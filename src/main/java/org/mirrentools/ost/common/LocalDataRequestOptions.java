package org.mirrentools.ost.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mirrentools.ost.model.OstRequestOptions;

/**
 * 本地{@link org.mirrentools.ost.model.OstRequestOptions}管理器
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public class LocalDataRequestOptions {
	/** 数据 */
	private static Map<String, OstRequestOptions> OPTIONS_MAP = new ConcurrentHashMap<>();

	/**
	 * 添加一个RequestOptions,如果已经存在RequestOptions就替换
	 * 
	 * @param key
	 *          RequestOptions的id通常对应 WebSocket的写id
	 * @param options
	 * @return 如果参数key或参数options==null则返回null
	 */
	public static OstRequestOptions put(String key, OstRequestOptions options) {
		if (key == null || options == null) {
			return null;
		}
		OstRequestOptions result = OPTIONS_MAP.put(key, options);
		return result;
	}

	/**
	 * 添加一个RequestOptions,如果已经存在RequestOptions就返回以存在的
	 * 
	 * @param key
	 *          RequestOptions的id通常对应 WebSocket的写id
	 * @param options
	 * @return 如果参数key或参数options==null则返回null
	 */
	public static OstRequestOptions putIfAbsent(String key, OstRequestOptions options) {
		if (key == null || options == null) {
			return null;
		}
		OstRequestOptions result = OPTIONS_MAP.putIfAbsent(key, options);
		return result;
	}

	/**
	 * 获取RequestOptions
	 * 
	 * @param key
	 *          RequestOptions的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static OstRequestOptions get(String key) {
		if (key == null) {
			return null;
		}
		return OPTIONS_MAP.get(key);
	}

	/**
	 * 获取RequestOptions
	 * 
	 * @param key
	 *          RequestOptions的id通常对应 WebSocket的写id
	 * @param defaultValue
	 *          如果为空就返回默认值
	 * @return 如果参数key==null则返回null
	 */
	public static OstRequestOptions get(String key, OstRequestOptions defaultValue) {
		if (key == null) {
			return null;
		}
		return OPTIONS_MAP.getOrDefault(key, defaultValue);
	}

	/**
	 * 删除RequestOptions
	 * 
	 * @param key
	 *          RequestOptions的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static OstRequestOptions remove(String key) {
		if (key == null) {
			return null;
		}
		return OPTIONS_MAP.remove(key);
	}

}
