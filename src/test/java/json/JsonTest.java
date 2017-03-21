package json;

import java.io.File;

import org.aspectj.util.FileUtil;


public class JsonTest {
	
	public static void main(String[] args) throws Exception {
		
		File tmpf = new File("/tmp/1.log");
		if(tmpf.exists()){
			String buildLog = FileUtil.readAsString(tmpf);
			if(buildLog.indexOf("SUCCESS") !=-1 && buildLog.indexOf("FAILURE") ==-1){
				System.out.println("ok");
			}else{
				System.out.println("failed");
			}
		}
		
	}

}
