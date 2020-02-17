package org.mirrentools.ost.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.Vertx;
	
/**
 * 本地Vertx管理器
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public class LocalDataVertx {
	/** 数据 */
	private static Map<String, Vertx> VERTX_MAP = new ConcurrentHashMap<>();

	/**
	 * 添加一个Socket,如果已经存在Socket就替换
	 * 
	 * @param key
	 *          Socket的id通常对应 WebSocket的写id
	 * @param vertx
	 * @return 如果参数key或参数vertx==null则返回null
	 */
	public static Vertx put(String key, Vertx vertx) {
		if (key == null || vertx == null) {
			return null;
		}
		Vertx result = VERTX_MAP.put(key, vertx);
		return result;
	}

	/**
	 * 添加一个Socket,如果已经存在Socket就返回以存在的
	 * 
	 * @param key
	 *          Socket的id通常对应 WebSocket的写id
	 * @param vertx
	 * @return 如果参数key或参数vertx==null则返回null
	 */
	public static Vertx putIfAbsent(String key, Vertx vertx) {
		if (key == null || vertx == null) {
			return null;
		}
		Vertx result = VERTX_MAP.putIfAbsent(key, vertx);
		return result;
	}

	/**
	 * 获取Socket
	 * 
	 * @param key
	 *          Socket的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static Vertx get(String key) {
		if (key == null) {
			return null;
		}
		return VERTX_MAP.get(key);
	}

	/**
	 * 获取Socket
	 * 
	 * @param key
	 *          Socket的id通常对应 WebSocket的写id
	 * @param defaultValue
	 *          如果为空就返回默认值
	 * @return 如果参数key==null则返回null
	 */
	public static Vertx get(String key, Vertx defaultValue) {
		if (key == null) {
			return null;
		}
		return VERTX_MAP.getOrDefault(key, defaultValue);
	}

	/**
	 * 删除Socket
	 * 
	 * @param key
	 *          Socket的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static Vertx remove(String key) {
		if (key == null) {
			return null;
		}
		return VERTX_MAP.remove(key);
	}

}
