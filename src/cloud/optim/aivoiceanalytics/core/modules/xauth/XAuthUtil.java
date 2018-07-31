/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：XAuthUtil.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.xauth;

import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class XAuthUtil {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final String join(final Collection collection, final String joinStr) {
		if (CollectionUtils.isEmpty(collection)) {
			return "";
		}
		if (joinStr == null) {
			throw new IllegalArgumentException("join string is null.");
		}

		Object[] arr = toArray(collection, Object.class);
		return arrayJoin(arr, joinStr);
	}

	public static final String arrayJoin(final Object[] arr, final String joinStr) {
		if (arr == null || arr.length < 1) {
			return StringUtils.EMPTY;
		}
		final StringBuffer sb = new StringBuffer(String.valueOf(arr[0]));
		for (int i = 1, len = arr.length; i < len; i++) {
			sb.append(StringUtils.isNotEmpty(joinStr) ? joinStr : StringUtils.EMPTY).append(String.valueOf(arr[i]));
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] toArray(final Collection<T> collection, final Class<T> clazz) {
		if (collection == null) {
			return null;
		}
		final T[] arr = (T[]) Array.newInstance(clazz, collection.size());
		return collection.toArray(arr);
	}
}
