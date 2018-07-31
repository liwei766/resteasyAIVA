/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：HankakuKanaConverter.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.ffmpeg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 外部プロセスffmpegを起動する
 * 本クラスのメソッドはコマンドを生成して実行するだけなのでプロセス数の制御は行っていません。
 * 同時実行数などを制御したい場合は呼び出し側で制御してください。
 */
@Component
public class FFmpeg {

	/** ffmpeg実行ファイルの場所 */
	@Value( "${ffmpeg.path}" )
	private String ffmpeg;

	/** ffmpegオプション ログレベル */
	@Value( "${ffmpeg.option.log.level}" )
	private String optLogLevel;

	/** ffmpegオプション ビットレート */
	@Value( "${ffmpeg.option.audio.bit.rate}" )
	private String optBitRate;

	/** ffmpegオプション コーデック */
	@Value( "${ffmpeg.option.audio.codec}" )
	private String optCodec;

	/** ffmpegオプション フォーマット */
	@Value( "${ffmpeg.option.format}" )
	private String optFormat;

	/** ffmpegオプション フィルター */
	@Value( "${ffmpeg.option.filter.complex}" )
	private String optFilterComplex;


	/** ffmpegオプション アップロード用フォーマット */
	@Value( "${ffmpeg.option.upload.format}" )
	private String optUploadFormat;

	/** ffmpegオプション アップロード用チャンネル数 */
	@Value( "${ffmpeg.option.upload.audio.channels}" )
	private String optUploadAudioChannels;

	/** ffmpegオプション アップロード用オーディオサンプリングレート */
	@Value( "${ffmpeg.option.upload.audio.rate}" )
	private String optUploadAudioRate;

	/** ffmpegオプション アップロード用コーデック */
	@Value( "${ffmpeg.option.upload.audio.codec}" )
	private String optUploadAudioCodec;


	/**
	 * 音声ファイルを圧縮する
	 * @param inputFile 入力ファイルのパス(フルパス)
	 * @param outputFile 出力ファイルのパス(フルパス)
	 * @throws Exception
	 */
	public void encode(Path inputFile, Path outputFile) throws Exception {

		List<String> command = new ArrayList<>();
		command.add(ffmpeg);

		// 同名ファイル上書き
		command.add(Option.OVERWRITE.getValue());

		// ログレベル
		command.add(Option.LOG_LEVEL.getValue());
		command.add(optLogLevel);

		// 入力ファイル
		command.add(Option.INPUT.getValue());
		command.add(inputFile.toString());

		// ビットレート
		command.add(Option.AUDIO_BIT_RATE.getValue());
		command.add(optBitRate);

		// コーデック
		command.add(Option.AUDIO_CODEC.getValue());
		command.add(optCodec);

		// 出力フォーマット
		command.add(Option.FORMAT.getValue());
		command.add(optFormat);

		// 出力ファイル名
		command.add(outputFile.toString());

		// コマンド実行
		doExecute(command);
	}

	/**
	 * 入力ファイルを一つの音声ファイルにマージする
	 * @param inputFile 入力ファイルのパス(フルパス)
	 * @param outputFile 出力ファイルのパス(フルパス)
	 * @throws Exception
	 */
	public void marge(List<Path> inputFiles, Path outputFile) throws Exception {

		List<String> command = new ArrayList<>();
		command.add(ffmpeg);

		// 同名ファイル上書き
		command.add(Option.OVERWRITE.getValue());

		// ログレベル
		command.add(Option.LOG_LEVEL.getValue());
		command.add(optLogLevel);


		// 入力ファイル
		for(Path each : inputFiles) {
			// 入力ファイル
			command.add(Option.INPUT.getValue());
			command.add(each.toString());
		}

		// ファイル結合フィルタ
		command.add(Option.FILTER_COMPLEX.getValue());
		command.add(String.format(optFilterComplex, inputFiles.size()));

		// ビットレート
		command.add(Option.AUDIO_BIT_RATE.getValue());
		command.add(optBitRate);

		// コーデック
		command.add(Option.AUDIO_CODEC.getValue());
		command.add(optCodec);

		// 出力フォーマット
		command.add(Option.FORMAT.getValue());
		command.add(optFormat);

		// 出力ファイル名
		command.add(outputFile.toString());

		// コマンド実行
		doExecute(command);
	}

	/**
	 * 入力ファイルをWAVE形式の音声ファイルに変換する
	 * @param inputFile 入力ファイルのパス(フルパス)
	 * @param outputFile 出力ファイルのパス(フルパス)
	 * @throws Exception
	 */
	public void conversion(Path inputFile, Path outputFile) throws Exception {

		List<String> command = new ArrayList<>();
		command.add(ffmpeg);

		// 同名ファイル上書き
		command.add(Option.OVERWRITE.getValue());

		// ログレベル
		command.add(Option.LOG_LEVEL.getValue());
		command.add(optLogLevel);

		// 入力ファイル
		command.add(Option.INPUT.getValue());
		command.add(inputFile.toString());

		// 音声ファイルのみ
		command.add(Option.AUDIO_ONLY.getValue());

		// チャンネル数
		command.add(Option.AUDIO_CHANNELS.getValue());
		command.add(optUploadAudioChannels);

		//サンプリングレート
		command.add(Option.AUDIO_RATE.getValue());
		command.add(optUploadAudioRate);


		// コーデック
		command.add(Option.AUDIO_CODEC.getValue());
		command.add(optUploadAudioCodec);


		// 出力フォーマット
		command.add(Option.FORMAT.getValue());
		command.add(optUploadFormat);


		// 出力ファイル名
		command.add(outputFile.toString());

		// コマンド実行
		doExecute(command);

	}

	/**
	 * コマンドを実行する
	 * @param command コマンド
	 * @throws Exception 異常終了時
	 */
	private void doExecute(List<String> command) throws Exception {

		// コマンド生成
		ProcessBuilder pb = new ProcessBuilder(command);

		// エラーストリームを標準出力にマージする
		pb.redirectErrorStream(true);

		// コマンド実行
		Process proc = pb.start();

		// 標準出力の内容を取得
		StringBuilder streamText = new StringBuilder();

		try(BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));) {
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				streamText.append(line);
			}
		}

		// 終了コードを取得する
		int exitCode = proc.waitFor();

		// 異常終了の場合は例外をスローする
		if (exitCode > 0) throw new Exception(streamText.toString());
	}

}
