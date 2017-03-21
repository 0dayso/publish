package com.shinemo.publish.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verifier {

	private static final Logger LOG = LoggerFactory.getLogger(Verifier.class);

	static Pattern phoneP = Pattern
			.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
	static Pattern mailP = Pattern
			.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$");
	
	static Pattern pattern = Pattern.compile("[0-9]*");

	/**
	 * Effective string. <br>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEffectiveStr(String str) {
		return (str != null) && (!str.trim().isEmpty());
	}
	
	public static boolean isEffectiveInt(int num){
		return num>0?true:false;
	}
	
	/**
	 * Effective list collection. <br>
	 * 
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> boolean isEffectiveList(List<T> list) {
		return list != null && list.size() > 0;
	}
	
	public static <T> boolean isEffectiveSet (Set<T> set){
		return set != null && set.size() >0;
	}

	/**
	 * Effective account, not allowed to contain special characters. <br>
	 * 
	 * @param account
	 * @return
	 */
	public static boolean isEffectiveAccount(String account) {
		if (!isEffectiveStr(account)) {
			return false;
		}

		for (int i = 0; i < account.length(); i++) {
			char ch = account.charAt(i);
			if (!isEffectiveChar(ch)) {
				return false;
			}
		}

		return true;
	}

	private static boolean isEffectiveChar(char ch) {
		if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')
				|| (ch >= 'a' && ch <= 'z'))
			return true;
		if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
			return true;

		return false;
	}

	/**
	 * Effective phone number in China. <br>
	 * 
	 * @param phoneNum
	 * @return
	 */
	public static boolean isEffectivePhoneNum(String phoneNum) {
		if (!isEffectiveStr(phoneNum)) {
			return false;
		}

		Matcher m = phoneP.matcher(phoneNum);
		return m.find();
	}
	
	public static boolean isEffectivePhoneNumNotNull(String phoneNum) {
		Matcher m = phoneP.matcher(phoneNum);
		return m.find();
	}
	
	/**
	 * verfy mobile phone Or telPhone
	 * when phoneNum is right, return false
	 * then phoneNum is wrong, return true.
	 * 
	 */
	public static boolean isEffectivePhoneNumOrTelPhone(String phoneNum) {
		
		if(!isEffectiveStr(phoneNum)) {
			return true;
		}
		
		if (phoneNum.startsWith("0")) {
			if (phoneNum.length() != 11 && phoneNum.length() != 12) {
				return true;
			}
		} else if(Verifier.isNumeric(phoneNum) && phoneNum.length() != 8 && phoneNum.length() != 11) {
			return true;
		} else if (!Verifier.isNumeric(phoneNum)) { 
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(isEffectivePhoneNumOrTelPhone(null));
	}

	/**
	 * Effective mail address with wildcard
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean isEffectiveMail(String mail) {
		if (!isEffectiveStr(mail)) {
			return false;
		}

		Matcher m = mailP.matcher(mail);
		return m.find();
	}

	/**
	 * Effective idCardNum, both 15 and 18 bit check
	 * 
	 * @param IDStr
	 * @return
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	public static boolean isEffectiveIDCard(String IDStr)
			throws NumberFormatException, ParseException {
		// String errorInfo = "";
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };
		String Ai = "";

		if (IDStr.length() != 15 && IDStr.length() != 18) {
			// errorInfo = "ID number length should be 15 or 18";
			return false;
		}
		// =======================(end)========================

		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			// errorInfo =
			// "ID number should be 15 to digital; 18 numbers except the last one, but should be a number";
			return false;
		}
		// =======================(end)========================

		String strYear = Ai.substring(6, 10);
		String strMonth = Ai.substring(10, 12);
		String strDay = Ai.substring(12, 14);
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			// errorInfo = "Birthday card invalid";
			return false;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
				|| (gc.getTime().getTime() - s.parse(
						strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
			// errorInfo = "Birthday card is not valid range";
			return false;
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			// errorInfo = "Invalid ID month";
			return false;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			// errorInfo = "ID Invalid date";
			return false;
		}
		// =====================(end)=====================

		Hashtable<String, String> h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			// errorInfo = "ID region coding errors";
			return false;
		}
		// ==============================================

		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.equalsIgnoreCase(IDStr) == false) {
				// errorInfo =
				// "ID is invalid, not a legal identity card number";
				return false;
			}
		} else {
			return true;
		}
		// =====================(end)=====================
		return true;
	}

	private static Hashtable<String, String> GetAreaCode() {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	public static boolean isNumeric(String str) {

		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
			
		} else {
			return false;
		}
	}

	private static boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))"
						+ "[\\-\\/\\s]?((((0?[13578])|(1[02]))"
						+ "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))"
						+ "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?"
						+ "[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))"
						+ "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|("
						+ "[1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|("
						+ "[1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?"
						+ "[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	private static char getVerifyCode(String idCardNumber) throws Exception {
		if (idCardNumber == null || idCardNumber.length() < 17) {
			throw new Exception("Illegal ID number");
		}
		char[] Ai = idCardNumber.toCharArray();
		int[] Wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] verifyCode = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3',
				'2' };
		int S = 0;
		int Y;
		for (int i = 0; i < Wi.length; i++) {
			S += (Ai[i] - '0') * Wi[i];
		}
		Y = S % 11;
		return verifyCode[Y];
	}

	/**
	 * convert from FifteenIDcard into EighteenIDCard. <br>
	 * 
	 * @param fifteenIDCard
	 * @return
	 * @throws Exception
	 */
	public static String convertFromFifteenIDcardIntoEighteenIDCard(
			String fifteenIDCard) throws Exception {
		if ("Correct".equals(isEffectiveIDCard(fifteenIDCard))) {
			StringBuilder sb = new StringBuilder();
			sb.append(fifteenIDCard.substring(0, 6)).append("19")
					.append(fifteenIDCard.substring(6));
			sb.append(getVerifyCode(sb.toString()));
			return sb.toString();
		} else {
			throw new Exception("ID is invalid");
		}
	}

	public static int getAgeFromIdCardNum(String idCardNum) {
		if (!isEffectiveStr(idCardNum)) {
			return 0;
		}
		
		int bornYear = 0;
		if (idCardNum.length() == 18) {
			bornYear = Integer.parseInt(idCardNum.substring(6, 10));
		} else if (idCardNum.length() == 15) {
			StringBuffer buffer = new StringBuffer();
			bornYear = Integer.parseInt(buffer.append(19)
					.append(idCardNum.substring(6, 8)).toString());
		}

		return Calendar.getInstance().get(Calendar.YEAR) - bornYear;
	}

	public static String getSexFromIdCardNum(String idCardNum) {
		if (!isEffectiveStr(idCardNum)) {
			return "";
		}
	
		String lastValue = "";
		if (idCardNum.length() == 15) {
			lastValue = idCardNum.substring(idCardNum.length() - 1, idCardNum.length());
		} else if (idCardNum.length() == 18) {
			lastValue = idCardNum.substring(idCardNum.length() - 2, idCardNum.length() - 1);
		} else {
			return "";
		}
		
		return (lastValue.trim().toLowerCase().equals("x") || lastValue.trim().toLowerCase().equals("e")) ? "男" : 
			Integer.parseInt(lastValue) % 2 != 0 ? "男" : "女";
	}

	public static boolean verifyCode(String expect, String current) {
		if (!isEffectiveCode(expect)) {
			LOG.warn("Original code doesnot fit for code rule : " + expect);
			return false;
		}

		return (expect != null) && expect.equals(current);
	}

	public static boolean isEffectiveAttribute(String content) {
		net.sf.json.JSONObject object = net.sf.json.JSONObject.fromObject(content);
		Iterator it = object.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			String value = object.get(key).toString();
			if (!Verifier.isEffectiveStr(value) || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("0")) {
				return false;
			}
		}

		return true;
	}

	private static boolean isEffectiveCode(String code) {
		if (code == null || code.length() != 6) {
			return false;
		}

		try {
			Integer.parseInt(code);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
