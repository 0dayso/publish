/**
 * 
 */
package com.shinemo.publish.api.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author david
 *
 */
public class DigestUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DigestUtils.class);
	private static Base64 coder = new Base64();
	
	public static synchronized String decodeBase64(String encoded) {
		try {
			byte[] b = coder.decode(encoded.getBytes());
			return new String(b);
		} catch (Exception e) {
			LOG.warn("Failed to decode BASE64 with str " + encoded + ", cause:" + e);
			return "";
		}
	}
}
