package com.shinemo.publish.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSearcher {
   
    public static File[] searchFile(String path, final String keyWord) {// 递归查找包含关键字的文件 
    	
    	File folder = new File(path); 
    	if (!folder.exists()) {// 如果文件夹不存在  
            System.out.println("目录不存在：" + folder.getAbsolutePath());  
            return null;  
        }  
   
        File[] subFolders = folder.listFiles(new FileFilter() {// 运用内部匿名类获得文件  
            @Override  
            public boolean accept(File pathname) {// 实现FileFilter类的accept方法  
                if (pathname.isDirectory()  
                        || (pathname.isFile() && pathname.getName().toLowerCase().contains(keyWord.toLowerCase())))// 目录或文件包含关键字  
                    return true;  
                return false;  
            }
        });  
   
        List<File> result = new ArrayList<File>();// 声明一个集合  
        for (int i = 0; i < subFolders.length; i++) {// 循环显示文件夹或文件  
        	File file = subFolders[i];
            if (file.isFile() ) {// 如果是文件则将文件添加到结果列表中 
            	if(!file.getName().endsWith(".java")){	//过来非java
            		continue;
            	}
                result.add(subFolders[i]);  
            } else {// 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中  
                File[] foldResult = searchFile(subFolders[i].getAbsolutePath(), keyWord);  
                for (int j = 0; j < foldResult.length; j++) {// 循环显示文件  
                    result.add(foldResult[j]);// 文件保存到集合中  
                }  
            }  
        }  
   
        File files[] = new File[result.size()];// 声明文件数组，长度为集合的长度  
        result.toArray(files);// 集合数组化  
        return files;  
    }  
    
    
    /**
     * 获取class package
     * @param filename
     * @return
     */
    public static String getPackageName(String filename){
    	File file = new File(filename);
    	if(file != null && file.isFile()){
    		BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    if(tempString.trim().startsWith("package")){
                    	return tempString.trim().substring(8);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            } 
    	}
    	return "";
    }
    
    
    public static String getClazzName(String filename){
    	String pkg = getPackageName(filename);
    	int start = filename.lastIndexOf("/")+1;
    	int end = filename.lastIndexOf(".java");
    	if(pkg!=null){
    		pkg = pkg.replaceAll(" ", "").replaceAll(";", "").trim();
    	}
    	return pkg+"."+filename.substring(start, end);
    }
    
    
    public static String getFile(String filename){
    	
    	StringBuffer sb = new StringBuffer();
    	File file = new File(filename);
    	if(file != null && file.isFile()){
    		BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    sb.append(tempString).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            } 
    	}
    	return sb.toString();
    }
    
    
   
    public static void main(String[] args) {// java程序的主入口处  
        String keyword = "Project";  
        String path = "/Users/luohuajun/work/workspace/git/publish/publish";
        File[] result = searchFile(path, keyword);// 调用方法获得文件数组  
        System.out.println("在 " + path + " 以及所有子文件时查找对象" + keyword);  
        for (int i = 0; i < result.length; i++) {// 循环显示文件  
            File file = result[i];  
            System.out.println(file.getAbsolutePath() + " ");// 显示文件绝对路径  
        }
        
        System.out.println(getClazzName("/Users/luohuajun/work/workspace/git/publish/publish/src/main/java/com/shinemo/publish/client/Project.java"));
    }  
} 
