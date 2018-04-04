package com.xj.common.security;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xj.common.utils.StringUtils;

/**
 * RSA加密/解密及签名/验签
 * 
 * @author yangb
 * @version 20171212
 */
public class RsaSign {
	private static Logger logger = LoggerFactory.getLogger(RsaSign.class);

	// RSA 算法 Algorithm
	public static final String KEY_ALGORITHM = "RSA";

	// 待加密的字节数不能超过密钥的长度值除以 8 再减去 11，而加密后得到密文的字节数，正好是密钥的长度值除以 8
	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

	// 生成 KEY 用
	public static final String PUBLIC_KEY = "publicKey";
	public static final String PRIVATE_KEY = "privateKey";
	public static final int KEY_SIZE_1024 = 1024;
	public static final int KEY_SIZE_2048 = 2048;

	private static final String DEFAULT_ENCODING = "UTF-8";

	public static void main(String[] args) {
		// 待加密内容
		String plain = new StringBuffer().append("非对称密码最大的特征在于加密与解密秘钥不同。两个秘钥,公开的叫公钥,保密的叫私钥。")
				.append("用公钥加密，则必须用私钥解密；反之亦然。通常情况下公钥用于加密，私钥用于解密。").append("非对称密码体制实现了无秘钥传输的保密通信，这一点对称密码体制是不能及的。")
				.append("非对称密码体制适用于开放环境，安全性高，秘钥管理简单，可方便、安全地实现数字签名、验证、数字信封等技术。").toString();

		// 生成指定长度的key（公钥，私钥）
		Map<String, String> keyMap = generateKeyBytes(KEY_SIZE_2048);
		logger.info("PublicKey: {}", keyMap.get(PUBLIC_KEY));
		logger.info("PrivateKey: {}", keyMap.get(PRIVATE_KEY));

		// 公钥加密
		String charset = "UTF-8"; // 字符串编码： UTF-8, GBK, GB2312
		String enStr = rsaEncrypt(keyMap.get(PUBLIC_KEY), plain, charset);
		logger.info("公钥加密: {}", enStr);

		// 私钥解密
		String deStr = rsaDecrypt(keyMap.get(PRIVATE_KEY), enStr, charset);
		logger.info("私钥解密: {}", deStr);

		logger.info("解密校验结果: {}", plain.equals(deStr));

		// 签名算法
		String algorithm = "SHA256withRSA";
		// 私钥签名
		String signatureStr = rsaSignature(algorithm, keyMap.get(PRIVATE_KEY), enStr, charset);
		logger.info("私钥签名: {}", signatureStr);

		// 公钥验签
		boolean signatureBool = rsaCheckSignature(algorithm, keyMap.get(PUBLIC_KEY), enStr, signatureStr, charset);
		logger.info("公钥验签结果: {}", signatureBool);
	}

