package ca.uwaterloo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileConvert {

	// Global variables
	// static private int i = 0;
	private static String filesName = "testheader.arff";
	private static String path = "/Users/HanyeWei/Documents/workspace/Data/";

	private static int maxRecordLines = 150;

	 public static void main(String[] args) {
	  //	readFileByLines(path + "notes3.txt");
		 writeFileHeader(path + filesName, 150);
	
	 // int count = getAllJavaFilesLineCount(new File(path));
	 // System.out.println("总行数：" + count);
	
	 }
	 
	 public static void writeFileHeader(String fileName, int numAttr){
		 try {
				// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
				FileWriter writer = new FileWriter(fileName, true);
				String header = "@RELATION waterTrackingData\n\n";
				writer.write(header);
				
				for(int i = 0; i < numAttr; i++){
					String attr = "@ATTRIBUTE attr" + i + " NUMERIC\n";
					writer.write(attr);
				}
				String res = "@ATTRIBUTE result {1, 0}\n\n";
				writer.write(res);
				
				String dat = "@DATA\n";
				writer.write(dat);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }

//	public static void method2(String fileName, String content) {
//		try {
//			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//			FileWriter writer = new FileWriter(fileName, true);
//			writer.write(content);
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void readFileByLines(String fileName) {
//		File file = new File(fileName);
//		BufferedReader reader = null;
//		try {
//			System.out.println("以行为单位读取文件内容，一次读一整行：");
//			reader = new BufferedReader(new FileReader(file));
//			String tempString = null;
//			String all = "no";
//			int line = 1;
//			// 一次读入一行，直到读入null为文件结束
//			while ((tempString = reader.readLine()) != null) {
//				// 显示行号
//				// System.out.println("line " + line + ": " + tempString);
//				// if(line == 1) all = tempString;
//				// else
//				all += ", " + tempString;
//				line++;
//				if (line == 1501)
//					break;
//			}
//			if (line < 1501) {
//				for (int k = line; k < 1501; k++) {
//					all = all + ", 0";
//					System.out.println("line " + k + ": ");
//				}
//			}
//			all += "\n";
//			method2(path + filesName, all);
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (reader != null) {
//				try {
//					reader.close();
//				} catch (IOException e1) {
//				}
//			}
//		}
//	}
//
//	private static int getAllJavaFilesLineCount(File dir) {
//		int count = 0;
//		// String tem = "";
//		for (File file : dir.listFiles()) {
//			// 如果是.java文件，就统计行数
//			if (file.isFile() && file.getName().endsWith(".txt")) {
//				// System.out.println(file);
//				// tem += getLine(file);
//				count += getLineCount(file);
//			}
//			// 如果是文件夹，就递归调用
//			else if (file.isDirectory()) {
//				count += getAllJavaFilesLineCount(file);
//			}
//		}
//		return count;
//	}
//
//	public static int getLineCount(File file) {
//		if (!file.isFile()) {
//			throw new IllegalArgumentException("请指定一个有效的文件对象！");
//		}
//
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			int count = 0;
//			while (reader.readLine() != null) {
//				count++;
//			}
//			// i++;
//			// System.out.println(i + ":" + count);
//			reader.close();
//			return count;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}

}
