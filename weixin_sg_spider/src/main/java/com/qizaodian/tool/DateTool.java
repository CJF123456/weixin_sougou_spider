/**
 * 
 */
package com.qizaodian.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: DateTool
 * @Description: 时间格式的各种转换
 * @author: Administrator
 * @version: V1.0
 * @date: 2016-11-29 下午4:51:36
 */
public class DateTool {

	
	/**
	 * @description: 获取当前时间
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public String getNowTime() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
		String nowTime = dateFormat.format(now);
		return nowTime;
	}

	/**
	 * @description: 获取当前时间戳
	 * @param: @return
	 * @return: Long
	 * @throws
	 */
	public Long getNowLongTime() {
		long longtime = new Date().getTime();
		/*
		 * System.out.println(longtime); long ss=System.currentTimeMillis();
		 * System.out.println(ss);
		 */
		return longtime;
	}

	/**
	 * 
	 * @description: 字符串转化成Date
	 * @param: @param date格式：2008-08-08 12:10:12/2008-08-08
	 * @param: @return
	 * @return: Date
	 * @throws
	 */
	public Date stringToDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date newdate = null;
		try {
			newdate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("转换异常");
		}
		return newdate;
	}

	/**
	 * @description: 时间戳转成时间
	 * @param: @param time
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public String longToDate(String time) {
		String dateTime = null;
		long longtime = Long.parseLong(time);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(longtime * 1000);
		dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c
				.getTime());
		return dateTime;
	}

	
	/**
	 * 
	 * @description:  日期增加 
	 * @param:        @param date1 指定日期
	 * @param:        @param day 增加天数
	 * @param:        @return    
	 * @return:       Date    
	 * @throws
	 */
	public static Date addDate(Date date1,int day){
		Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.add(Calendar.DATE, day);
        Date date=cal1.getTime();
        return date;
	}
	
	
	/**
	 * @description:  两个日期间隔天数 
	 * @param:        @param day1
	 * @param:        @param day2
	 * @param:        @return    
	 * @return:       long    
	 * @throws
	 */
	public static long betweenDate(Date day1, Date day2){        
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    if(day1==null||day2==null){
	    	return 1;
	    }
	    try {	    
	    	String ds1 = sdf.format(day1);
	    	String ds2 = sdf.format(day2);
			day1=sdf.parse(ds1);
			day2=sdf.parse(ds2);
		} catch (ParseException e) {
		}
	    long betweenDate = (day2.getTime()-day1.getTime())/(24*60*60*1000);
	    if(betweenDate<0){
	    	return betweenDate;
	    }
	    return betweenDate+1;
	}
	
	/**
	 * @description:  判断是不是同一天 
	 * @param:        @param day1
	 * @param:        @param day2
	 * @param:        @return    
	 * @return:       boolean    
	 * @throws
	 */
	public static boolean isSameDate(Date day1, Date day2) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;//是同一天
	    } else {
	        return false;//不是同一天
	    }
	}
	
	
}
