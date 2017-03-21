package com.shinemo.publish.utils;

import java.util.ArrayList;
import java.util.List;

public class CMDUtils {

	private static List<String> CMD = new ArrayList<String>();

	static {
		CMD.add("cd");
		CMD.add("ls");
		CMD.add("pwd");
		CMD.add("netstat");
		CMD.add("ps");
		CMD.add("jps");
		CMD.add("top");
		CMD.add("free");
		CMD.add("cat");
		CMD.add("more");
		CMD.add("tail");
		CMD.add("find");
		CMD.add("awk");
		CMD.add("grep");
		CMD.add("sort");
		CMD.add("sar");
		// CMD.add("head");
		CMD.add("jstat");
		CMD.add("vmstat");
		CMD.add("iostat");
		CMD.add("du");
		CMD.add("df");
		CMD.add("curl");
		CMD.add("ping");
		CMD.add("mysql");
	}

	public static boolean isAllow(String cmd) {
		if (cmd == null) {
			return false;
		}
		for (String command : CMD) {
			if (cmd.trim().startsWith(command)
					&& cmd.trim().indexOf("&") == -1
					&& cmd.trim().indexOf("||") == -1
					&& cmd.trim().indexOf(";") == -1
					&& cmd.trim().indexOf("$") == -1) {
				return true;
			}
		}
		return false; // test
	}

	public static String parseCmd(String cmd) {
		if (cmd.trim().startsWith("top")) {
			return  "/usr/bin/top -bcn 1";
		}
		if(cmd.trim().startsWith("tail")){
			cmd.replaceAll("-f", "");
			return cmd;
		}
		return cmd;
	}

	public static List<String> getCmds() {
		return CMD;
	}

}
