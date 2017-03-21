package com.shinemo.publish.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigestUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DigestUtils.class);
	private static Base64 coder = new Base64();

	/**
	 * Encode with SHA-1(密码加密)
	 * 
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		return encode(str.getBytes());
	}

	public static String encode(byte[] content) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");

			byte[] result = md.digest(content);
			return getHexByBytes(result);
		} catch (NoSuchAlgorithmException e) {
			LOG.warn("SHA1 not supported");
		}

		return "";
	}
	
	/**
	 * Encode with MD5(密码加密)
	 * 
	 * @param str
	 * @return
	 */
	public static String encodeMD5(String str) {
		return encodeMD5(str.getBytes());
	}
	
	private static String encodeMD5(byte[] content) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");

			byte[] result = md.digest(content);
			return getHexByBytes(result);
		} catch (NoSuchAlgorithmException e) {
			LOG.warn("SHA1 not supported");
		}

		return "";
	}
	
	private static String getHexByBytes(byte[] result) {
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			hexValue.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return hexValue.toString();
	}

	/**
	 * Encode with BASE64
	 * 
	 * @param str
	 * @return
	 */
	public static synchronized String encodeBase64(String str) {
		return new String(coder.encode(str.getBytes()));
	}

	/**
	 * Decode with BASE64
	 * 
	 * @param encoded
	 * @return
	 */
	public static synchronized String decodeBase64(String encoded) {
		try {
			byte[] b = coder.decode(encoded.getBytes());
			return new String(b);
		} catch (Exception e) {
			LOG.warn("Failed to decode BASE64 with str " + encoded , e);
			return "";
		}
	}
	
}
