package com.msds.dubbo.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.msds.redis.annation.RedisCache;
import com.msds.redis.annation.RedisFieldNotCache;
import com.msds.redis.annation.RedisQuery;

/**
 * @ClassName:NoteBook.java
 * @Description: 笔记本实体类
 * @author gaoguangjin
 * @Date 2015-5-19 下午10:18:23
 */
@Getter
@Setter
@RedisCache
public class NoteBook implements Serializable {

	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "NoteBook";
	@RedisFieldNotCache
	private static final String primaryKey = "noteBookId";

	private int noteBookId;
	@RedisQuery
	private String noteBookName;
	private int textSum;// 统计该笔记本下面有多少文本
	private NoteBookGroup noteBookGroup;
	@RedisQuery
	private Integer flag;
	@RedisQuery
	private Date createdate;

	public int getNoteBookId() {
		return noteBookId;
	}

	public void setNoteBookId(int noteBookId) {
		this.noteBookId = noteBookId;
	}

	public String getNoteBookName() {
		return noteBookName;
	}

	public void setNoteBookName(String noteBookName) {
		this.noteBookName = noteBookName;
	}

	public int getTextSum() {
		return textSum;
	}

	public void setTextSum(int textSum) {
		this.textSum = textSum;
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

	@Override
	public String toString() {
		return "NoteBook [noteBookId=" + noteBookId + ", noteBookName=" + noteBookName + ", textSum=" + textSum
				+ ", noteBookGroup=" + noteBookGroup + ", flag=" + flag + ", createdate=" + createdate + "]";
	}

}
