package com.qizaodian.fetcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/***
 * 
 * @ClassName: WeixinFetcher
 * @Description: 通过httpClient 登录搜狗
 * @author: Administrator
 * @date: 2016-11-22 下午3:56:53
 */
public class WeixinFetcher {
   /* private static HttpHost proxy = new HttpHost("183.141.72.67", 312); */
	public static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(30000).setConnectTimeout(30000)
			/*.setProxy(proxy)*/
			.build();
	public static BasicCookieStore cookieStore = new BasicCookieStore();

	public String post(CloseableHttpClient httpClient, String url,
			Map<String, String> params, String charset, String cookie) {
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		String useCharset = charset;
		if (charset == null) {
			useCharset = "utf-8";
		}
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			fillHeader(url, httpPost, cookie);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String key : params.keySet()) {
					nvps.add(new BasicNameValuePair(key, params.get(key)));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			}
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpPost,
					context);
			try {
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, useCharset);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getHtml(CloseableHttpClient httpClient, String url,
			String cookie) {
		HttpClientContext context = HttpClientContext.create();
		HttpGet httpGet = new HttpGet(url);
		fillHeaderWithCookie(url, httpGet, cookie);
		httpGet.setConfig(requestConfig);
		String chartset = null;
		String result = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet,
					context);
			try {
				Header heads[] = response.getAllHeaders();
				for (Header header : heads) {
					if (header.getValue().toLowerCase().contains("charset")) {
						Pattern pattern = Pattern
								.compile("charset=[^\\w]?([-\\w]+)");
						Matcher matcher = pattern.matcher(header.getValue());
						if (matcher.find()) {
							chartset = matcher.group(1);
						}
					}
				}
				if (chartset == null) {
					chartset = "utf-8";
				} else {
					if (chartset.equals("gbk2312")) {
						chartset = "gbk";
					}
				}
				InputStream inputStream = response.getEntity().getContent();
				result = inputStream2String(inputStream, chartset);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getCookiesString() {
		List<Cookie> cookies = cookieStore.getCookies();
		StringBuffer sb = new StringBuffer();
		if (cookies != null) {
			for (Cookie c : cookies) {
				sb.append(c.getName() + "=" + c.getValue() + ";");
			}
		}
		return sb.toString();
	}

	public void fillHeaderWithCookie(String url, HttpGet httpGet, String cookie) {
		httpGet.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
		httpGet.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.setHeader("Accept-Language",
				"zh-CN,zh;q=0.8,en-us;q=0.8,en;q=0.6");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate,sdch");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Cache-Control", "no-cache");
		httpGet.setHeader("Cookie", cookie);
		httpGet.setHeader("Upgrade-Insecure-Requests:", "1");

	}

	private void fillHeader(String url, HttpPost httpPost, String cookie) {
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
		httpPost.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Accept-Language",
				"zh-CN,zh;q=0.8,en-us;q=0.8,en;q=0.6");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate,sdch");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Cache-Control", "no-cache");
		httpPost.setHeader("Cookie", cookie);
		httpPost.setHeader("Upgrade-Insecure-Requests:", "1");

	}

	public String inputStream2String(InputStream is, String charset) {
		String temp = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int i = -1;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
			temp = baos.toString(charset);
			if (temp.contains("???") || temp.contains("�")) {
				Pattern pattern = Pattern
						.compile("<meta[\\s\\S]*?charset=\"{0,1}(\\S+?)\"\\s{0,10}/{0,1}>");
				// .compile("<meta\\s+http-equiv=\"content-type\"\\s+content=\"[\\s\\S]*?charset=(\\S+?)\"\\s+/>");
				Matcher matcher = pattern.matcher(temp.toLowerCase());
				if (matcher.find()) {
					charset = matcher.group(1);
				} else {
					charset = "gbk";
				}
				temp = baos.toString(charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return temp;

	}

}
