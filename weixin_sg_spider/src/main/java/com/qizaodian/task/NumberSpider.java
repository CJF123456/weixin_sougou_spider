/**
 * 
 */
package com.qizaodian.task;

import java.util.Date;
import java.util.HashMap;

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
 * @ClassName: NumberSpider
 * @Description: TODO
 * @author: Administrator
 * @version: V1.0
 * @date: 2016-11-30 下午5:27:41
 */
public class NumberSpider {

	public static void main(String[] args) {
		new NumberSpider().Spider();

	}

	/**
	 * @description: Spider采集入口
	 * @param:
	 * @return: void
	 * @throws
	 */
	String user = new ReaderLine().ReaderUserByFile();
	String name = user.split(",")[0];
	String pwd = user.split(",")[1];
	WeixinFetcher fetcher = new WeixinFetcher();
	CloseableHttpClient httpClient = null;
	private String firstUrl = "https://account.sogou.com/web/webLogin";
	private String loginUrl = "https://account.sogou.com/web/login";
	private String callUrl = "https://account.sogou.com/static/api/jump.htm?status=0&needcaptcha=0&msg=";
	// private String
	// cookieUrl="https://pb.sogou.com/pv.gif?uigs_productid=ufo&ufoid=passport&rdk=1479803139020&img=pv.gif&b=ff&v=49&o=win6.1&s=1920x1080&l=zh-CN&bi=64&ls=1_1&refer=&page=搜狗通行证&pageUrl=https://account.sogou.com/web/webLogin&productid=passport&ptype=web&pcode=index";
	private String ssologinUrl = "https://account.sogou.com/";
	private String cookieUrl="https://pb.sogou.com/pv.gif?uigs_productid=ufo&ufoid=passport&rdk=1479803139020&img=pv.gif&b=ff&v=49&o=win6.1&s=1920x1080&l=zh-CN&bi=64&ls=1_1&refer=&page=搜狗通行证&pageUrl=https://account.sogou.com/web/webLogin&productid=passport&ptype=web&pcode=index";
								   

	private void Spider() {

		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		HttpClientBuilder builder = new HttpClientBuilder(false,
				poolingHttpClientConnectionManager, fetcher.cookieStore);
		httpClient = builder.getHttpClient();
		saveWexin();
		

	}

	/**
	 * @description: TODO(这里用一句话描述这个方法的作用)
	 * @param: @param name2
	 * @param: @param pwd2
	 * @return: void
	 * @throws
	 */
	public boolean login(String name, String pwd) {
		HashMap<String, String> params = null;
		params = new HashMap<String, String>();
		String token = new Token4Sogou().getToken();
		params = new HashMap<String, String>();
		params.put("username", name);
		params.put("password", pwd);
		params.put("captcha", "");
		params.put("autoLogin", "0");
		params.put("client_id", "1120");
		params.put("xd", "https://account.sogou.com/static/api/jump.htm");
		params.put("token", token);
		fetcher.cookieStore.clear();
		// 进入通行证
		fetcher.getHtml(httpClient, firstUrl, "");
		fetcher.getHtml(httpClient, cookieUrl, "");
		long dateTime = new Date().getTime();
		String checkUrl = "https://account.sogou.com/web/login/checkNeedCaptcha?username="
				+ name + "&client_id=1120&t=" + dateTime;
		fetcher.getHtml(httpClient, checkUrl, fetcher.getCookiesString());
		fetcher.post(httpClient, loginUrl, params, "utf-8",
				fetcher.getCookiesString());
		 fetcher.getHtml(httpClient, callUrl,fetcher.getCookiesString()); 
		String result = fetcher.getHtml(httpClient, ssologinUrl,
				fetcher.getCookiesString());
		if (result.contains("我的帐号")) {
			System.out.println("=============="+name);
			System.out.println("登录成功！");
			return true;
		}
		System.out.println("您已连续登录十次失败，请检查您的程序！");
		return false;

	}

	/**
	 * @description: TODO(这里用一句话描述这个方法的作用)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void saveWexin() {
		login(name, pwd);
		System.out.println(name);
			String url ="http://weixin.sogou.com/weixin?query=%E9%93%B6%E8%A1%8C&_sug_type_=&sut=326&lkt=3%2C1480562841053%2C1480562841053&_sug_=y&type=1&sst0=1480562841173&page=9&ie=utf8";
			String sougouWeixinhtml = fetcher.getHtml(httpClient, url,
					fetcher.getCookiesString());
			Document document = Jsoup.parse(sougouWeixinhtml);
			System.out.println(document);
			String url2 = document.select("img#seccodeImage").attr("src");
			System.out.println(url2);
			/*Elements resultsElements = document.select("div.news-box");
			System.out.println(resultsElements);
			
				Elements aElements = resultsElements.first().select(
						"ul.news-list2>li");
				
				for (Element element : aElements) {
					System.out.println(element);
					String con = element.select("div.gzh-box2>div.txt-box>p.tit>a").text();
					System.out.println(con);
					path = Class.class.getClass().getResource("/").getPath();
					saveToFile(path, keyword, con);
				
			}*/
}



}
