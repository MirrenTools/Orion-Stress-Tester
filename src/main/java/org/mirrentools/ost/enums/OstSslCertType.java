package org.mirrentools.ost.enums;

/**
 * SSL的证书类型
 * 
 * @author <a href="http://mirrentools.org">Mirren</a>
 *
 */
public enum OstSslCertType {
	/** 请求证书的类型,使用默认证书(服务器提供) */
	DEFAULT,
	/** PEM证书 */
	PEM,
	/** PFX证书 */
	PFX,
	/** JKS证书 */
	JKS,;
}
