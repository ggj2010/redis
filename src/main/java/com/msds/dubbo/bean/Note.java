package com.msds.dubbo.bean;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import com.msds.redis.annation.RedisCache;
import com.msds.redis.annation.RedisFieldNotCache;
import com.msds.redis.annation.RedisQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName:Note.java
 * @Description: 笔记实体类
 * @author gaoguangjin
 * @Date 2015-3-4 上午11:31:38
 */
@Getter
@Setter
@RedisCache
public class Note implements Serializable {
	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "Note";
	@RedisFieldNotCache
	private static final String primaryKey = "noteId";

	private int noteId;
	private String noteName;// 笔记名称
	@RedisQuery
	private String authorName;// 作者名称
	@RedisQuery
	private String fromUrl;// 文本来源
	private String content;// 文本内容
	private NoteBook noteBook;// 笔记本id
	private NoteBookGroup noteBookGroup;// 笔记本组
	@RedisQuery
	private Integer flag;// 放到BaseBean里面，反射获取不到field值
	@RedisQuery
	private Date createdate;
	@RedisFieldNotCache
	private Blob blobContent;

	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

	public String getNoteName() {
		return noteName;
	}

	public void setNoteName(String noteName) {
		this.noteName = noteName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getFromUrl() {
		return fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NoteBook getNoteBook() {
		return noteBook;
	}

	public void setNoteBook(NoteBook noteBook) {
		this.noteBook = noteBook;
	}

	public NoteBookGroup getNoteBookGroup() {
		return noteBookGroup;
	}

	public void setNoteBookGroup(NoteBookGroup noteBookGroup) {
		this.noteBookGroup = noteBookGroup;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Blob getBlobContent() {
		return blobContent;
	}

	public void setBlobContent(Blob blobContent) {
		this.blobContent = blobContent;
	}

	public String toString() {
		return "输出值==>id=" + noteId + " 笔记本名称：" + noteName + "   文本来源：" + fromUrl + "  作者名称:" + authorName;
	}

}
