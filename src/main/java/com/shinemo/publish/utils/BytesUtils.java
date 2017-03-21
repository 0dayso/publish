/**
 * 
 */
package com.shinemo.publish.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author david
 *
 */
public class BytesUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(BytesUtils.class);

	public static byte[] extractContent(InputStream input) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int len = 0;
			byte[] buf = new byte[8196];
			while ((len = input.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			
			output.close();
			
			return output.toByteArray();
		} catch (Exception e) {
			LOG.warn("Failed to read content from stream due to ", e);
			throw new IllegalArgumentException("input stream error." + e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					LOG.warn("Failed to close stream ", e);
				}
			}
		}
	}

}
