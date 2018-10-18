package io.microvibe.booster.core.base.web.security;

import io.microvibe.booster.commons.crypto.EncryptUtil;
import io.microvibe.booster.commons.utils.IOUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class JWTContext {
	public static enum AlgType {
		RSA(EncryptUtil.RSA, EncryptUtil.SIGN_SHA256withRSA),
		DSA(EncryptUtil.DSA, EncryptUtil.SIGN_SHA256withDSA),;

		private String algKey;
		private String algSign;

		private AlgType(String algKey, String algSign) {
			this.algKey = algKey;
			this.algSign = algSign;
		}

		public String getAlgKey() {
			return algKey;
		}

		public String getAlgSign() {
			return algSign;
		}
	}

	private String algKey = AlgType.DSA.getAlgKey();
	private String algSign = AlgType.DSA.getAlgSign();
	private PrivateKey privateKey;
	private PublicKey publicKey;

	private static boolean useStatic = true;
	private static JWTContext staticRsaJwtContext = new JWTContext(AlgType.RSA, "private.rsa", "public.rsa");
	private static JWTContext staticDsaJwtContext = new JWTContext(AlgType.DSA, "private.dsa", "public.dsa");
	private static JWTContext globalRsaJwtContext = new JWTContext(AlgType.RSA);
	private static JWTContext globalDsaJwtContext = new JWTContext(AlgType.DSA);

	static {
		useStatic(Boolean.parseBoolean(System.getProperty("jwtContext.useStatic", "true")));
	}

	private static void useStatic(boolean useStatic) {
		JWTContext.useStatic = useStatic;
	}

	public static boolean useStatic() {
		return JWTContext.useStatic;
	}

	public static JWTContext getGlobalDsaJwtContext() {
		if (useStatic()) return staticDsaJwtContext;
		else return globalDsaJwtContext;
	}

	public static JWTContext getGlobalRsaJwtContext() {
		if (useStatic()) return staticRsaJwtContext;
		else return globalRsaJwtContext;
	}

	public JWTContext() throws JWTException {
		this(AlgType.DSA);
	}

	public JWTContext(AlgType algType) throws JWTException {
		try {
			this.algKey = algType.getAlgKey();
			this.algSign = algType.getAlgSign();
			KeyPair keyPair = EncryptUtil.getKeyPair(this.algKey);
			this.privateKey = keyPair.getPrivate();
			this.publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e) {
			throw new JWTException(e, "ERR-01000");
		}
	}

	public JWTContext(String privateKeyPath, String publicKeyPath) throws JWTException {
		this(AlgType.DSA, privateKeyPath, publicKeyPath);
	}

	public JWTContext(AlgType algType, String privateKeyPath, String publicKeyPath) throws JWTException {
		try {
			this.algKey = algType.getAlgKey();
			this.algSign = algType.getAlgSign();
			InputStream privateKeyIn = IOUtils.getInputStream(privateKeyPath);
			InputStream publicKeyIn = IOUtils.getInputStream(publicKeyPath);
			byte[] privateKeyBytes = IOUtils.toByteArray(privateKeyIn);
			byte[] publicKeyBytes = IOUtils.toByteArray(publicKeyIn);
			init(privateKeyBytes, publicKeyBytes);
		} catch (IOException e) {
			throw new JWTException(e, "ERR-01002");
		}
	}

	public JWTContext(InputStream privateKeyIn, InputStream publicKeyIn) throws JWTException {
		this(AlgType.DSA, privateKeyIn, publicKeyIn);
	}

	public JWTContext(AlgType algType, InputStream privateKeyIn, InputStream publicKeyIn) throws JWTException {
		try {
			this.algKey = algType.getAlgKey();
			this.algSign = algType.getAlgSign();
			byte[] privateKeyBytes = IOUtils.toByteArray(privateKeyIn);
			byte[] publicKeyBytes = IOUtils.toByteArray(publicKeyIn);
			init(privateKeyBytes, publicKeyBytes);
		} catch (IOException e) {
			throw new JWTException(e, "ERR-01002");
		}
	}

	public JWTContext(byte[] privateKeyBytes, byte[] publicKeyBytes) throws JWTException {
		this(AlgType.DSA, privateKeyBytes, publicKeyBytes);
	}

	public JWTContext(AlgType algType, byte[] privateKeyBytes, byte[] publicKeyBytes) throws JWTException {
		this.algKey = algType.getAlgKey();
		this.algSign = algType.getAlgSign();
		init(privateKeyBytes, publicKeyBytes);
	}

	private void init(byte[] privateKeyBytes, byte[] publicKeyBytes) throws JWTException {
		try {
			this.privateKey = EncryptUtil.getPrivateKey(algKey, Base64.decodeBase64(privateKeyBytes));
			this.publicKey = EncryptUtil.getPublicKey(algKey, Base64.decodeBase64(publicKeyBytes));
		} catch (NoSuchAlgorithmException e) {
			throw new JWTException(e, "ERR-01000");
		} catch (InvalidKeySpecException e) {
			throw new JWTException(e, "ERR-01001");
		}
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public String sign(String origin) {
		try {
			byte[] data = origin.getBytes();
			byte[] enc = EncryptUtil.sign(algSign, privateKey, data);
			return Base64.encodeBase64String(enc);
		} catch (NoSuchAlgorithmException e) {
			throw new JWTException(e, "ERR-01000");
		} catch (InvalidKeyException e) {
			throw new JWTException(e, "ERR-01001");
		} catch (Exception e) {
			throw new JWTException(e, "ERR-01005");
		}
	}

	public boolean verify(String origin, String sign) {
		try {
			byte[] data = origin.getBytes();
			byte[] signBytes = Base64.decodeBase64(sign.getBytes());
			return EncryptUtil.verify(algSign, publicKey, data, signBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new JWTException(e, "ERR-01000");
		} catch (InvalidKeyException e) {
			throw new JWTException(e, "ERR-01001");
		} catch (Exception e) {
			throw new JWTException(e, "ERR-01006");
		}
	}

	public void genPrivateKey(String path) throws IOException {
		try (FileOutputStream fis = new FileOutputStream(path);) {
			genPrivateKey(fis);
		}
	}

	public void genPrivateKey(OutputStream out) throws IOException {
		byte[] bys = Base64.encodeBase64Chunked(privateKey.getEncoded());
		out.write(bys);
		out.flush();
	}

	public void genPublicKey(String path) throws IOException {
		try (FileOutputStream fis = new FileOutputStream(path);) {
			genPublicKey(fis);
		}
	}

	public void genPublicKey(OutputStream out) throws IOException {
		byte[] bys = Base64.encodeBase64Chunked(publicKey.getEncoded());
		out.write(bys);
		out.flush();
	}

	public void printPrivateKey(OutputStream out) throws IOException {
		out.write(Base64.encodeBase64Chunked(privateKey.getEncoded()));
	}

	public void printPublicKey(OutputStream out) throws IOException {
		out.write(Base64.encodeBase64Chunked(publicKey.getEncoded()));
	}

	public void printPrivateKey() throws IOException {
		printPrivateKey(System.out);
	}

	public void printPublicKey() throws IOException {
		printPublicKey(System.out);
	}

}
