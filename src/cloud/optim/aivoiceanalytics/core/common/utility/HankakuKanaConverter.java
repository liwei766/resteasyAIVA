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
package cloud.optim.aivoiceanalytics.core.common.utility;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 半角カナを全角カナに変換する
 * @author raifuyor
 *
 */
public class HankakuKanaConverter {
	/** 半角カナ文字コード値の最小値 */
	private static int MIN_CODE = 65377;

	/** 半角カナ文字コード値の最大値 */
	private static int MAX_CODE = 65439;

	/** 半角カナ濁点コード値 */
	private static int HALF_DAKUTENN_CODE = 65438;

	/** 半角カナ半濁点コード値 */
	private static int HALF_HANDAKUTENN_CODE = 65439;

	/** 全角カナ濁点コード値 */
	private static int FULL_DAKUTENN_CODE = 12443;

	/** 全角カナ半濁点コード値 */
	private static int FULL_HANDAKUTENN_CODE = 12444;

	/** 濁音、半濁音になる可能性のある文字 */
	private static final List<Integer> DAKUON_LIST = Collections.unmodifiableList(Arrays.asList(new Integer[]{
			65395, // ｳ
			65398, 65399, 65400, 65401, 65402, // ｶｷｸｹｺ
			65403, 65404, 65405, 65406, 65407, // ｻｼｽｾｿ
			65408, 65409, 65410, 65411, 65412, // ﾀﾁﾂﾃﾄ
			65418, 65419, 65420, 65421, 65422  // ﾊﾋﾌﾍﾎ
	}));

	/** 半濁音になる可能性のある文字 */
	private static final List<Integer> HANDAKUON_LIST = Collections.unmodifiableList(Arrays.asList(new Integer[]{
			65418, 65419, 65420, 65421, 65422  // ﾊﾋﾌﾍﾎ
	}));


	/**
	 * テキスト中に含まれる半角カナ文字を全角カナに変換する<br>
	 * 変換対象は以下の文字<br>
	 * ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉ<br>
	 * ﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾚﾙﾚﾛﾜｦﾝ<br>
	 * ｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞ<br>
	 * ﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟｳﾞ<br>
	 * ｧｨｩｪｫｯｬｭｮ<br>
	 * ﾞﾟｰ｡､･｢｣<br>
	 * @param text テキスト
	 * @return 変換結果のテキスト
	 */
	public static String convert(String text) {

		if (StringUtils.isEmpty(text)) return text;

		int joinCount = 0;
		int [] result = new int[text.codePointCount(0, text.length())];
		for (int i = 0, j = 0, code; i < text.length(); i += Character.charCount(code)) {
			code = text.codePointAt(i);
			//System.out.println(code + "\t" + new String(Character.toChars(code)));
			// 半角カナ文字以外の場合は変換しないで次の文字に進む
			if (code < MIN_CODE  || MAX_CODE < code) {
				result[j++] = code;
				continue;
			}

			// 濁点単品の場合は全角濁点のコードを設定する
			if (code == HALF_DAKUTENN_CODE) {
				result[j++] = FULL_DAKUTENN_CODE;
				continue;
			}

			// 濁点単品の場合は全角濁点のコードを設定する
			if (code == HALF_HANDAKUTENN_CODE) {
				result[j++] = FULL_HANDAKUTENN_CODE;
				continue;
			}

			// 濁音、半濁音にならない文字の場合は全角カナに変換して次の文字に進む
			if (!DAKUON_LIST.contains(code)) {
				result[j++] = normalize(code);
				continue;
			}

			// 最後の文字の場合は全角カナ文字に変換して次の文字に進む
			if (i + Character.charCount(code) >= text.length()) {
				result[j++] = normalize(code);
				continue;
			}

			// 濁音、半濁音なる文字の場合次の文字が半角濁点、半角半濁点でない場合は全角カナ文字に変換して次の文字に進む
			int nextCode = text.codePointAt(i + Character.charCount(code));
			if (nextCode != HALF_DAKUTENN_CODE && nextCode != HALF_HANDAKUTENN_CODE) {
				result[j++] = normalize(code);
				continue;
			}

			// 次の文字が半濁点の場合、半濁点にならない文字の場合は全角カナ文字に変換して次の文字に進む
			if (nextCode == HALF_HANDAKUTENN_CODE && !HANDAKUON_LIST.contains(code)) {
				result[j++] = normalize(code);
				continue;
			}

			// 濁音、半濁音の場合は
			joinCount++;
			result[j++] = normalize(code, nextCode);

			// 濁点、半濁点の分カウンタをインクリメントする
			i += Character.charCount(nextCode);
		}

		return new String(result, 0, result.length - joinCount);
	}

	/**
	 * 半角カナ文字を全角カナ文字に変換する
	 * @param code 文字コード
	 * @return 変換後の文字コード
	 */
	private static int normalize(int... code) {
		return Normalizer.normalize(new String(code, 0, code.length), Normalizer.Form.NFKC).codePointAt(0);
	}
}
