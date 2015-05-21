package com.msds.test;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class PutArticelUrlByPage implements Runnable {
	public static final String BEGIN_URL = "http://www.tuicool.com/articles/";
	// 推酷上面的 科技和数码栏目 pageNumber 0到20
	// http://www.tuicool.com/ah/0/20?lang=0//科技
	// http://www.tuicool.com/ah/101050000/20?lang=0//数码
	public static final String BEGIN_KJ_URL = "http://www.tuicool.com/ah/0/pageNumber?lang=0";
	public static final String BEGIN_SM_URL = "http://www.tuicool.com/ah/101050000/pageNumber?lang=0";
	
	ConcurrentHashMap<String, String> currentHashMap;
	int number;
	
	public PutArticelUrlByPage(ConcurrentHashMap<String, String> currentHashMap, int i) {
		this.number = i;
		this.currentHashMap = currentHashMap;
	}
	
	public void run() {
		try {
			// 每一个主页开启两个线程
			praseHtml(BEGIN_KJ_URL.replace("pageNumber", "" + number));
			praseHtml(BEGIN_SM_URL.replace("pageNumber", "" + number));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void praseHtml(String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClients = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);// get
		CloseableHttpResponse response = httpClients.execute(httpGet);
		HttpEntity entity = response.getEntity();
		String html = EntityUtils.toString(entity);
		putUrl(html);
	}
	
	private void putUrl(String html) {
		String[] bb = html.split("<a href=\"/articles/");
		// <a href="/articles/3Mbe63y" class="article-list-title" target="_blank" title="Nexpaq Modular Smartphone Case
		for (int i = 1; i < bb.length; i++) {
			String url = BEGIN_URL + bb[i].split("\"")[0];
			String title = bb[i].split("title=\"")[1].split("\"")[0] + ".html";
			title = title.replaceAll("\\/", "").replaceAll("\\*", "").replaceAll("\\?", "").replaceAll("\\|", "");
			currentHashMap.put(title, url);
		}
	}
	
}
