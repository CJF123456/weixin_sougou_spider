package com.qizaodian.login;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


/***
 * @ClassName: Token4Sogou
 * @Description: 通过Js 获取搜狗Token
 * @author: Administrator
 * @date: 2016-11-22 
 */
public class Token4Sogou {

	public static void main(String[] args) {
		System.out.println(new Token4Sogou().getToken());
	}

	public static Invocable invoke = null;
	static {
		// String path = Token4Sogou.class.getClassLoader().getResource("")
		// .getPath();
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine jsEngine = manager.getEngineByName("javascript");
		String jsFileName = "token4sogou.js";
		Reader reader;
		try {
			InputStream in = Token4Sogou.class.getClassLoader()
					.getResourceAsStream(jsFileName);
			reader = new InputStreamReader(in);
			jsEngine.eval(reader);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		invoke = (Invocable) jsEngine;

	}

	public static String getToken() {
		String pass = null;
		try {
			pass = (String) invoke.invokeFunction("getToken");
		} catch (Exception e) {
			System.out.println("为登陆密码加密时，出现异常!");
			e.printStackTrace();
		}
		return pass;
	}
}
