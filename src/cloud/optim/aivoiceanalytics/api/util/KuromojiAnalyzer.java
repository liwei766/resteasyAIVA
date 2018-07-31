/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KuromojiAnalyzer.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.atilika.kuromoji.ipadic.Tokenizer.Builder;

/**
 * 形態素解析にkuromojiを利用した形態素解析エンジン
 * @author raifuyor
 *
 */
public class KuromojiAnalyzer implements MorphologicalAnalyzer{

	public static final String SYMBOL_REGEX = "^[ -/:-@\\[-\\`\\{-\\~’‘“”＃＄％＆－―‐～＾；：，、．。｡､？！＠／￥＝￣｜＿＋＊（）［］｛｝＜＞「」『』【】〔〕〈〉《》　		]+$";

	/** 抽出対象外品詞 */
	private List<String> ngPartOfSpeech;

	/** 抽出対象の品詞  */
	private String extractPartOfSpeech;

	/** ユーザ辞書ファイルパス */
	private String userDictionary;

	/** Tokenizer */
	private Tokenizer tokenizer;

	/**
	 *
	 * @param ngPartOfSpeech
	 * @param extractPartOfSpeech
	 * @param tokenizer
	 * @throws Exception
	 */
	public KuromojiAnalyzer(List<String> ngPartOfSpeech, String extractPartOfSpeech, String userDictionary) throws Exception {
		this.ngPartOfSpeech = ngPartOfSpeech;
		this.extractPartOfSpeech = extractPartOfSpeech;
		this.userDictionary = userDictionary;
		reloadUserDictionary();
	}

	/**
	 * ユーザ辞書を読み直す
	 * @throws IOException
	 */
	@Override
	public void reloadUserDictionary() throws IOException {
		Builder builder = new Builder();
		if(!StringUtils.isEmpty(userDictionary) && Files.exists(Paths.get(userDictionary))) {
			try (InputStream inputStream = Files.newInputStream(Paths.get(userDictionary))) {
				builder.userDictionary(inputStream);
			}
		}
		this.tokenizer = builder.build();
	}

	@Override
	public  List<String> extractNouns(String text, boolean reverse) {

		List<Token> tokens = tokenizer.tokenize(text);

		// 抽出順序を逆転させる
		if(reverse) Collections.reverse(tokens);

		return tokens.stream()
				.filter(distinctByKey(token -> token.getSurface()))
				.filter(token -> {
					String pos1 = token.getPartOfSpeechLevel1();
					String posDetail = token.getPartOfSpeechLevel2() + "," + token.getPartOfSpeechLevel3() + "," + token.getPartOfSpeechLevel4();
					if(ngPartOfSpeech.stream().anyMatch(ng -> (pos1 + posDetail).contains(ng))) return false;
					return Arrays.asList(pos1.split(",", 0)).contains(extractPartOfSpeech);
				})
				.map(token -> token.getSurface())
				.filter(noun -> {
					// 抽出した名詞が全角・半角記号のみで構成されている場合は除外する
					if (noun == null) return false;
					return !noun.replaceAll(SYMBOL_REGEX, "").isEmpty();
				})
				.collect(Collectors.toList());
	}

	/**
	 * フィラー品詞を除去する
	 * @param text 解析テキスト
	 * @throws IOException
	 */
	@Override
	public  String removeFiller(String text) {

		List<Token> tokens = tokenizer.tokenize(text);

		List<String> removeList = tokens.stream()
						.filter(token -> {
							String pos1 = token.getPartOfSpeechLevel1();
							String posDetail = token.getPartOfSpeechLevel2() + "," + token.getPartOfSpeechLevel3() + "," + token.getPartOfSpeechLevel4();
							if(ngPartOfSpeech.stream().anyMatch(ng -> (pos1 + posDetail).contains(ng))) return false;
							return true;
						})
						.map(token -> token.getSurface())
						.collect(Collectors.toList());

		
		return String.join("", removeList);

	}

	private <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
	    Map<Object, Boolean> map = new ConcurrentHashMap<>();
	    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
