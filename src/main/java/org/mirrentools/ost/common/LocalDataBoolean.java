package org.mirrentools.ost.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地Boolean管理器
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public class LocalDataBoolean {
	/** 数据 */
	private static Map<String, Boolean> BOOLEAN_MAP = new ConcurrentHashMap<>();

	/**
	 * 添加一个Boolean,如果已经存在Boolean就替换
	 * 
	 * @param key
	 *          Boolean的id通常对应 WebSocket的写id
	 * @param flag
	 * @return 如果参数key或参数flag==null则返回null
	 */
	public static Boolean put(String key, Boolean flag) {
		if (key == null || flag == null) {
			return null;
		}
		Boolean result = BOOLEAN_MAP.put(key, flag);
		return result;
	}

	/**
	 * 添加一个Boolean,如果已经存在Boolean就返回以存在的
	 * 
	 * @param key
	 *          Boolean的id通常对应 WebSocket的写id
	 * @param flag
	 * @return 如果参数key或参数flag==null则返回null
	 */
	public static Boolean putIfAbsent(String key, Boolean flag) {
		if (key == null || flag == null) {
			return null;
		}
		Boolean result = BOOLEAN_MAP.putIfAbsent(key, flag);
		return result;
	}

	/**
	 * 获取Boolean
	 * 
	 * @param key
	 *          Boolean的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static Boolean get(String key) {
		if (key == null) {
			return null;
		}
		return BOOLEAN_MAP.get(key);
	}

	/**
	 * 获取Boolean
	 * 
	 * @param key
	 *          Boolean的id通常对应 WebSocket的写id
	 * @param defaultValue
	 *          如果为空就返回默认值
	 * @return 如果参数key==null则返回null
	 */
	public static Boolean get(String key, Boolean defaultValue) {
		if (key == null) {
			return null;
		}
		return BOOLEAN_MAP.getOrDefault(key, defaultValue);
	}

	/**
	 * 删除Boolean
	 * 
	 * @param key
	 *          Boolean的id通常对应 WebSocket的写id
	 * @return 如果参数key==null则返回null
	 */
	public static Boolean remove(String key) {
		if (key == null) {
			return null;
		}
		return BOOLEAN_MAP.remove(key);
	}

}
