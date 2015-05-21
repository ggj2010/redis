package com.msds.dubbo.service;

import java.util.List;

import com.msds.dubbo.bean.Note;

public interface NoteService {
	/* 根据主键 */
	Note queryById(String i);
	
	/**
	 * @Description:
	 * @see:例如 select * from tcnote where note_name="文本名称" and author_name="高广金"
	 * @param note
	 * @return:List<Note>
	 */
	List<Note> queryParamAnd(Note note);
	
}
