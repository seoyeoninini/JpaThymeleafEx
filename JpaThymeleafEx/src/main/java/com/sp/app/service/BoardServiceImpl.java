package com.sp.app.service;


import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.domain.Board;
import com.sp.app.repository.BoardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true) // select만 가능
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl  implements BoardService {
	private final BoardRepository boardRepository; 
	
	/*
	  - 클래스 레벨 에서 @Transactional(readOnly = true)를 설정해서 모든 메소드를 변경이 불가능하다.
	  - 따라서 insert, update, delete 에서는 @Transactional 애노테이션을 사용하여 변경이 가능하도록 설정해야 한다.
	  - 클래스 레벨에서 @Transactional(readOnly = true)를 설정하지 않으면 메소드 레벨에서 @Transactional 애노테이션을 사용하지 않아도 가능
	 */
	
	@Transactional
	@Override
	public void insertBoard(Board entity) throws Exception {
		try {
			boardRepository.save(entity);
		} catch (Exception e) {
			log.info(e.toString());
			throw e;
		}
	}

	@Override
	public Page<Board> listPage(String schType, String kwd, int current_page, int size) {
		Page<Board> page = null;
		// current_page - 1: offset 0으로 설정
		try {
			Pageable pageable = PageRequest.of(current_page-1, size, Sort.by(Sort.Direction.DESC, "num"));
			
			if(kwd.length() ==0) {
				page  = boardRepository.findAll(pageable);
			} else if(schType.equals("name")) {
				// page = boardRepository.findByNameContaining(kwd, pageable);
				page = boardRepository.findByName(kwd, pageable);
			} else {
				page = boardRepository.findBySubjectContainingOrContentContaining(kwd, kwd, pageable);
			}
			
		} catch (IllegalArgumentException e) {
			// 게시글이 존재하지 않을 때
		} catch (Exception e) {
			log.info(e.toString());
		}
		
		return page;
	}

	@Override
	public Board findById(long num) {
		Board dto = null;
		
		try {
			Optional<Board> board = boardRepository.findById(num);
			
			dto = board.get();
		} catch (NoSuchElementException e) {
			// 게시글이 존재하지 않은 경우
		} catch (Exception e) {
			log.info(e.toString());
		}
		
		return dto;
	}

	@Override
	public Board findByPrev(String schType, String kwd, long num) {
		Board dto = null;
		try {
			if(kwd.length() == 0) {
				dto = boardRepository.findByPrev(num);
			} else if(schType.equals("name")) {
				dto = boardRepository.findByPrevName(num, kwd);
			} else {
				dto = boardRepository.findByPrevAll(num, kwd);
			}
			
		} catch (Exception e) {
			// log.info(e.toString());
			log.info("BoardService - findByPrev : ", e);
		}
		
		return dto;
	}

	@Override
	public Board findByNext(String schType, String kwd, long num) {
		Board dto = null;
		try {
			if(kwd.length() == 0) {
				dto = boardRepository.findByNext(num);
			} else if(schType.equals("name")) {
				dto = boardRepository.findByNextName(num, kwd);
			} else {
				dto = boardRepository.findByNextAll(num, kwd);
			}
			
		} catch (Exception e) {
			// log.info(e.toString());
			log.info("BoardService - findByNext : ", e);
		}
		
		return dto;
	}
	
	
	@Transactional
	@Override
	public void updateBoard(Board entity) throws Exception {
		try {
			// save() : 레코드가 존재하지 않으면 insert, 존재하면 update
			boardRepository.save(entity);
		} catch (Exception e) {
			log.info(e.toString());
			throw e;
		}
	}

	@Transactional
	@Override
	public void deleteBoard(long num) throws Exception {
		try {
			boardRepository.deleteById(num);
		} catch (Exception e) {
			log.info(e.toString());
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateHitCount(long num) throws Exception {
		try {
			boardRepository.updateHitCount(num);
		} catch (Exception e) {
			log.info(e.toString());
			throw e;
		}
	}
	
}
