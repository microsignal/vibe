package io.microvibe.booster.commons.crypto;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

/**
 * @author Qt
 * @since Jun 19, 2018
 */
public class RSA {

	public static final String DEFAULT_PUBLIC_KEY_STRING
		= "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJhUCNdvbcZJ3ofrmShZbimM6O9cbZEFG9Xvge7N0mE"
		+ "zYcmBdjbtBfkG+mN/E2PNRBhwruFX11SIVnNLEq2ZEyUCAwEAAQ==";
	public static final String UTF_8 = "UTF-8";
	private static final String DEFAULT_PRIVATE_KEY_STRING
		= "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmFQI129txkneh+uZKFluKYzo71x"
		+ "tkQUb1e+B7s3SYTNhyYF2Nu0F+Qb6Y38TY81EGHCu4VfXVIhWc0sSrZkTJQIDAQABAkBCODXnwa"
		+ "8NBkvQ9qCMI+R596HDfIC9DGV/aTEo6RzN1+XtR3t/rOqdqMVuF15xZxkW5335UNO13MeVYNdJG"
		+ "1YBAiEA2VZ09KiD+LApiZJFhtT6lyGxLqojgFZnwUzXXBpAmcECIQCzbQ1A/kcHnld0mfleeQcq"
		+ "RvLIZhWLv/8SPxYz9hLqZQIhAMjQE5yDn/zGeHq+hjNA0pGV9eqSFd9c9t0qLMrTDuGBAiBgqP0"
		+ "c6F+HoSvoKxMGbBkKE5KzlxtAYDyfrdDeSVDDaQIgd5SP6TzDIvOfXJnFdc3vCisVHhIokN87Ro"
		+ "5VwUR8ki8=";

	public static void main(String[] args) throws Exception {
		String password = args.length > 0 ? args[0] : RandomStringUtils.randomAlphanumeric(8);
		String[] arr = genKeyPair(512);
		System.out.println("privateKey:" + arr[0]);
		System.out.println("publicKey:" + arr[1]);
		String encrypt = encrypt(getPrivateKey(arr[0]), password);
		System.out.println("password:" + encrypt);
		System.out.println("plain:" + decrypt(getPublicKey(arr[1]), encrypt));
		/*
		encrypt = encrypt(getPublicKey(arr[1]), password);
		System.out.println("password:" + encrypt);
		System.out.println("plain:" + decrypt(getPrivateKey(arr[0]), encrypt));
		*/
	}

	public static String decrypt(PublicKey publicKey, String cipherText)
		throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		try {
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
		} catch (InvalidKeyException e) {
			// 因为 IBM JDK 不支持私钥加密, 公钥解密, 所以要反转公私钥
			// 也就是说对于解密, 可以通过公钥的参数伪造一个私钥对象欺骗 IBM JDK
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
			RSAPrivateKeySpec spec = new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
			Key fakePrivateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
			cipher = Cipher.getInstance("RSA"); //It is a stateful object. so we need to get new one.
			cipher.init(Cipher.DECRYPT_MODE, fakePrivateKey);
		}

		if (cipherText == null || cipherText.length() == 0) {
			return cipherText;
		}

		byte[] cipherBytes = Base64.decodeBase64(cipherText);
		byte[] plainBytes = cipher.doFinal(cipherBytes);

		return new String(plainBytes, UTF_8);
	}


	public static String decrypt(PrivateKey privateKey, String plainText)
		throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] cipherBytes = Base64.decodeBase64(plainText);
		byte[] plainBytes = cipher.doFinal(cipherBytes);
		return new String(plainBytes, UTF_8);
	}

	public static PublicKey getPublicKeyByX509(String x509File) {
		if (x509File == null || x509File.length() == 0) {
			return getPublicKey((String) null);
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(x509File);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			Certificate cer = factory.generateCertificate(in);
			return cer.getPublicKey();
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static PublicKey getPublicKeyByPublicKeyFile(String publicKeyFile) {
		if (publicKeyFile == null || publicKeyFile.length() == 0) {
			return getPublicKey((String) null);
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(publicKeyFile);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len = 0;
			byte[] b = new byte[512 / 8];
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			byte[] publicKeyBytes = out.toByteArray();
			return getPublicKey(publicKeyBytes);
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String encrypt(PrivateKey privateKey, String plainText)
		throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		try {
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		} catch (InvalidKeyException e) {
			//For IBM JDK, 原因请看解密方法中的说明
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
			Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
		}

		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(UTF_8));
		return Base64.encodeBase64String(encryptedBytes);
	}

	public static String encrypt(PublicKey publicKey, String plainText)
		throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(UTF_8));
		return Base64.encodeBase64String(encryptedBytes);
	}


	public static PublicKey getPublicKey(String publicKeyText) {
		byte[] publicKeyBytes = Base64.decodeBase64(
			(publicKeyText == null || (publicKeyText = publicKeyText.trim()).length() == 0)
				? DEFAULT_PUBLIC_KEY_STRING : publicKeyText);
		return getPublicKey(publicKeyBytes);
	}


	public static PublicKey getPublicKey(byte[] publicKeyBytes) {
		try {
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
				publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
			return keyFactory.generatePublic(x509KeySpec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		}
	}

	public static PrivateKey getPrivateKey(String privateKeyText) {
		byte[] publicKeyBytes = Base64.decodeBase64(
			(privateKeyText == null || (privateKeyText = privateKeyText.trim()).length() == 0)
				? DEFAULT_PRIVATE_KEY_STRING : privateKeyText);
		return getPrivateKey(publicKeyBytes);
	}


	public static PrivateKey getPrivateKey(byte[] privateKeyBytes) {
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory factory = KeyFactory.getInstance("RSA", "SunRsaSign");
			return factory.generatePrivate(spec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get private key", e);
		}
	}

	public static byte[][] genKeyPairBytes(int keySize)
		throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[][] keyPairBytes = new byte[2][];

		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
		gen.initialize(keySize, new SecureRandom());
		KeyPair pair = gen.generateKeyPair();

		keyPairBytes[0] = pair.getPrivate().getEncoded();
		keyPairBytes[1] = pair.getPublic().getEncoded();

		return keyPairBytes;
	}

	public static String[] genKeyPair(int keySize)
		throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[][] keyPairBytes = genKeyPairBytes(keySize);
		String[] keyPairs = new String[2];

		keyPairs[0] = Base64.encodeBase64String(keyPairBytes[0]);
		keyPairs[1] = Base64.encodeBase64String(keyPairBytes[1]);

		return keyPairs;
	}

}
