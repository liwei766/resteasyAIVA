package cloud.optim.aivoiceanalytics.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	/** JSON<->Object変換用のObjectMapperのインスタンス */
	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * ObjectをJSON文字列に変換する
	 * @param object オブジェクト
	 * @return JSON文字列
	 * @throws Exception
	 */
	public static String toJson(Object object) throws Exception {
		if (object == null) return null;
		return mapper.writeValueAsString(object);
	}

	/**
	 * ObjectをJSON文字列のバイト配列に変換する
	 * @param object オブジェクト
	 * @return JSON文字列のバイト配列
	 * @throws Exception
	 */
	public static byte[] toJsonBytes(Object object) throws Exception {
		if (object == null) return null;
		return mapper.writeValueAsString(object).getBytes();
	}

	/**
	 * JSON文字列をオブジェクトに変換する
	 * @param json JSON文字列
	 * @param type 変換するクラス
	 * @return 変換したオブジェクト
	 * @throws Exception
	 */
	public static <T> T toObject(String json, Class<T> type) throws Exception {
		if (StringUtils.isEmpty(json)) return type.newInstance();
		return mapper.readValue(json, type);
	}

	/**
	 * JSON文字列をオブジェクトに変換する
	 * @param json JSON文字列
	 * @param type 変換するクラス
	 * @return 変換したオブジェクト
	 * @throws Exception
	 */
	public static <T> T toObject(String json, TypeReference<T> type) throws Exception {
		return mapper.readValue(json, type);
	}

	/**
	 * JSON文字列をリストに変換する
	 * @param json JSON文字列
	 * @param type 変換するクラス
	 * @return 変換したオブジェクト
	 * @throws Exception
	 */
	public static <T> List<T> toList(String json, Class<T> type) throws Exception {
		if (StringUtils.isEmpty(json)) return new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = mapper.readValue(json, ArrayList.class);

		List<T> result = new ArrayList<>();
		for (Map<String, Object> each : list) {
			T object = type.newInstance();
			BeanUtils.populate(object, each);
			result.add(object);
		}
		return result;
	}
}
