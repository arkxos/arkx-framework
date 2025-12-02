package org.ark.framework.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

/**
 * @class org.ark.framework.security.CAUtil
 * @author Darkness
 * @date 2013-1-31 下午12:24:00
 * @version V1.0
 */
public class CAUtil {

	public static KeyPair generateRSAKeyPair() throws Exception {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "BC");
		gen.initialize(1024, new SecureRandom());
		return gen.generateKeyPair();
	}

	// public static X509Certificate createCA(PublicKey pubKey, PrivateKey privKey,
	// String issuerDN, int limit) throws Exception {
	// Date today = new Date(System.currentTimeMillis());
	// Date endDay = DateUtil.addMonth(today, limit);
	// SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new
	// DefaultSignatureAlgorithmIdentifierFinder().find(pubKey.getAlgorithm()),
	// pubKey.getEncoded());
	//
	// X509v3CertificateBuilder builder = new X509v3CertificateBuilder(new
	// X500Name(issuerDN), BigInteger.valueOf(System.currentTimeMillis()), today,
	// endDay, new X500Name(issuerDN), info);
	//
	// builder.addExtension(X509Extension.subjectKeyIdentifier, false, new
	// SubjectKeyIdentifier(info));
	// builder.addExtension(X509Extension.authorityKeyIdentifier, false, new
	// AuthorityKeyIdentifier(info));
	// builder.addExtension(X509Extension.basicConstraints, false, new
	// BasicConstraints(true));
	// builder.addExtension(X509Extension.keyUsage, false, new KeyUsage(6));
	//
	// ContentSigner signer = new
	// JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
	// X509CertificateHolder holder = builder.build(signer);
	//
	// holder.isValidOn(new Date());
	// ContentVerifierProvider contentVerifierProvider = new
	// JcaContentVerifierProviderBuilder().setProvider("BC").build(pubKey);
	// holder.isSignatureValid(contentVerifierProvider);
	//
	// return new JcaX509CertificateConverter().getCertificate(holder);
	// }

}
