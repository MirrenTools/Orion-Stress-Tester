package org.mirrentools.ost.handler;

import java.util.List;

import org.mirrentools.ost.model.OstRequestOptions;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebsocketVersion;

/**
 * WebSocket请求处理器
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public interface OstWebSocketRequestHandler {
	/**
	 * 执行WebSocket测试请求
	 * 
	 * @param httpClient
	 *          请求客户端
	 * @param options
	 *          请求配置
	 * @param handler
	 *          返回结果
	 */
	static void requestAbs( HttpClient httpClient, OstRequestOptions options, Handler<AsyncResult<WebSocket>> handler) {
		httpClient.webSocketAbs(options.getUrl(), options.getHeaders(), options.getWebSocketVersion(), options.getSubProtocols(), handler);
	}

	/**
	 * WebSocket请求
	 * 
	 * @param httpClient
	 *          Http客户端
	 * @param options
	 *          请求的配置
	 * @param succeededHandler
	 *          请求成功
	 * @param exceptionHandler
	 *          请求失败
	 */
	static void requestAbs(HttpClient httpClient, OstRequestOptions options, Handler<WebSocket> succeededHandler, Handler<Throwable> exceptionHandler) {
		try {
			requestAbs(httpClient, options, null, succeededHandler, exceptionHandler);
		} catch (Exception e) {
			if (exceptionHandler != null) {
				exceptionHandler.handle(e);
			}
		}
	}

	/**
	 * WebSocket请求
	 * 
	 * @param httpClient
	 *          Http客户端
	 * @param options
	 *          请求的配置
	 * @param connectionHandler
	 *          连接建立处理器
	 * @param succeededHandler
	 *          请求成功
	 * @param exceptionHandler
	 *          请求失败
	 */
	static void requestAbs(HttpClient httpClient, OstRequestOptions options, Handler<HttpConnection> connectionHandler, Handler<WebSocket> succeededHandler, Handler<Throwable> exceptionHandler) {
		try {
			requestAbs(httpClient, options.getUrl(), options.getHeaders(), options.getWebSocketVersion(), options.getSubProtocols(), connectionHandler, succeededHandler, exceptionHandler);
		} catch (Exception e) {
			if (exceptionHandler != null) {
				exceptionHandler.handle(e);
			}
		}
	}

	/**
	 * WebSocket请求
	 * 
	 * @param httpClient
	 *          Http客户端
	 * @param url
	 *          请求的url
	 * @param headers
	 *          请求的Headers
	 * @param version
	 *          客户端的版本号
	 * @param subProtocols
	 *          子协议
	 * @param succeededHandler
	 *          请求成功
	 * @param exceptionHandler
	 *          请求失败
	 */
	static void requestAbs(HttpClient httpClient, String url, MultiMap headers, WebsocketVersion version, List<String> subProtocols, Handler<WebSocket> succeededHandler,
			Handler<Throwable> exceptionHandler) {
		requestAbs(httpClient, url, headers, version, subProtocols, null, succeededHandler, exceptionHandler);
	}

	/**
	 * WebSocket请求
	 * 
	 * @param httpClient
	 *          Http客户端
	 * @param url
	 *          请求的url
	 * @param headers
	 *          请求的Headers
	 * @param version
	 *          客户端的版本号
	 * @param subProtocols
	 *          子协议
	 * @param connectionHandler
	 *          连接建立处理器
	 * @param succeededHandler
	 *          请求成功
	 * @param exceptionHandler
	 *          请求失败
	 */
	static void requestAbs(HttpClient httpClient, String url, MultiMap headers, WebsocketVersion version, List<String> subProtocols, Handler<HttpConnection> connectionHandler,
			Handler<WebSocket> succeededHandler, Handler<Throwable> exceptionHandler) {
		try {
			if (connectionHandler != null) {
				httpClient.connectionHandler(connectionHandler);
			}
			httpClient.webSocketAbs(url, headers, version, subProtocols, res -> {
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
