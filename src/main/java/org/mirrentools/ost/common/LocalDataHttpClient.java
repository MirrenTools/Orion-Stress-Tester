package org.mirrentools.ost.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.http.HttpClient;

/**
 * 本地HttpClient管理器
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public class LocalDataHttpClient {
	/** 数据 */
	private static Map<String, HttpClient> HTTP_CLIENT_MAP = new ConcurrentHashMap<>();

	/**
	 * 添加一个HttpClient,如果已经存在HttpClient就替换
	 * 
	 * @param key
	 *          HttpClient的id通常对应 WebSocket的写id
	 * @param options
	 * @return 如果参数key或参数options==null则返回null
	 */
	public static HttpClient put(String key, HttpClient options) {
		if (key == null || options == null) {
			return null;
		}
		HttpClient result = HTTP_CLIENT_MAP.put(key, options);
		return result;
	}

	/**
	 * 添加一个HttpClient,如果已经存在HttpClient就返回以存在的
	 * 
	 * @param key
	 *          HttpClient的id通常对应 WebSocket的写id
	 * @param options
	 * @return 如果参数key或参数options==null则返回null
	 */
	public static HttpClient putIfAbsent(String key, HttpClient options) {
		if (key == null || options == null) {
			return null;
		}
		HttpClient result = HTTP_CLIENT_MAP.putIfAbsent(key, options);
		return result;
	}

	/**
	 * 获取HttpClient
	 * 
	 * @param key
	 *          HttpClient的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static HttpClient get(String key) {
		if (key == null) {
			return null;
		}
		return HTTP_CLIENT_MAP.get(key);
	}

	/**
	 * 获取HttpClient
	 * 
	 * @param key
	 *          HttpClient的id通常对应 WebSocket的写id
	 * @param defaultValue
	 *          如果为空就返回默认值
	 * @return 如果参数key==null则返回null
	 */
	public static HttpClient get(String key, HttpClient defaultValue) {
		if (key == null) {
			return null;
		}
		return HTTP_CLIENT_MAP.getOrDefault(key, defaultValue);
	}

	/**
	 * 删除HttpClient
	 * 
	 * @param key
	 *          HttpClient的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static HttpClient remove(String key) {
		if (key == null) {
			return null;
		}
		return HTTP_CLIENT_MAP.remove(key);
	}

}
