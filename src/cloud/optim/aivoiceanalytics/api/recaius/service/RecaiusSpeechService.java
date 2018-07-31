/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusSpeechService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cloud.optim.aivoiceanalytics.api.recaius.Lexicon;
import cloud.optim.aivoiceanalytics.api.recaius.SpeechResultType;
import cloud.optim.aivoiceanalytics.api.recaius.UserLexicon;
import cloud.optim.aivoiceanalytics.api.recaius.result.CreateModelResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.LexiconGetResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.RecaiusResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechStartResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.UserLexiconsResult;
import cloud.optim.aivoiceanalytics.api.recaius.util.Util;
import cloud.optim.aivoiceanalytics.api.util.JsonUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;


/**
 * RecaiusSpeechService実装.<br/>
 */
@Component
public class RecaiusSpeechService {

	///** Commons Logging instance.  */
	@SuppressWarnings("unused")
	private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** リカイアス音声解析URL */
	@Value( "${recaius.url.speech}" )
	private String recaiusSpeechUrl;

	/** リカイアスフラッシュ問い合わせ間隔 */
	@Value( "${recaius.speech.flush.interval}" )
	private Long speechFlushInterval;

  /** リカイアスベースモデルID. */
  @Value("${recaius.service.speech.base.model.id}")
  private String baseModelId;

