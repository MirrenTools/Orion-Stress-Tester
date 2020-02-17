package org.mirrentools.ost.enums;

/**
 * 操作指令
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public enum OstCommand {
	/** 取消操作:-1 */
	CANCEL(-1),
	/** 提交测试:1 */
	SUBMIT_TEST(1),
	/** 开始的前测试:2,成功返回1,失败返回0 */
	BEFORE_REQUEST_TEST(2),
	/** 测试响应:3 */
	TEST_RESPONSE(3),
	/** 日志输出:4 */
	TEST_LOG_RESPONSE(4),
	/** 测试完成 */
	TEST_COMPLETE(99),
	/** 无效的请求参数或缺失必填的参数:412 */
	MISSING_PARAMETER(412),
	/** 未知异常:500 */
	ERROR(500),
	/** JVM的内存等指标:1000 */
	JVM_METRIC(1000);

	/** 状态码 */
	private int code;

	private OstCommand(int code) {
		this.code = code;
	}

	/**
	 * 获得验证码
	 * 
	 * @return
	 */
	public int value() {
		return this.code;
	}
}
