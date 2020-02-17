package org.mirrentools.ost.common;

/**
 * EventBus 通信地址
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public interface EventBusAddress {
	/**
	 * HTTP处理器的通讯地址,接收参数JsonObject{id(String):请求id,count(int):第几批请求,index(int):第几次请求,init(boolean):是否创建客户端}
	 */
	public final static String HTTP_TEST_HANDLER = "submit.test//HTTP";
	/**
	 * WebSocket处理器的通讯地址,接收参数JsonObject{id(String):请求id,count(int):第几批请求,index(int):第几次请求,init(boolean):是否创建客户端}
	 */
	public final static String WEB_SOCKET_TEST_HANDLER = "submit.test//WebSocket";
	/**
	 * TCP处理器的通讯地址,接收参数JsonObject{id(String):请求id,count(int):第几批请求,index(int):第几次请求,init(boolean):是否创建客户端}
	 */
	public final static String TCP_TEST_HANDLER = "submit.test//TCP";

}
