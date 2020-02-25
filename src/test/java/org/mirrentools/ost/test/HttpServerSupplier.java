package org.mirrentools.ost.test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;

public class HttpServerSupplier {
	public static void main(String[] args) {
		AtomicInteger integer = new AtomicInteger();
		AtomicInteger get = new AtomicInteger();
		
		Vertx.vertx().createHttpServer().requestHandler(rct -> {
			if (rct.getHeader(HttpHeaders.CONTENT_LENGTH) != null) {
				System.out.println(rct.getHeader(HttpHeaders.CONTENT_LENGTH));
			}
			int random = new Random().nextInt(66666);
			String code = Integer.toString(random);
			if (code.contains("4")) {
				rct.response().setStatusCode(404);
				int addAndGet = integer.addAndGet(1);
				if (addAndGet>10) {
//					System.exit(0);
				}
			} else if (code.contains("5")) {
				rct.response().setStatusCode(500);
			}
			rct.response().end("ok");
			get.incrementAndGet();
		}).webSocketHandler(socket->{
			int get2 = get.incrementAndGet();
			if (get2>10) {
//				System.exit(0);
			}
			socket.handler(body->{
			});
			socket.writeTextMessage("ok");
		}).listen(7890);
	}
}