	/**
	 * 生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥
	 * 
	 * @param keySize
	 *            RSA密钥长度必须是64的倍数，在512~16384之间。常用值：KEY_SIZE_1024、KEY_SIZE_2048
	 * @return
	 */
	public static Map<String, String> generateKeyBytes(int keySize) {
		if (keySize < 512 || keySize > 16384 || keySize % 64 != 0) {
			logger.warn("generateKeyBytes, RSA密钥长度 {} 无效，使用 2048 长度代替！ ", keySize);
			keySize = KEY_SIZE_2048;
		}

		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGenerator.initialize(keySize);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

			Map<String, String> keyMap = new HashMap<String, String>();
			keyMap.put(PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()));
			keyMap.put(PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded()));
			return keyMap;
		} catch (NoSuchAlgorithmException e) {
			logger.error("generateKeyBytes, 无效的 Algorithm {}", KEY_ALGORITHM, e);

		} catch (Exception e) {
			logger.error("generateKeyBytes, 生成RSA密钥对失败！ ", e);
		}
		return null;
	}

	/**
	 * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
	 * 
	 * @param publicKeyStr
	 *            Base64编码的公钥字符串
	 * @return
	 */
	public static PublicKey restorePublicKey(String publicKeyStr) {
		try {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
			KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
			PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			logger.error("restorePublicKey, 无效的 Algorithm {}", KEY_ALGORITHM, e);

		} catch (InvalidKeySpecException e) {
			logger.error("restorePublicKey, 无效的公钥字符串 ", e);

		} catch (Exception e) {
			logger.error("restorePublicKey, 还原公钥失败！ ", e);
		}
		return null;
	}

	/**
	 * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
	 * 
	 * @param privateKeyStr
	 *            Base64编码的私钥字符串
	 * @return
	 */
	public static PrivateKey restorePrivateKey(String privateKeyStr) {
		try {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyStr));
			KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
			PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			logger.error("restorePrivateKey, 无效的 Algorithm {}", KEY_ALGORITHM, e);

		} catch (InvalidKeySpecException e) {
			logger.error("restorePrivateKey, 无效的私钥字符串 ", e);

		} catch (Exception e) {
			logger.error("restorePrivateKey, 还原私钥失败！ ", e);
		}
		return null;
	}

	/**
	 * 加密，三步走
	 * 
	 * @param publicKey
	 *            公钥
	 * @param plainText
	 *            不能超过（密钥长度/8-11）字节长度
	 * @return
	 */
	public static byte[] rsaEncode(PublicKey publicKey, byte[] plainText) {

		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(plainText);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			logger.error("RSAEncode, 加密失败！ ", e);
		}
		return null;

	}

	/**
	 * 解密，三步走。
	 * 
	 * @param privateKey
	 *            私钥
	 * @param encodedText
	 *            RSA加密密文字节码
	 * @return
	 */
	public static String rsaDecode(PrivateKey privateKey, byte[] encodedText) {

		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(encodedText));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			logger.error("RSADecode, 解密失败！ ", e);
		}
		return null;

	}

	// ************************
	/**
	 * 公钥加密（默认字符编码）
	 * 
	 * @param publicKeyStr
	 *            Base64编码的公钥字符串
	 * @param content
	 *            待加密内容
	 * @return 密文内容
	 */
	public static String rsaEncrypt(String publicKeyStr, String content) {
		// 默认字符编码
		return rsaEncrypt(publicKeyStr, content, DEFAULT_ENCODING);
	}

	/**
	 * 公钥加密
	 * 
	 * @param publicKeyStr
	 *            Base64编码的公钥字符串
	 * @param content
	 *            待加密内容
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 * @return 密文内容
	 */
	public static String rsaEncrypt(String publicKeyStr, String content, String charset) {
		try {
			// 字符编码
			if (StringUtils.isBlank(charset)) {
				charset = DEFAULT_ENCODING;
			}

			// 获取公钥及公钥长度
			PublicKey publicKey = restorePublicKey(publicKeyStr);
			KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
			RSAPublicKeySpec keySpec = (RSAPublicKeySpec) factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			int keySize = keySpec.getModulus().toString(2).length(); // 转换为二进制，获取公钥长度
			int MaxBlockSize = keySize / 8 - 11;

			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] data = content.getBytes(charset);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MaxBlockSize) {
					cache = cipher.doFinal(data, offSet, MaxBlockSize);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MaxBlockSize;
			}
			byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
			out.close();

			return new String(encryptedData, charset);
		} catch (Exception e) {
			logger.error("rsaEncrypt, 加密失败！ ", e);
		}
		return null;
	}

	/**
	 * 私钥解密（默认字符编码）
	 * 
	 * @param privateKeyStr
	 *            Base64编码的私钥字符串
	 * @param content
	 *            待解密内容
	 * @return 明文内容
	 */
	public static String rsaDecrypt(String privateKeyStr, String content) {
		// 默认字符编码
		return rsaDecrypt(privateKeyStr, content, DEFAULT_ENCODING);
	}

	/**
	 * 私钥解密
	 * 
	 * @param privateKeyStr
	 *            Base64编码的私钥字符串
	 * @param content
	 *            待解密内容
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 * @return 明文内容
	 */
	public static String rsaDecrypt(String privateKeyStr, String content, String charset) {
		try {
			// 字符编码
			if (StringUtils.isBlank(charset)) {
				charset = DEFAULT_ENCODING;
			}

			// 获取私钥及私钥长度
			PrivateKey privateKey = restorePrivateKey(privateKeyStr);
			KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
			RSAPrivateKeySpec keySpec = (RSAPrivateKeySpec) factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			int keySize = keySpec.getModulus().toString(2).length(); // 转换为二进制，获取私钥长度
			int MaxBlockSize = keySize / 8;

			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] encryptedData = Base64.decodeBase64(content.getBytes(charset));
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MaxBlockSize) {
					cache = cipher.doFinal(encryptedData, offSet, MaxBlockSize);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MaxBlockSize;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return new String(decryptedData, charset);
		} catch (Exception e) {
			logger.error("rsaDecrypt, 解密失败！ ", e);
		}
		return null;
	}

	/**
	 * rsa签名（默认字符集）
	 * 
	 * @param algorithm
	 *            签名算法 SHA1withRSA SHA224withRSA SHA256withRSA SHA384withRSA
	 *            SHA512withRSA
	 * @param privateKeyStr
	 *            Base64编码的私钥字符串
	 * @param content
	 *            待签名内容
	 */
	public static String rsaSignature(String algorithm, String privateKeyStr, String content) {
		return rsaSignature(algorithm, privateKeyStr, content, DEFAULT_ENCODING);
	}

	/**
	 * rsa签名
	 * 
	 * @param algorithm
	 *            签名算法 SHA1withRSA SHA224withRSA SHA256withRSA SHA384withRSA
	 *            SHA512withRSA
	 * @param privateKeyStr
	 *            Base64编码的私钥字符串
	 * @param content
	 *            待签名内容
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 */
	public static String rsaSignature(String algorithm, String privateKeyStr, String content, String charset) {
		try {
			// 字符编码
			if (StringUtils.isBlank(charset)) {
				charset = DEFAULT_ENCODING;
			}
			// 获取私钥及私钥长度
			PrivateKey privateKey = restorePrivateKey(privateKeyStr);

			java.security.Signature signature = java.security.Signature.getInstance(algorithm);

			signature.initSign(privateKey);

			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();

			return new String(Base64.encodeBase64(signed), charset);
		} catch (Exception e) {
			logger.error("rsaSignature, 签名失败! content:{}, charset:{}, algorithm:{},", content, charset, algorithm, e);
		}
		return null;
	}

	/**
	 * rsa验签（默认字符集）
	 * 
	 * @param algorithm
	 *            签名算法 SHA1withRSA SHA224withRSA SHA256withRSA SHA384withRSA
	 *            SHA512withRSA
	 * @param publicKeyStr
	 *            Base64编码的公钥字符串
	 * @param content
	 *            待签名内容
	 * @param sign
	 *            签名
	 */
	public static boolean rsaCheckSignature(String algorithm, String publicKeyStr, String content, String sign) {
		return rsaCheckSignature(algorithm, publicKeyStr, content, sign, DEFAULT_ENCODING);
	}

	/**
	 * rsa验签
	 * 
	 * @param algorithm
	 *            签名算法 SHA1withRSA SHA224withRSA SHA256withRSA SHA384withRSA
	 *            SHA512withRSA
	 * @param publicKeyStr
	 *            Base64编码的公钥字符串
	 * @param content
	 *            待签名内容
	 * @param sign
	 *            签名
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 */
	public static boolean rsaCheckSignature(String algorithm, String publicKeyStr, String content, String sign,
			String charset) {
		try {
			// 字符编码
			if (StringUtils.isBlank(charset)) {
				charset = DEFAULT_ENCODING;
			}

			// 获取公钥及公钥长度
			PublicKey publicKey = restorePublicKey(publicKeyStr);

			java.security.Signature signature = java.security.Signature.getInstance(algorithm);

			signature.initVerify(publicKey);

			signature.update(content.getBytes(charset));
			return signature.verify(Base64.decodeBase64(sign.getBytes(charset)));
		} catch (Exception e) {
			logger.error("rsaCheckSignature, 验签失败! content:{}, sign:{}, charset:{}, algorithm:{},", content, sign,
					charset, algorithm, e);
		}
		return false;
	}

}
