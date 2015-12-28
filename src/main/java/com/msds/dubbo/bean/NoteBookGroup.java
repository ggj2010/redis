package com.msds.dubbo.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.msds.redis.annation.RedisCache;
import com.msds.redis.annation.RedisFieldNotCache;
import com.msds.redis.annation.RedisQuery;

/**
 * @ClassName:NoteBookGroup.java
 * @Description: 笔记本组实体类
 * @author gaoguangjin
 * @Date 2015-5-19 下午10:18:40
 */
@Getter
@Setter
@RedisCache
public class NoteBookGroup implements Serializable {
	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "NoteBookGroup";
	@RedisFieldNotCache
	private static final String primaryKey = "noteBookGroupId";

	private int noteBookGroupId;
	@RedisQuery
	private String noteBookGroupName;
	private int textSum;// 统计该笔记本组下面有多少文本
	@RedisQuery
	private Integer flag;
	@RedisQuery
	private Date createdate;

	public int getNoteBookGroupId() {
		return noteBookGroupId;
	}

	public void setNoteBookGroupId(int noteBookGroupId) {
		this.noteBookGroupId = noteBookGroupId;
	}

	public String getNoteBookGroupName() {
		return noteBookGroupName;
	}

	public void setNoteBookGroupName(String noteBookGroupName) {
		this.noteBookGroupName = noteBookGroupName;
	}

	public int getTextSum() {
		return textSum;
	}

	public void setTextSum(int textSum) {
		this.textSum = textSum;
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
		return "NoteBookGroup [noteBookGroupId=" + noteBookGroupId + ", noteBookGroupName=" + noteBookGroupName
				+ ", textSum=" + textSum + ", flag=" + flag + ", createdate=" + createdate + "]";
	}

}
