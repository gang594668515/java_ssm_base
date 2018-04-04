package com.xj.common.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.lang3.Validate;

import com.xj.common.utils.Encodes;
import com.xj.common.utils.Exceptions;

/**
 * 支持SHA-x/MD5消息摘要的工具类.
 * 
 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
 * 
 * @author calvin
 */
public class Digests {

	public static final String SHA1 = "SHA-1";
	public static final String SHA224 = "SHA-224";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";
	public static final String MD5 = "MD5";
	private static final String DEFAULT_URL_ENCODING = "UTF-8";

	private static SecureRandom random = new SecureRandom();

	/**
	 * 对输入字符串进行md5散列.
	 */
	public static byte[] md5(byte[] input) {
		return digest(input, MD5, null, 1);
	}

	public static byte[] md5(byte[] input, int iterations) {
		return digest(input, MD5, null, iterations);
	}

	public static String md5(String input) {
		try {
			return Encodes.encodeHex(digest(input.getBytes(DEFAULT_URL_ENCODING), MD5, null, 1));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String md5(String input, int iterations) {
		try {
			return Encodes.encodeHex(digest(input.getBytes(DEFAULT_URL_ENCODING), MD5, null, iterations));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 对输入字符串进行sha1散列.
	 */
	public static byte[] sha1(byte[] input) {
		return digest(input, SHA1, null, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt) {
		return digest(input, SHA1, salt, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
		return digest(input, SHA1, salt, iterations);
	}

	public static String sha1(String input) {
		try {
			return Encodes.encodeHex(digest(input.getBytes(DEFAULT_URL_ENCODING), SHA1, null, 1));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String sha1(String input, String salt) {
		try {
			return Encodes.encodeHex(
					digest(input.getBytes(DEFAULT_URL_ENCODING), SHA1, salt.getBytes(DEFAULT_URL_ENCODING), 1));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String sha1(String input, String salt, int iterations) {
		try {
			return Encodes.encodeHex(digest(input.getBytes(DEFAULT_URL_ENCODING), SHA1,
					salt.getBytes(DEFAULT_URL_ENCODING), iterations));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 对输入字符串进行sha散列.
	 */
	public static byte[] sha(byte[] input, String algorithm) {
		return digest(input, algorithm, null, 1);
	}

	public static byte[] sha(byte[] input, String algorithm, byte[] salt) {
		return digest(input, algorithm, salt, 1);
	}

	public static byte[] sha(byte[] input, String algorithm, byte[] salt, int iterations) {
		return digest(input, algorithm, salt, iterations);
	}

	public static String sha(String input, String algorithm) {
		try {
			return Encodes.encodeHex(digest(input.getBytes(DEFAULT_URL_ENCODING), algorithm, null, 1));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String sha(String input, String algorithm, String salt) {
		try {
			return Encodes.encodeHex(
					digest(input.getBytes(DEFAULT_URL_ENCODING), algorithm, salt.getBytes(DEFAULT_URL_ENCODING), 1));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String sha(String input, String algorithm, String salt, int iterations) {
		try {
			return Encodes.encodeHex(digest(input.getBytes(DEFAULT_URL_ENCODING), algorithm,
					salt.getBytes(DEFAULT_URL_ENCODING), iterations));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				digest.update(salt);
			}

			byte[] result = digest.digest(input);

			for (int i = 1; i < iterations; i++) {
				digest.reset();
				result = digest.digest(result);
			}
			return result;
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成随机的Byte[]作为salt.
	 * 
	 * @param numBytes
	 *            byte数组的大小
	 */
	public static byte[] generateSalt(int numBytes) {
		Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);

		byte[] bytes = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * 对文件进行md5散列.
	 */
	public static byte[] md5(InputStream input) throws IOException {
		return digest(input, MD5);
	}

	/**
	 * 对文件进行sha1散列.
	 */
	public static byte[] sha1(InputStream input) throws IOException {
		return digest(input, SHA1);
	}

	/**
	 * 对文件进行sha散列.
	 */
	public static byte[] sha(InputStream input, String algorithm) throws IOException {
		return digest(input, algorithm);
	}

	private static byte[] digest(InputStream input, String algorithm) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int bufferLength = 8 * 1024;
			byte[] buffer = new byte[bufferLength];
			int read = input.read(buffer, 0, bufferLength);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = input.read(buffer, 0, bufferLength);
			}

			return messageDigest.digest();
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

}
