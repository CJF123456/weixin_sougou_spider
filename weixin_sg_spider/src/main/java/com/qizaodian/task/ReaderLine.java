/**
 * 
 */
package com.qizaodian.task;

import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReaderLine
 * @Description: TODO
 * @author: Administrator
 * @version: V1.0
 * @date: 2016-11-29 上午9:16:11
 */
public class ReaderLine {

	private static String path;
	List<String> list = new ArrayList<String>();

	
	/**
	 * 获取随机行数
	 * 
	 * @param total
	 *            文件总行数
	 * @return 整形参数
	 */
	public int getRandomNumber(int total) {
		return (int) (Math.random() * total);
	}

	/**
	 * @description:  将文件内容按行读取存放到List里面
	 * @param:        @param fileName    
	 * @return:       void    
	 * @throws
	 */
	public void initList(String fileName) {
		try {
			RandomAccessFile accessFile = new RandomAccessFile(fileName, "r");
			String str = "";

			while (null != (str = accessFile.readLine())) {
				list.add(str);
			}
			accessFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @description:  获取随机行数的字符串
	 * @param:        @return    
	 * @return:       String    
	 * @throws
	 */
	public String getStringOfFile() {
		if (null != list) {
			int line = getRandomNumber(list.size());

			return list.get(line);
		}
		return null;
	}

	public String ReaderUserByFile() {
		String user = null;
		ReaderLine rl = new ReaderLine();
		try {
			path = ReaderLine.class.getClassLoader().getResource("").toURI()
					.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		rl.initList(path + "users.txt");
		user = rl.getStringOfFile();
		return user;

	}
}
