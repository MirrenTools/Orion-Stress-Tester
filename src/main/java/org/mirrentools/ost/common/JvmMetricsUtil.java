package org.mirrentools.ost.common;

/**
 * JVM的相关指标
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public class JvmMetricsUtil {

	/**
	 * 获取处理器数量
	 * 
	 * @return
	 */
	public static int availableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * 可用内存
	 * 
	 * @return
	 */
	public static long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * 最大内存
	 * 
	 * @return
	 */
	public static long maxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * 剩余内存
	 * 
	 * @return
	 */
	public static long freeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

}
