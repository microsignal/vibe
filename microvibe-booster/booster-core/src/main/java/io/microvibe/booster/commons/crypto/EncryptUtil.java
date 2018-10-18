package io.microvibe.booster.commons.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

public class EncryptUtil {
	private static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
	public static final String DSA = "DSA";
	public static final String SIGN_SHA1withDSA = "SHA1withDSA";
	public static final String SIGN_SHA224withDSA = "SHA224withDSA";
	public static final String SIGN_SHA256withDSA = "SHA256withDSA";
	public static final String RSA = "RSA";
	public static final String SIGN_NONEwithRSA = "NONEwithRSA";
	public static final String SIGN_SHA1withRSA = "SHA1withRSA";
	public static final String SIGN_SHA224withRSA = "SHA224withRSA";
	public static final String SIGN_SHA256withRSA = "SHA256withRSA";
	public static final String SIGN_SHA384withRSA = "SHA384withRSA";
	public static final String SIGN_SHA512withRSA = "SHA512withRSA";
	public static final String CIPHER_RSA = "RSA/ECB/PKCS1Padding";
	public static final String CIPHER_3DES = "DESede";
	public static final String CIPHER_DES = "DES";
	public static final String CIPHER_AES = "AES";
	public static final String PRNG_SHA1PRNG = "SHA1PRNG";

	public static byte[] encryptAES(byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		return encrypt(CIPHER_AES, key, data);
	}

	public static byte[] decryptAES(byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		return decrypt(CIPHER_AES, key, data);
	}

	public static byte[] encrypt3DES(byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		return encrypt(CIPHER_3DES, key, data);
	}

	public static byte[] decrypt3DES(byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		return decrypt(CIPHER_3DES, key, data);
	}

	public static byte[] encryptDES(byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		return encrypt(CIPHER_DES, key, data);
	}

	public static byte[] decryptDES(byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		return decrypt(CIPHER_DES, key, data);
	}

	public static byte[] encrypt(String algorithm, byte[] key, byte[] data)
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
		SecureRandom secureRandom = SecureRandom.getInstance(PRNG_SHA1PRNG);//new SecureRandom();
		secureRandom.setSeed(key);
		keyGen.init(secureRandom);
		SecretKey secretKey = keyGen.generateKey();
		return encrypt(algorithm, secretKey, data);
	}

	public static byte[] _encrypt(String algorithm, byte[] key, byte[] data)
		throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
		IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
		SecretKey secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
		return encrypt(algorithm, secretKey, data);
	}

	public static byte[] encrypt(String algorithm, Key key, byte[] data)
		throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
		IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static byte[] decrypt(String algorithm, byte[] key, byte[] data)
		throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException,
		BadPaddingException {
		KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
		SecureRandom secureRandom = SecureRandom.getInstance(PRNG_SHA1PRNG);//new SecureRandom();
		secureRandom.setSeed(key);
		keyGen.init(secureRandom);
		SecretKey secretKey = keyGen.generateKey();
		return decrypt(algorithm, secretKey, data);
	}

	public static byte[] _decrypt(String algorithm, byte[] key, byte[] data)
		throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
		IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
		SecretKey secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
		return decrypt(algorithm, secretKey, data);
	}

	public static byte[] decrypt(String algorithm, Key key, byte[] data)
		throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
		IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static KeyPair getKeyPair(String algorithm) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
		keyPairGen.initialize(1024);
		/*SecureRandom secureRandom = SecureRandom.getInstance(PRNG_SHA1PRNG);
		keyPairGen.initialize(1024, secureRandom);*/
		KeyPair keyPair = keyPairGen.generateKeyPair();
		return keyPair;
	}

	public static byte[] sign(String algorithm, PrivateKey key, byte[] data)
		throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(key);
		signature.update(data);
		return signature.sign();
	}

	public static boolean verify(String algorithm, PublicKey key, byte[] data, byte[] sign)
		throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(key);
		signature.update(data);
		try {
			return signature.verify(sign);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return false;
		}
	}

	public static PrivateKey getPrivateKey(String algorithm, byte[] key)
		throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		return privateKey;
	}

	public static PublicKey getPublicKey(String algorithm, byte[] key)
		throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
		return publicKey;
	}

	public static RSAPrivateKey getRSAPrivateKey(byte[] modulus, byte[] publicExponent)
		throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(
			publicExponent));
		return (RSAPrivateKey) keyFactory.generatePublic(keySpec);
	}

	public static RSAPublicKey getRSAPublicKey(byte[] modulus, byte[] publicExponent)
		throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(
			publicExponent));
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

}