	/**
	 * リカイアス音声認識セッション開始
	 * @return 音声認識ID
	 * @throws Exception
	 */
	public String startSession(String token, int energyThreshold, int modelId) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);
		header.put("Content-Type", "application/json");

		// ボディ部の生成
		Map<String, Object> body = new HashMap<>();
		body.put("energy_threshold", energyThreshold);
		body.put("result_type", "nbest");
		body.put("model_id", modelId);
		body.put("result_count", "1");
		body.put("data_log", "1");

		// リカイアス音声認識開始APIを呼ぶ
		RecaiusResult response = Util.communicate(
			HttpMethod.POST, recaiusSpeechUrl + "/voices", header, JsonUtil.toJsonBytes(body));

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_START_ERROR, null, response.getErrorDetails() ));
		}

		// 音声認識IDを返す
		SpeechStartResult speechStartResponse = response.getResponse(SpeechStartResult.class);
		return speechStartResponse.getUuid();
	}

	/**
	 * 音声データをリカイアスへ送信する
	 * @return 解析結果がある場合は解析結果を返す
	 * @throws Exception
	 */
	public SpeechResult sendData(String token, String uuid, int voiceId, byte[] data) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);
		header.put("Content-Type", "multipart/form-data");

		// ボディ部の生成
		String boundary = "--" + Util.generateBoundary() + "\r\n";
		StringBuilder dataHeader = new StringBuilder();
		dataHeader
			.append(boundary)
			.append("Content-Disposition: form-data; name=\"voice_id\"\r\n\r\n")
			.append(voiceId).append("\r\n")
			.append(boundary)
			.append("Content-Disposition: form-data; name=\"voice\"\r\n")
			.append("Content-Type: application/octet-stream\r\n\r\n");

		byte[] body = dataHeader.toString().getBytes();
		if(data != null) body = ArrayUtils.addAll(body, data);
		body = ArrayUtils.addAll(body, boundary.getBytes());

		// リカイアス音声送信APIを呼ぶ
		RecaiusResult response = Util.communicate(
			HttpMethod.PUT, recaiusSpeechUrl + "/voices/" + uuid, header, body);

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_SEND_DATA_ERROR, null, response.getErrorDetails() ));
		}

		List<SpeechNBestResult> speechResult = response.getResponse(new TypeReference<List<SpeechNBestResult>>() {});

		SpeechResult result = new SpeechResult();
		result.setResultList(speechResult == null ? new ArrayList<>() : speechResult);
		result.setUseTime(response.getTime());
		return result;
	}

	/**
	 * 送った分の音声の最終結果を取得する
	 * @return 最終解析結果
	 * @throws Exception
	 */
	public SpeechResult flush(String token, String uuid, int voiceId) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);
		header.put("Content-Type", "application/json");

		// ボディ部の生成
		Map<String, Object> body = new HashMap<>();
		body.put("voice_id", voiceId);

		long useTime = 0;
		List<SpeechNBestResult> resultList = new ArrayList<>();

		while(true) {

			// リカイアス音声データ送信終了APIを呼ぶ
			RecaiusResult response = Util.communicate(
				HttpMethod.PUT, recaiusSpeechUrl + "/voices/" + uuid + "/flush", header, JsonUtil.toJsonBytes(body));

			// エラーの場合は例外をスローする
			if(response.isAbendError()) {
				throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null, response.getErrorDetails() ));
			}
			if(response.isError()) {
				throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_FLUSH_ERROR, null, response.getErrorDetails() ));
			}

			// 利用時間の設定
			useTime += response.getTime();

			// レスポンスコードが200でない場合(おそらく結果が無い場合は204が返ってくる)は一定時間待機する
			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {

				List<SpeechNBestResult> speechResult = response.getResponse(new TypeReference<List<SpeechNBestResult>>() {});

				// RESULTのテキストを取得する
				if (speechResult != null) {
					resultList.addAll(speechResult);
				}

				// NO_DATAが返ってきたら終了
				if(isContainsNoData(speechResult)) break;
			}

			// 結果が返らない場合は一定の間隔を置いて再度フラッシュAPIを呼ぶ
			Thread.sleep(speechFlushInterval);
		}

		// 最終結果を生成して返す。
		SpeechResult result = new SpeechResult();
		result.setResultList(resultList);
		result.setUseTime(useTime);
		return result;
	}

	/**
	 * リカイアス音声認識終了する
	 * @param token
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public void stopSession(String token, String uuid) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);

		// リカイアス音声認識終了APIを呼ぶ
		RecaiusResult response = Util.communicate(HttpMethod.DELETE, recaiusSpeechUrl + "/voices/" + uuid, header, null);

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_END_ERROR, null, response.getErrorDetails() ));
		}
	}

	/**
	 *
	 * @param speechResult
	 * @return
	 */
	private boolean isContainsNoData(List<SpeechNBestResult> speechResult) {

		if (speechResult == null || speechResult.isEmpty()) return false;

		for(SpeechNBestResult each : speechResult) {
			if (SpeechResultType.NO_DATA.toString().equals(each.getType())) return true;
		}
		return false;
	}

	/**
	 * リカイアスユーザ辞書登録単語一覧取得
	 * @param token
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public LexiconGetResult getLexicon(String token, int modelId) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);

		// リカイアスユーザ辞書取得APIを呼ぶ
		RecaiusResult response = Util.communicate(HttpMethod.GET, recaiusSpeechUrl + "/userlexicons/" + modelId + "/contents", header, null);

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_LEXICON_GET_ERROR, null, response.getErrorDetails() ));
		}

		return response.getResponse(LexiconGetResult.class);
	}

	/**
	 * リカイアスユーザ辞書登録単語一括登録
	 * @param token
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public RecaiusResult updateLexicon(String token, int modelId, List<Lexicon> lexicons) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);
		header.put("Content-Type", "application/json");

		// ボディ部の生成
		Map<String, Object> body = new HashMap<>();
		body.put("ulex", lexicons);

		// リカイアスユーザ辞書一括登録APIを呼ぶ
		RecaiusResult result = Util.communicate(HttpMethod.PUT, recaiusSpeechUrl + "/userlexicons/" + modelId + "/contents", header, JsonUtil.toJsonBytes(body));

		// エラーの場合は例外をスローする
		if(result.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null, result.getErrorDetails() ));
		}
		if(result.isError() && result.getResponseCode() != HttpURLConnection.HTTP_BAD_REQUEST) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_LEXICON_UPDATE_ERROR, null, result.getErrorDetails() ));
		}

		return result;
	}

  /**
   * リカイアスモデルID生成.
   *
   * @param token 認証トークン
   * @param baseModelId ベースモデルID
   * @return モデルID
   * @throws Exception リカイアスAPIエラー時
   */
  public int createModelId(final String token, final int baseModelId) throws Exception {
    Map<String, String> header = new HashMap<>();
    header.put("X-Token", token);
    header.put("Content-Type", "application/json");

    Map<String, Object> body = new HashMap<>();
    body.put("base_model_id", baseModelId);

    final String uri = recaiusSpeechUrl + "/userlexicons";
    RecaiusResult response =
        Util.communicate(HttpMethod.POST, uri, header, JsonUtil.toJsonBytes(body));

    if (response.isAbendError()) {
      throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null,
          response.getErrorDetails()));
    }
    if (response.isError()) {
      throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_CREATE_MODEL_ID_ERROR,
          null, response.getErrorDetails()));
    }

    CreateModelResult result = response.getResponse(CreateModelResult.class);
    return result.getModelId();
  }

  /**
   * リカイアスモデルID一覧取得.
   *
   * @param token 認証トークン
   * @return モデルIDリスト
   * @throws Exception リカイアスAPIエラー時
   */
  public List<UserLexicon> getModelIdList(final String token) throws Exception {
    Map<String, String> header = new HashMap<>();
    header.put("X-Token", token);
    header.put("Content-Type", "application/json");

    final String uri = recaiusSpeechUrl + "/userlexicons";
    RecaiusResult response = Util.communicate(HttpMethod.GET, uri, header, null);

    if (response.isAbendError()) {
      throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null,
          response.getErrorDetails()));
    }
    if (response.isError()) {
      throw new RestException(
          new RestResult(ResponseCode.RECAIUS_SPEECH_GET_BASE_MODEL_ID_LIST_ERROR, null,
              response.getErrorDetails()));
    }

    // 後続の　getResponse で JSON を展開するときに Exception が発生する
    // 現状のクラスではこのタイプの JSON がうまく処理できないので実装に合わせて無理やり整形する
    // TODO 関連クラスの見直しは必要
    StringBuilder sb = new StringBuilder("{ \"userLexicons\":");
    sb.append(response.getResponseBody());
    sb.append("}");
    response.setResponseBody(sb.toString());
    //

    UserLexiconsResult result = response.getResponse(UserLexiconsResult.class);
    return result.getUserLexicons();
  }

  /**
   * リカイアスモデルID生成数超過判定.
   *
   * @param token 認証トークン
   * @return 登録されているモデルIDが10以上の場合true
   * @throws Exception リカイアスAPIエラー時
   */
  public boolean isOverflowModelId(final String token) throws Exception {
    List<UserLexicon> result = this.getModelIdList(token);
    //log.debug("isOverflowModelId:UserLexicon:size = " + result.size());
    if (result.size() >= 10) {
      return true;
    }
    return false;
  }

  /**
   * リカイアスモデルID破棄.
   *
   * @param token 認証トークン
   * @param modelId 対象モデルID
   * @throws Exception リカイアスAPIエラー時
   */
  public void destroyModelId(final String token, final int modelId) throws Exception {
    Map<String, String> header = new HashMap<>();
    header.put("X-Token", token);

    final String uri = recaiusSpeechUrl + "/userlexicons/" + Integer.toString(modelId);
    RecaiusResult response = Util.communicate(HttpMethod.DELETE, uri, header, null);

    if (response.isAbendError()) {
      throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_ABEND_ERROR, null,
          response.getErrorDetails()));
    }
    if (response.isError()) {
      throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_DESTROY_MODEL_ID_ERROR,
          null, response.getErrorDetails()));
    }
  }

}
