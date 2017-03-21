package com.shinemo.publish.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(HttpUtils.class);


	public static String executeGet(String url) throws Exception {  
        BufferedReader in = null;  
  
        String content = null;  
        try {  
            HttpClient client = new DefaultHttpClient();  
            HttpGet request = new HttpGet();  
            request.setURI(new URI(url));  
            HttpResponse response = client.execute(request);  
            in = new BufferedReader(new InputStreamReader(response.getEntity()  
                    .getContent()));  
            StringBuffer sb = new StringBuffer("");  
            String line = "";  
            String NL = System.getProperty("line.separator");  
            while ((line = in.readLine()) != null) {  
                sb.append(line + NL);  
            }  
            in.close();  
            content = sb.toString();  
        } catch(Exception e){
        	 LOG.error(e.getMessage());
        } finally { 
            if (in != null) {  
                try {  
                    in.close();// 最后要关闭BufferedReader  
                } catch (final Exception e) {  
                    e.printStackTrace();  
                }  
            }  
            return content;  
        }  
    }

}
