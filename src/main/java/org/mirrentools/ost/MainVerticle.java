package org.mirrentools.ost;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mirrentools.ost.common.Constant;
import org.mirrentools.ost.common.JvmMetricsUtil;
import org.mirrentools.ost.common.LocalDataBoolean;
import org.mirrentools.ost.common.LocalDataCounter;
import org.mirrentools.ost.common.LocalDataRequestOptions;
import org.mirrentools.ost.common.LocalDataServerWebSocket;
import org.mirrentools.ost.common.LocalDataVertx;
import org.mirrentools.ost.common.ResultFormat;
import org.mirrentools.ost.enums.OstCommand;
import org.mirrentools.ost.enums.OstRequestType;
import org.mirrentools.ost.enums.OstSslCertType;
import org.mirrentools.ost.handler.OstHttpRequestHandler;
import org.mirrentools.ost.handler.OstTcpRequestHandler;
import org.mirrentools.ost.handler.OstWebSocketRequestHandler;
import org.mirrentools.ost.model.OstRequestOptions;
import org.mirrentools.ost.verticle.OstHttpVerticle;
import org.mirrentools.ost.verticle.OstTcpVerticle;
import org.mirrentools.ost.verticle.OstWebSocketVerticle;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebsocketVersion;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.MetricsService;
import io.vertx.micrometer.MicrometerMetricsOptions;

/**
 * <pre>
 * 嗨!我通过下面这4行代码,运行了很长很长的时间后,它打印的内容进化成现在的这个项目
 * Hi! I ran the following four lines of code for a long time, and the printed content evolved into the current project
 * while (true) {
 *   System.out.print(new Random().nextInt(2));
 *   Thread.sleep(1000);
 * }
 *   可能我上面的内容很疯狂,但是你可能听信过更疯狂的,比如:我们现在生活的世界是大爆炸而来的;
 * Maybe what I am talking above is crazy, but you may have heard much more crazy things, for example: The world we live in now is from the big bang;
 * 因为我们不关心或不容易证实,所以一些假说与谎言传多了就被当真了;
 * Because we don't care about it or it's not easy to prove it, some hypotheses and lies are taken seriously when they are spread too much;
 * 计算机需要被制造,程序也需要被编写或生成才有;程序能做什么,能获取到计算机的什么信息都已经在编写的时候设定好了;
 * A computer needs to be made, and a program needs to be written or generated. What a program can do and what information it can get has been set up at the time of writing;
 * 计算机的世界好比我们现在生活的世界,程序好比现在看到这段注释的你我;
 * The world of computer is like the world we live in now, and the program is like you and me who see this comment now;
 * 这个世界有一位造物主,虽然眼不能见但是籍着祂所造的一切,只要我们不压着我们的良心我们都能感受到;
 * There is a creator in this world, who can't see but can feel everything created by HIM as long as we don't press our conscience;
 *   我们编写程序需要有文档或注释帮助我们了解程序相关的,同样如果要了解这个世界的一切我们只能通过她的说明书,就是圣经
 * We need to have documents or notes to help us understand the program, and if we want to understand everything in the world, we can only through her instructions, that is, the BIBLE.
 * 程序入口
 * Main
 * </pre>
 * 
 * @author <a href="https://mirrentools.org/">Mirren</a>
 * @date 2019-02-14
 */
public class MainVerticle extends AbstractVerticle {
	/** 日志 */
	private final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
	/** 实例的数量 */
	private int instances;

