package org.mirrentools.ost.common;

import org.mirrentools.ost.enums.OstCommand;

import io.vertx.core.json.JsonObject;

/**
 * 返回结果格式化
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public final class ResultFormat {

	/**
	 * 格式化成功返回结果
	 * 
	 * @param data
	 * @return
	 */
	public static String success(OstCommand command, Object data) {
		JsonObject result = new JsonObject();
		result.put("code", command.value());
		result.put("msg", "成功!");
		if (data != null) {
			result.put("data", data);
		} else {
			result.putNull("data");
		}
		return result.toString();
	}

	/**
	 * 
	 * @param code
	 * @param msg
	 * @param data
	 * @return
	 */
	public static String failed(OstCommand command, String msg, Object data) {
		JsonObject result = new JsonObject();
		result.put("code", command.value());
		result.put("msg", msg == null ? "操作失败,请稍后重试!" : msg);
		if (data != null) {
			result.put("data", data);
		} else {
			result.putNull("data");
		}
		return result.toString();
	}

}
