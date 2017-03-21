package com.shinemo.publish.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

public class DateUtils {

	private static final String FORMAT_DETAILED = "yyyy-MM-dd HH:mm:ss";
	
	private static final String FORMAT_YEAR = "yyyy-MM-dd";
	
	
	// 获得下周星期一的日期  
    public static Date getDayPlus(int count) {  
          
        Calendar strDate = Calendar.getInstance();         
        strDate.add(strDate.DATE,count);  
          
        //System.out.println(strDate.getTime());  
        GregorianCalendar currentDate = new GregorianCalendar();  
        currentDate.set(strDate.get(Calendar.YEAR), strDate.get(Calendar.MONTH),strDate.get(Calendar.DATE));  
        Date monday = currentDate.getTime();  
        monday.setHours(0);
        monday.setMinutes(0);
        return monday;  
    }  
    
    /**
     * 
     * @param date
     * @param minitus
     * @return
     */
    public static boolean isExpired(Date date,int minitus){
    	Calendar strDate = Calendar.getInstance();         
        strDate.add(strDate.MINUTE,-minitus); 
        Date d = strDate.getTime();
        return date.before(d);
    }
  
	
    public static String format2str(Date date){
    	SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DETAILED);
        return sdf.format(date);
    }

	public static Date format(String date){
		return format(date, FORMAT_DETAILED);
	}

	public static Date formatDate(String date){
		return format(date, FORMAT_YEAR);
	}
	
	public static Date format(String date, String dateFormat) {
		if (date == null || "".equals(date))
			return null;
		
		if(StringUtils.isBlank(date)){
			return null;
		}
		
		if(StringUtils.isBlank(dateFormat)){
			dateFormat = FORMAT_DETAILED;
		}
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		try {
			return format.parse(date);
		} catch (Exception ex) {}
		return null;
	}

    
	public static void main(String[] args) {
		Date date = new Date();
		date.setMinutes(1);;
		System.out.println(isExpired(date, 5));
		}
    
    

}
