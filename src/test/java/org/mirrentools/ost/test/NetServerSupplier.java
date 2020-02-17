package org.mirrentools.ost.test;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class NetServerSupplier {
	public static void main(String[] args) {
		NetServer server = Vertx.vertx().createNetServer();
		server.connectHandler(socket -> {
			socket.handler(body -> {
				System.out.println(body);
			});
			socket.write("ok");
		});
		server.listen(8901);

	}
}
