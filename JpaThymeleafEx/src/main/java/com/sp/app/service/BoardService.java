package com.sp.app.service;

import org.springframework.data.domain.Page;

import com.sp.app.domain.Board;

public interface BoardService {
	public void insertBoard(Board entity) throws Exception;
	
	public Page<Board> listPage(String schType, String kwd, int current_page, int size);
	
	public Board findById(long num);
	public Board findByPrev(String schType, String kwd, long num);
	public Board findByNext(String schType, String kwd, long num);
	
	public void updateBoard(Board entity) throws Exception;
	public void deleteBoard(long num) throws Exception;
	
	public void updateHitCount(long num) throws Exception;
	
	
	
	
}
