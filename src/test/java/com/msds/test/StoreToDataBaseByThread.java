package com.msds.test;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.msds.dubbo.bean.Note;
import com.msds.dubbo.bean.NoteBook;
import com.msds.dubbo.bean.NoteBookGroup;

/**
 * @ClassName:StoreToDataBase.java
 * @Description: 根据url 读取内容写入到表里面
 * @author gaoguangjin
 * @Date 2015-5-15 下午6:08:19
 */
@Slf4j
public class StoreToDataBaseByThread {

	private static Logger log = Logger.getLogger(StoreToDataBaseByThread.class);

	private ConcurrentHashMap<String, String> currentHashMap;
	private Session session;
	private CountDownLatch latch;
	// 默认笔记本组id是1
	private NoteBookGroup noteBookGroup = new NoteBookGroup();

	// 多线程httpclient解决超时连接
	private static RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(500000)
			.setConnectTimeout(500000).build();
	// http请求连接池
	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	// 一个服务器只有一个客户端
	private CloseableHttpClient httpClients = HttpClients.custom().setConnectionManager(cm)
			.setDefaultRequestConfig(defaultRequestConfig).build();

	public StoreToDataBaseByThread(ConcurrentHashMap<String, String> currentHashMap, Session session,
			CountDownLatch latch) {
		this.currentHashMap = currentHashMap;
		this.session = session;
		this.latch = latch;
	}

	/**
	 * @Description:
	 * @see:多单线程插入==》map大小： 插入数据库耗时：50103ms
	 * @return:void
	 */
	public List<Note> insertToDatabase() {
		final Random random = new Random();
		noteBookGroup.setNoteBookGroupId(1);
		final List<Note> noteList = new CopyOnWriteArrayList<Note>();
		Iterator<String> iter = currentHashMap.keySet().iterator();
		try {
			while (iter.hasNext()) {
				final String title = iter.next();
				final String path = currentHashMap.get(title);
				new Thread() {
					public void run() {
						Note note = new Note();
						NoteBook nb = new NoteBook();
						String content = getHtmlByPath(path);
						Blob clobContent;
						try {
							clobContent = session.getLobHelper().createBlob(content.getBytes("utf-8"));
							note.setBlobContent(clobContent);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						note.setFromUrl(path);

						note.setNoteName(title);
						note.setAuthorName("高广金");
						note.setCreatedate(new Date());
						if (content.contains("error:httpClients读取网页内容失败")) {
							note.setFlag(1);
						} else {
							note.setFlag(0);
						}
						// 随机存放笔记本名称
						if (random.nextInt(2) == 1) {
							nb.setNoteBookId(1);
						} else {
							nb.setNoteBookId(2);
						}

						note.setNoteBookGroup(noteBookGroup);
						note.setNoteBook(nb);
						noteList.add(note);
						latch.countDown();
					}
				}.start();
			}

		} catch (Exception e) {
			log.error("爬虫抓取网页内容写入到数据库失败！" + e.getLocalizedMessage());
		}
		return noteList;
	}

	private String getHtmlByPath(String url) {
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);// get
			// 解决超时
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).build();
			httpGet.setConfig(requestConfig);
			CloseableHttpResponse response = httpClients.execute(httpGet);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "utf-8");

		} catch (Exception e) {
			log.error("httpClients读取网页内容失败！" + e.getLocalizedMessage() + ":" + url);
			return "error:httpClients读取网页内容失败：失败地址" + url + "===错误信息" + e.getLocalizedMessage();
		} finally {
			httpGet.releaseConnection();
		}
	}

}