	/**
	 * IDE中启动的Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainLauncher.start();
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		instances = config().getInteger("instances", JvmMetricsUtil.availableProcessors());
		Integer port = config().getInteger("httpPort", 7090);

		Router router = Router.router(vertx);
		router.route().handler(StaticHandler.create("webroot").setDefaultContentEncoding("UTF-8"));
		vertx.createHttpServer().requestHandler(router).webSocketHandler(socket -> {
			SocketAddress address = socket.remoteAddress();
			if (LOG.isDebugEnabled()) {
				LOG.debug(address.host() + ":" + address.port() + socket.path() + " -->连接控制台成功!");
			}
			if (socket.query() != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("\"query: \" + socket.query()");
				}
			}
			// 处理用户的信息
			socket.handler(buf -> {
				try {
					JsonObject body = new JsonObject(buf);
					if (LOG.isDebugEnabled()) {
						LOG.debug("收到用户请求:" + body);
					}
					Integer code = body.getInteger(Constant.CODE);
					if (code.equals(OstCommand.CANCEL.value())) {
						socket.end();
					} else if (code.equals(OstCommand.SUBMIT_TEST.value())) {
						JsonObject data = body.getJsonObject(Constant.DATA);
						checkAndLoadRequestOptions(data, res -> {
							if (res.succeeded()) {
								// 检查与装载数据成功,提交测试任务
								OstRequestOptions options = res.result();
								String id = socket.textHandlerID();
								options.setId(id);
								if (LOG.isDebugEnabled()) {
									LOG.debug("加载并检查请求参数-->成功:" + options);
								}
								vertx.setTimer(1000, tid -> {
									if (socket.isClosed()) {
										vertx.cancelTimer(tid);
										return;
									}
									JsonObject result = new JsonObject();
									result.put("processors", JvmMetricsUtil.availableProcessors());
									result.put("totalMemory", JvmMetricsUtil.totalMemory());
									result.put("maxMemory", JvmMetricsUtil.maxMemory());
									result.put("freeMemory", JvmMetricsUtil.freeMemory());
									String metrics = ResultFormat.success(OstCommand.JVM_METRIC, result);
									socket.writeTextMessage(metrics);
								});
								submitTest(options, socket);
								// 设置Socket关闭事件
								socket.endHandler(end -> {
									LocalDataServerWebSocket.remove(id);
									LocalDataRequestOptions.remove(id);
									LocalDataCounter.remove(id);
									LocalDataBoolean.remove(id);
									Vertx remove = LocalDataVertx.remove(id);
									if (remove != null) {
										remove.close(close -> {
											if (close.failed()) {
												LOG.error("关闭WebSocket->关闭测试服务->" + id + "-->异常", close.cause());
											} else {
												if (LOG.isDebugEnabled()) {
													LOG.debug("关闭WebSocket->关闭测试服务->" + id + "-->成功");
												}
											}
										});
									}
								});
							} else {
								if (LOG.isDebugEnabled()) {
									LOG.debug("加载并检查请求参数-->失败:", res.cause());
								}
								socket.writeTextMessage(ResultFormat.failed(OstCommand.MISSING_PARAMETER, res.cause().getMessage(), buf.toString()));
							}
						});
					} else {
						socket.writeTextMessage(ResultFormat.failed(OstCommand.MISSING_PARAMETER, "请求失败,无效的操作指令!", buf.toString()));
					}
				} catch (Exception e) {
					LOG.error("解析用户请求-->失败:" + buf);
					socket.writeTextMessage(ResultFormat.failed(OstCommand.MISSING_PARAMETER, "请求失败,存在无效的数据!", buf.toString()));
				}
			});
		}).listen(port, res -> {
			if (res.succeeded()) {
				System.out.println("Orion-Stress-Tester running http://127.0.0.1:" + port);
				startPromise.complete();
			} else {
				LOG.error("Orion-Stress-Tester start failed. If the port is occupied, you can modify the httpport of data/config.json");
				startPromise.fail(res.cause());
			}
		});

	}

	/**
	 * 提交测试请求
	 * 
	 * @param options
	 */
	private void submitTest(OstRequestOptions options, ServerWebSocket socket) {
		checkRequest(options, res -> {
			if (res.succeeded()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("检查请求是否可用-->结果:成功!");
				}
				String result = ResultFormat.success(OstCommand.BEFORE_REQUEST_TEST, 1);
				socket.writeTextMessage(result);
				// 请求的id
				String optionsId = socket.textHandlerID();
				// 存储需要请求的数量
				LocalDataCounter.newCounter(optionsId, (options.getAverage() * options.getCount()));
				// 共享WebSocket
				LocalDataServerWebSocket.put(optionsId, socket);
				// 共享请求配置
				LocalDataRequestOptions.put(optionsId, options);
				// 开启测试
				startTest(options, socket);
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("检查请求是否可用-->结果:失败:", res.cause());
				}
				if (socket.isClosed()) {
					return;
				}
				String result = ResultFormat.failed(OstCommand.BEFORE_REQUEST_TEST, res.cause().getMessage(), 0);
				socket.writeTextMessage(result);
				socket.end();
			}
		});
	}

	/**
	 * 启动测试服务
	 * 
	 * @param options
	 *          请求的配置
	 * @param socket
	 *          Socket
	 */
	private void startTest(OstRequestOptions options, ServerWebSocket socket) {

		// 测试服务的名称
		String testName;
		// 测试镜像的名称
		String snapshotName;
		// 要启动的服务名称
		String verticleName;
		// 请求的总数量
		long requestTotal;
		if (options.getType() == OstRequestType.TCP) {
			testName = "TCP";
			snapshotName = "vertx.net";
			verticleName = OstTcpVerticle.class.getName();
			requestTotal = options.getCount();
		} else if (options.getType() == OstRequestType.WebSocket) {
			testName = "WebSocket";
			snapshotName = "vertx.http";
			verticleName = OstWebSocketVerticle.class.getName();
			requestTotal = options.getCount();
		} else {
			testName = "HTTP";
			snapshotName = "vertx.http";
			verticleName = OstHttpVerticle.class.getName();
			requestTotal = (options.getCount() * options.getAverage());
		}

		MicrometerMetricsOptions metricsOptions = new MicrometerMetricsOptions().setEnabled(true);
		metricsOptions.setMicrometerRegistry(new SimpleMeterRegistry());
		VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(metricsOptions);
		if ((options.getCount() > 100 || options.getInterval() > 1000 * 150) && instances < 100) {
			vertxOptions.setWorkerPoolSize(100);
		}
		vertxOptions.setBlockedThreadCheckInterval(1000 * 60 * 60);
		Vertx newVertx = Vertx.vertx(vertxOptions);
		LocalDataVertx.put(options.getId(), newVertx);
		JsonObject config = new JsonObject().put("optionsId", options.getId());
		DeploymentOptions deployments = new DeploymentOptions();
		deployments.setInstances(instances);
		deployments.setConfig(config);
		if (LOG.isDebugEnabled()) {
			LOG.debug("正在启动" + testName + "测试服务:" + deployments.toJson());
		}
		Promise<String> promise = Promise.promise();
		promise.future().setHandler(h -> {
			MetricsService service = MetricsService.create(newVertx);
			vertx.setPeriodic(1000, tid -> {
				if (socket.isClosed()) {
					vertx.cancelTimer(tid);
					return;
				}
				JsonObject snapshot = service.getMetricsSnapshot(snapshotName);
				long succeeded = LocalDataCounter.getCount(Constant.REQUEST_SUCCEEDED_PREFIX + options.getId());
				long failed = LocalDataCounter.getCount(Constant.REQUEST_FAILED_PREFIX + options.getId());
				long endSum = (succeeded + failed);
				snapshot.put("succeeded", succeeded);
				snapshot.put("failed", failed);
				socket.writeTextMessage(ResultFormat.success(OstCommand.TEST_RESPONSE, snapshot), writed -> {
					if (endSum >= requestTotal) {
						JsonObject metrics = service.getMetricsSnapshot(snapshotName);
						metrics.put("succeeded", succeeded);
						metrics.put("failed", failed);
						String msg = ResultFormat.success(OstCommand.TEST_RESPONSE, metrics);
						if (LOG.isDebugEnabled()) {
							LOG.debug("执行" + testName + "测试->完成-->结果:" + metrics);
						}
						socket.writeTextMessage(msg, mwend -> {
							String end = ResultFormat.success(OstCommand.TEST_COMPLETE, 1);
							socket.writeTextMessage(end, ended -> {
								socket.end();
							});
						});
					} else {
						LOG.info("执行发送信息给客户端-->已请求数量: " + endSum + " / " + requestTotal);
					}
				});
			});
		});

		newVertx.deployVerticle(verticleName, deployments, res -> {
			if (res.succeeded()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("启动" + testName + "测试服务-->成功!");
				}
				promise.complete();
			} else {
				LOG.error("启动" + testName + "测试服务-->失败:", res.cause());
				String result = ResultFormat.failed(OstCommand.ERROR, res.cause().getMessage(), 0);
				socket.writeTextMessage(result);
				socket.end();
			}
		});
	}

	/**
	 * 检查请求是否有效
	 * 
	 * @param options
	 *          请求的配置
	 * @param handler
	 *          失败返回错误提示信息
	 */
	private void checkRequest(OstRequestOptions options, Handler<AsyncResult<Void>> handler) {
		try {
			OstRequestType type = options.getType();
			HttpClientOptions hOptions = new HttpClientOptions();
			if (options.getCert() != null && options.getCert() != OstSslCertType.DEFAULT) {
				if (OstSslCertType.PFX == options.getCert()) {
					PfxOptions certOptions = new PfxOptions();
					certOptions.setPassword(options.getCertKey());
					certOptions.setValue(Buffer.buffer(options.getCertValue()));
					hOptions.setPfxKeyCertOptions(certOptions);
				} else if (OstSslCertType.JKS == options.getCert()) {
					JksOptions certOptions = new JksOptions();
					certOptions.setPassword(options.getCertKey());
					certOptions.setValue(Buffer.buffer(options.getCertValue()));
					hOptions.setKeyStoreOptions(certOptions);
				} else {
					PemKeyCertOptions certOptions = new PemKeyCertOptions();
					certOptions.setKeyValue(Buffer.buffer(options.getCertKey()));
					certOptions.setCertValue(Buffer.buffer(options.getCertValue()));
					hOptions.setPemKeyCertOptions(certOptions);
				}
			}

			if (type == OstRequestType.HTTP) {
				HttpClient httpClient = vertx.createHttpClient(hOptions);
				if (LOG.isDebugEnabled()) {
					LOG.debug("进行测试前请求正在检查HTTP后端服务是否可用...");
				}
				OstHttpRequestHandler.requestAbs(httpClient, options, res -> {
					if (res.succeeded()) {
						HttpClientResponse result = res.result();
						int code = result.statusCode();
						if (LOG.isDebugEnabled()) {
							LOG.debug("进行测试前请求检查->HTTP-->结果:" + code);
						}
						if (code >= 200 && code < 300) {
							handler.handle(Future.succeededFuture());
						} else {
							handler.handle(Future.failedFuture("进行测试前请求检查失败:返回了无效的状态码: " + code));
						}
					} else {
						String msg = res.cause().getMessage();
						if (LOG.isDebugEnabled()) {
							LOG.debug("进行测试前请求检查->HTTP-->失败:", res);
						}
						handler.handle(Future.failedFuture("进行测试前请求检查失败:" + msg));
					}
				});

			} else if (type == OstRequestType.WebSocket) {
				HttpClient httpClient = vertx.createHttpClient(hOptions);
				OstWebSocketRequestHandler.requestAbs(httpClient, options, res -> {
					if (LOG.isDebugEnabled()) {
						LOG.debug("进行测试前请求检查->WebSocket-->成功!");
					}
					handler.handle(Future.succeededFuture());
				}, err -> {
					if (LOG.isDebugEnabled()) {
						LOG.debug("进行测试前请求检查->WebSocket-->失败:", err);
					}
					String msg = err.getMessage();
					handler.handle(Future.failedFuture("进行测试前请求检查失败:" + msg));
				});
			} else if (type == OstRequestType.TCP) {
				NetClientOptions cOption = new NetClientOptions();
				if (options.getCert() != null) {
					if (options.getCert() != OstSslCertType.DEFAULT) {
						if (OstSslCertType.PFX == options.getCert()) {
							PfxOptions certOptions = new PfxOptions();
							certOptions.setPassword(options.getCertKey());
							certOptions.setValue(Buffer.buffer(options.getCertValue()));
							cOption.setPfxKeyCertOptions(certOptions);
						} else if (OstSslCertType.JKS == options.getCert()) {
							JksOptions certOptions = new JksOptions();
							certOptions.setPassword(options.getCertKey());
							certOptions.setValue(Buffer.buffer(options.getCertValue()));
							cOption.setKeyStoreOptions(certOptions);
						} else {
							PemKeyCertOptions certOptions = new PemKeyCertOptions();
							certOptions.setKeyValue(Buffer.buffer(options.getCertKey()));
							certOptions.setCertValue(Buffer.buffer(options.getCertValue()));
							cOption.setPemKeyCertOptions(certOptions);
						}
					}
					cOption.setSsl(true);
				}
				NetClient netClient = vertx.createNetClient(cOption);
				OstTcpRequestHandler.request(netClient, options, res -> {
					if (LOG.isDebugEnabled()) {
						LOG.debug("进行测试前请求检查->TCP-->成功!");
					}
					handler.handle(Future.succeededFuture());
				}, err -> {
					if (LOG.isDebugEnabled()) {
						LOG.debug("进行测试前请求检查->TCP-->失败:", err);
					}
					String msg = err.getMessage();
					handler.handle(Future.failedFuture("进行测试前请求检查失败:" + msg));
				});
			} else {
				handler.handle(Future.failedFuture("无效的请求类型!"));
			}
		} catch (Exception e) {
			LOG.error("进行测试前请求检查-->失败:", e);
			handler.handle(Future.failedFuture(e.getMessage()));
		}

	}

	/**
	 * 参数检查并加载请求信息<br>
	 * type(String): 请求的类型:HTTP,WebSocket,TCP<br>
	 * url(String):请求的url<br>
	 * method(String): http请求的类型 {@link io.vertx.core.http.HttpMethod}<br>
	 * isSSL(boolean): 是否使用SSL<br>
	 * cert(String):证书的类型:DEFAULT,PEM,PFX,JKS <br>
	 * certKey(String):证书的key <br>
	 * certValue(String):证书的value <br>
	 * headers(JsonArray(JsonObject){key,value}) 请求的header数据<br>
	 * body(String):请求的body <br>
	 * count(Long):请求的总次数 <br>
	 * average(Long):每次请求多数次<br>
	 * interval(Long):请求的间隔 <br>
	 * keepAlive(boolean):是否保持连接 <br>
	 * virtualUsers(Long):请求客户端数量 <br>
	 * 
	 * @param body
	 * @return
	 */
	private void checkAndLoadRequestOptions(JsonObject body, Handler<AsyncResult<OstRequestOptions>> handler) {
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("执行参数检查并加载请求信息-->数据:" + body);
			}
			OstRequestOptions result = new OstRequestOptions();
			result.setType(OstRequestType.valueOf(body.getString("type")));
			String url = body.getString("url");
			if (result.getType() == OstRequestType.HTTP) {
				try {
					new URL(url);
				} catch (MalformedURLException e) {
					handler.handle(Future.failedFuture("无效的URL:" + url));
					return;
				}
				try {
					HttpMethod method = HttpMethod.valueOf(body.getString("method"));
					result.setMethod(method);
				} catch (Exception e) {
					handler.handle(Future.failedFuture("无效的method:" + body.getString("method")));
					return;
				}
			} else if (result.getType() == OstRequestType.WebSocket) {
				if (!url.startsWith("ws") && !url.startsWith("wss")) {
					handler.handle(Future.failedFuture("无效的URL:" + url));
					return;
				}
				result.setWebSocketVersion(WebsocketVersion.valueOf(body.getString("webSocketVersion")));
				JsonArray subs = body.getJsonArray("subProtocols");
				if (subs != null) {
					List<String> subProtocols = new ArrayList<>();
					for (int i = 0; i < subs.size(); i++) {
						subProtocols.add(subs.getString(i));
					}
					if (subProtocols.size() > 0) {
						result.setSubProtocols(subProtocols);
					}
				}
			} else if (result.getType() == OstRequestType.TCP) {
				result.setServerName(body.getString("serverName"));
				result.setHost(body.getString("host"));
				result.setPort(body.getInteger("port"));
			}
			result.setUrl(url);
			Boolean ssl = body.getBoolean("isSSL");
			if (ssl != null && ssl) {
				result.setSsl(ssl);
				OstSslCertType sslType = OstSslCertType.valueOf(body.getString("cert"));
				if (sslType == OstSslCertType.DEFAULT) {
					result.setCert(OstSslCertType.DEFAULT);
				} else {
					if (body.getString("certKey") == null || "".equals(body.getString("certKey").trim()) || body.getString("certValue") == null || "".equals(body.getString("certValue").trim())) {
						handler.handle(Future.failedFuture("如果不使用默认SSL证书,证书的key与value不能为空"));
						return;
					}
					result.setCertKey(body.getString("certKey"));
					result.setCertValue(body.getString("certValue"));
				}
			}
			if (body.getJsonArray("headers") != null) {
				MultiMap header = MultiMap.caseInsensitiveMultiMap();
				JsonArray th = body.getJsonArray("headers");
				for (int i = 0; i < th.size(); i++) {
					JsonObject h = th.getJsonObject(i);
					if (h.getString("key") != null && h.getString("value") != null) {
						header.add(h.getString("key"), h.getString("value"));
					}
				}
				if (header.size() > 0) {
					result.setHeaders(header);
				}
			}
			if (body.getString("body") != null) {
				result.setBody(Buffer.buffer(body.getString("body")));
			}
			int count = body.getInteger("count");
			if (count < 1) {
				handler.handle(Future.failedFuture("无效的请求的总次数"));
				return;
			}
			result.setCount(count);

			int average = body.getInteger("average");
			if (average < 1) {
				handler.handle(Future.failedFuture("无效的每次请求多数次"));
				return;
			}
			result.setAverage(average);

			Long interval = body.getLong("interval");
			if (interval < 1) {
				handler.handle(Future.failedFuture("无效的请求的间隔"));
				return;
			}
			result.setInterval(interval);
			result.setPrintResInfo(body.getBoolean("printResInfo"));
			result.setKeepAlive(body.getBoolean("keepAlive"));
			result.setTimeout(body.getInteger("timeout"));
			if (body.getBoolean("keepAlive")) {
				int au = body.getInteger("poolSize");
				result.setPoolSize(au);
			}
			handler.handle(Future.succeededFuture(result));
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("执行参数检查并加载请求信息-->失败:", e);
			}
			handler.handle(Future.failedFuture("缺少参数或存在无效的参数"));
		}
	}

}
