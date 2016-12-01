/**
 * 
 */
package com.qizaodian.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.qizaodian.fetcher.HttpClientBuilder;
import com.qizaodian.fetcher.WeixinFetcher;
import com.qizaodian.login.Token4Sogou;

/**
 * 
 * @ClassName: WeixinSpider
 * @Description: 搜狗微信采集系统
 * @author: Administrator
 * @version: V1.0
 * @date: 2016-11-24
 */
public class WeixinSpider {
	private static String keyword = "爱情";
	private static CloseableHttpClient httpClient = null;
	private static String firstUrl = "https://account.sogou.com/web/webLogin";
	private static String loginUrl = "https://account.sogou.com/web/login";
	private static String cookieUrl = "https://pb.sogou.com/pv.gif?uigs_productid=ufo&ufoid=passport&rdk=1479803139020&img=pv.gif&b=ff&v=49&o=win6.1&s=1920x1080&l=zh-CN&bi=64&ls=1_1&refer=&page=搜狗通行证&pageUrl=https://account.sogou.com/web/webLogin&productid=passport&ptype=web&pcode=index";
								   
	private static String callUrl = "https://account.sogou.com/static/api/jump.htm?status=0&needcaptcha=0&msg=";
	private static String ssologinUrl = "https://account.sogou.com/";
	private static WeixinFetcher fetcher = new WeixinFetcher();

	private String path = null;
	/** 标记位 **/
	private int num = 0;

	public static void main(String[] args) {
		new WeixinSpider().Spider();
	}

	/**
	 * @description: 采集程序入口
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void Spider() {
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		HttpClientBuilder httpClientBuilder = new HttpClientBuilder(false,
				poolingHttpClientConnectionManager, fetcher.cookieStore);
		httpClient = httpClientBuilder.getHttpClient();
		saveWeixin();
	}

	/**
	 * @description: TODO(这里用一句话描述这个方法的作用)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void saveWeixin() {
		boolean isLogin = login();
		if (isLogin) {
			spiderList(1);
		}

	}

	/**
	 * @description: TODO(这里用一句话描述这个方法的作用)
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	private boolean login() {
		int loginNum = 0;
		boolean isLogin = false;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String user = new ReaderLine().ReaderUserByFile();
			String name = user.split(",")[0];
			String pwd = user.split(",")[1];
			if (login(name, pwd)) {
				System.out.println("登录成功,欢迎您:" + name + "!");
				isLogin = true;
				break;
			}
			loginNum++;
			if (loginNum >= 10) {
				break;
			}
		}
		return isLogin;
	}

	/**
	 * @description: 控制层
	 * @param: @param i
	 * @return: void
	 * @throws
	 */
	private void spiderList(int i) {
		// 控制采集的页数 可以修改
		boolean iskd = false;
		for (; i <= 100; i++) {
			if (!spiderDetail(i)) {
				num = i;
				iskd = true;
				break;
			}
		}
		if (iskd) {
			boolean isLogin = login();
			if (isLogin) {
				spiderList(num);
			}
		}

	}

	/**
	 * @description:  保存详情 
	 * @param:        @param i
	 * @param:        @return    
	 * @return:       boolean    
	 * @throws
	 */
	public boolean spiderDetail(int i) {
		System.out.println("现在采集到第" + i + "页,请耐心等待！");
		String url = "http://weixin.sogou.com/weixin?query="
				+ keyword
				+ "&_sug_type_=&sut=769&lkt=1%2C1479803756148%2C1479803756148&_sug_=y&type=2&sst0=1479803756249&page="
				+ i + "&ie=utf8&w=01019900&dr=1";
		String sougouWeixinhtml = fetcher.getHtml(httpClient, url,
				fetcher.getCookiesString());
		Document document = Jsoup.parse(sougouWeixinhtml);
		Elements resultsElements = document.select("div.news-box");
		if (resultsElements.size() == 0) {
			return false;
		} else {
			Elements aElements = resultsElements.first().select(
					"ul.news-list>li");
			for (Element element : aElements) {
				String con = element.select("h3>a").text();
				System.out.println(con);
				path = Class.class.getClass().getResource("/").getPath();
				saveToFile(path, keyword, con);
			}
		}
		return true;

	}

	/**
	 * @description: 模拟浏览器登录搜狗微信
	 * @param: @param username
	 * @param: @param password
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public boolean login(String name, String pwd) {
		HashMap<String, String> params = null;
		String token = new Token4Sogou().getToken();
		params = new HashMap<String, String>();
		params.put("username", name);
		params.put("password", pwd);
		params.put("captcha", "");
		params.put("autoLogin", "0");
		params.put("client_id", "1120");
		params.put("xd", "https://account.sogou.com/static/api/jump.htm");
		params.put("token", token);
		try {
			fetcher.cookieStore.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fetcher.getHtml(httpClient, firstUrl, "");
		fetcher.getHtml(httpClient, cookieUrl, "");
		long dateTime = new Date().getTime();
		String checkUrl = "https://account.sogou.com/web/login/checkNeedCaptcha?username="
				+ name + "&client_id=1120&t=" + dateTime;
		fetcher.getHtml(httpClient, checkUrl, fetcher.getCookiesString());
		sleep();
		fetcher.post(httpClient, loginUrl, params, "utf-8",
				fetcher.getCookiesString());
		// 校验
		fetcher.getHtml(httpClient, callUrl, fetcher.getCookiesString());
		String result = fetcher.getHtml(httpClient, ssologinUrl,
				fetcher.getCookiesString());
		if (result.contains("我的帐号")) {
			System.out.println("登录成功！");
			return true;
		}
		System.out.println("您已连续登录十次失败，请检查您的程序！");
		return false;

	}

	/**
	 * @description: 休眠
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void sleep() {
		try {
			Random rand = new Random();
			int randNum = rand.nextInt(1) + 2;
			Thread.sleep(1000 * randNum);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @description: 写成文件
	 * @param: @param path
	 * @param: @param name
	 * @param: @param con
	 * @return: void
	 * @throws
	 */
	public void saveToFile(String path, String keyword, String con) {
		if (!keyword.endsWith(".txt"))
			keyword = keyword + ".txt";
		File file = new File(path + File.separator + keyword);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file, true);
			fos.write(con.getBytes());
			fos.write('\r');
		} catch (Exception e) {
			System.out.println("文件出错");
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
