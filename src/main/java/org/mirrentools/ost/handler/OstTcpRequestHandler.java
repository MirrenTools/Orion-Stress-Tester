package org.mirrentools.ost.handler;

import org.mirrentools.ost.model.OstRequestOptions;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

/**
 * TCP的请求处理器
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public interface OstTcpRequestHandler {
	/**
	 * 执行TCP测试请求
	 * @param netClient
	 * @param options
	 * @param handler
	 */
	static void requestAbs(NetClient netClient, OstRequestOptions options,Handler<AsyncResult<NetSocket>> handler) {
		netClient.connect(options.getPort(), options.getHost(),options.getServerName(), handler);
	}
	
	
	/**
	 * 执行TCP请求
	 * 
	 * @param netClient
	 *          TCP客户端
	 * @param options
	 *          请求信息
	 * @param succeededHandler
	 *          成功的处理器
	 * @param exceptionHandler
	 *          失败的处理器
	 */
	static void request(NetClient netClient, OstRequestOptions options, Handler<NetSocket> succeededHandler, Handler<Throwable> exceptionHandler) {
		try {
			request(netClient, options.getPort(), options.getHost(), options.getServerName(), succeededHandler, exceptionHandler);
		} catch (Exception e) {
			if (exceptionHandler != null) {
				exceptionHandler.handle(e);
			}
		}
	}

	/**
	 * 执行TCP请求
	 * 
	 * @param netClient
	 *          TCP客户端
	 * @param port
	 *          端口号
	 * @param host
	 *          主机地址
	 * @param succeededHandler
	 *          成功的处理器
	 * @param exceptionHandler
	 *          失败的处理器
	 */
	static void request(NetClient netClient, int port, String host, Handler<NetSocket> succeededHandler, Handler<Throwable> exceptionHandler) {
		request(netClient, port, host, null, succeededHandler, exceptionHandler);
	}

	/**
	 * 执行TCP请求
	 * 
	 * @param netClient
	 *          TCP客户端
	 * @param port
	 *          端口号
	 * @param host
	 *          主机地址
	 * @param serverName
	 *          主机名称
	 * @param succeededHandler
	 *          成功的处理器
	 * @param exceptionHandler
	 *          失败的处理器
	 */
	static void request(NetClient netClient, int port, String host, String serverName, Handler<NetSocket> succeededHandler, Handler<Throwable> exceptionHandler) {
		try {
			netClient.connect(port, host, serverName, res -> {
				if (res.succeeded()) {
					if (succeededHandler != null) {
						succeededHandler.handle(res.result());
					}
				} else {
					if (exceptionHandler != null) {
						exceptionHandler.handle(res.cause());
					}
				}
			});
		} catch (Exception e) {
			if (exceptionHandler != null) {
				exceptionHandler.handle(e);
			}
		}
	}
}
