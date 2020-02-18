package org.mirrentools.ost.verticle;

import org.mirrentools.ost.MainVerticle;
import org.mirrentools.ost.common.Constant;
import org.mirrentools.ost.common.EventBusAddress;
import org.mirrentools.ost.common.LocalDataBoolean;
import org.mirrentools.ost.common.LocalDataCounter;
import org.mirrentools.ost.common.LocalDataRequestOptions;
import org.mirrentools.ost.common.LocalDataServerWebSocket;
import org.mirrentools.ost.common.ResultFormat;
import org.mirrentools.ost.enums.OstCommand;
import org.mirrentools.ost.enums.OstSslCertType;
import org.mirrentools.ost.handler.OstTcpRequestHandler;
import org.mirrentools.ost.model.OstRequestOptions;
import org.mirrentools.ost.model.OstResponseInfo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;

/**
 * 处理TCP请求的Verticle,创建时需要传入请求的optionsId(String):请求的id
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public class OstTcpVerticle extends AbstractVerticle {
	/** 日志 */
	private final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		try {
			// 注册TCP测试处理器
			vertx.eventBus().consumer(EventBusAddress.TCP_TEST_HANDLER, this::testHandler);
			String optionsId = config().getString("optionsId");
			OstRequestOptions options = LocalDataRequestOptions.get(optionsId);
			ServerWebSocket socket = LocalDataServerWebSocket.get(optionsId);
			Boolean created = LocalDataBoolean.putIfAbsent(optionsId, true);
			if (created == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("执行测试任务提交->" + deploymentID() + "-->进行发布任务!");
				}
				vertx.executeBlocking(push -> {
					try { // 发布测试任务
						int count = options.getCount();
						for (int i = 1; i <= count; i++) {
							if (socket.isClosed()) {
								break;
							}
							JsonObject message = new JsonObject();
							message.put("id", optionsId);
							message.put("count", i);
							message.put("index", 1);
							message.put("init", !options.isKeepAlive());
							vertx.eventBus().send(EventBusAddress.TCP_TEST_HANDLER, message);
						}
						if (LOG.isDebugEnabled()) {
							LOG.debug("执行测试任务提交-->成功!");
						}
						push.complete();
					} catch (Exception e) {
						push.fail(e);
						LOG.error("执行测试任务提交-->失败:", e);
					}
				}, startPromise);
			} else {
				startPromise.complete();
			}
		} catch (Exception e) {
			LOG.error("执行初始化TCP测试Verticle-->失败:", e);
			startPromise.fail(e);
		}
	}

	/**
	 * TCP的测试处理器
	 * 
	 * @param msg
	 *          接收参数JsonObject{id(String):请求id,count(int):第几批请求,index(int):第几次请求,init(boolean):是否创建客户端}
	 */
	private void testHandler(Message<JsonObject> msg) {
		String id = msg.body().getString("id");
		int count = msg.body().getInteger("count");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Thread[" + Thread.currentThread().getId() + "] [" + count + "]处理器:" + deploymentID());
		}
		ServerWebSocket socket = LocalDataServerWebSocket.get(id);
		if (socket.isClosed()) {
			return;
		}
		OstRequestOptions options = LocalDataRequestOptions.get(id);
		if (socket.isClosed()) {
			return;
		}
		boolean init = !options.isKeepAlive();
		NetClientOptions cOptions = new NetClientOptions();
		if (options.getCert() != null) {
			cOptions.setSsl(true);
			if (options.getCert() != OstSslCertType.DEFAULT) {
				if (OstSslCertType.PFX == options.getCert()) {
					PfxOptions certOptions = new PfxOptions();
					certOptions.setPassword(options.getCertKey());
					certOptions.setValue(Buffer.buffer(options.getCertValue()));
					cOptions.setPfxKeyCertOptions(certOptions);
				} else if (OstSslCertType.JKS == options.getCert()) {
					JksOptions certOptions = new JksOptions();
					certOptions.setPassword(options.getCertKey());
					certOptions.setValue(Buffer.buffer(options.getCertValue()));
					cOptions.setKeyStoreOptions(certOptions);
				} else {
					PemKeyCertOptions certOptions = new PemKeyCertOptions();
					certOptions.setKeyValue(Buffer.buffer(options.getCertKey()));
					certOptions.setCertValue(Buffer.buffer(options.getCertValue()));
					cOptions.setPemKeyCertOptions(certOptions);
				}
			}
		}
		if (options.getTimeout() != null) {
			cOptions.setConnectTimeout(options.getTimeout());
		}
		NetClient netClient = vertx.createNetClient(cOptions);

		OstTcpRequestHandler.requestAbs(netClient, options, res -> {
			OstResponseInfo info = new OstResponseInfo();
			info.setCount(count);
			if (res.succeeded()) {
				NetSocket netSocket = res.result();
				Buffer buffer = Buffer.buffer();
				if (options.isPrintResInfo()) {
					netSocket.handler(buffer::appendBuffer);
				}

				vertx.executeBlocking(exec -> {
					try {
						long initTime = System.currentTimeMillis();
						for (int i = 0; i < options.getAverage(); i++) {
							int when = (i + 1);
							boolean ended = (when == options.getAverage());
							long startTime = System.currentTimeMillis() - initTime;
							long oriTime = options.getInterval() * i;
							long execTime = startTime > oriTime ? 1 : (oriTime - startTime);
							vertx.setTimer(execTime < 1 ? 1 : execTime, tid -> {
								if (LOG.isDebugEnabled()) {
									LOG.debug("TCP正在发送信息-->第[" + count + "-" + when + "]次!");
								}
								netSocket.endHandler(close -> {
									exec.tryComplete();
								});
								netSocket.write(options.getBody() == null ? "" : options.getBody().toString(), send -> {
									if (ended) {
										if (!options.isKeepAlive()) {
											netSocket.close();
										}
										exec.tryComplete();
									}
								});
							});
						}
					} catch (Exception e) {
						exec.fail(e);
					}
				}, end -> {
					if (socket.isClosed()) {
						return;
					}
					if (end.succeeded()) {
						LocalDataCounter.incrementAndGet(Constant.REQUEST_SUCCEEDED_PREFIX + id);
						info.setState(1);
						if (options.isPrintResInfo()) {
							info.setBody(buffer.toString());
							writeMsg(info, socket);
						}
						if (init) {
							netClient.close();
						}
					} else {
						if (socket.isClosed()) {
							return;
						}
						LocalDataCounter.incrementAndGet(Constant.REQUEST_FAILED_PREFIX + id);
						info.setBody(res.cause() == null ? "" : res.cause().getMessage());
						if (init) {
							netClient.close();
						}
						info.setState(0);
						writeMsg(info, socket);
					}
				});
			} else {
				if (socket.isClosed()) {
					return;
				}
				LocalDataCounter.incrementAndGet(Constant.REQUEST_FAILED_PREFIX + id);
				info.setBody(res.cause() == null ? "" : res.cause().getMessage());
				if (init) {
					netClient.close();
				}
				info.setState(0);
				writeMsg(info, socket);
			}
		});
	}

	/**
	 * 响应信息到前端
	 * 
	 * @param info
	 * @param socket
	 */
	private void writeMsg(OstResponseInfo info, ServerWebSocket socket) {
		if (socket.isClosed()) {
			return;
		}
		String result = ResultFormat.success(OstCommand.TEST_LOG_RESPONSE, info.toJson());
		socket.writeTextMessage(result);
	}

}
