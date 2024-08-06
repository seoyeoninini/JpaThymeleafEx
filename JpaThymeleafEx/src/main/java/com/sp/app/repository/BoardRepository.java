package com.sp.app.repository;

import java.sql.SQLException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.domain.Board;
// <entity, 기본키 자료형>
public interface BoardRepository  extends JpaRepository<Board, Long>{
	/*
	 * - LIKE 검색
	 *   findBy컬럼명Containing(String 필드)
	 *   findBy컬럼명1ContainigOrFindBy컬럼명2Conataining(String 필드명1,String 필드명2) // 필드명1 -> 컬럼명1, 필드명2 -> 컬럼명2 에 각각 매칭 
	 *   findBy컬럼명1ContainigAndFindBy컬럼명2Conataining(String 필드명1,String 필드명2) // 필드명1 -> 컬럼명1, 필드명2 -> 컬럼명2 에 각각 매칭 
	 */
	
	// 이름 검색. 26~30번 쿼리랑 같은 기능. 둘 중 하나 사용하면 된다.
	// public Page<Board> findByNameContaining(String kwd, Pageable pageable);
	public Page<Board> findBySubjectContainingOrContentContaining(String subject, String content, Pageable pageable);
	
	// 이름 검색
	// JPQL을 이용한 검색. 페이징 처리를 위해 countQuery 속성 추가
	// From 테이블 명이 아닌 도메인명
	@Query(
			value = "Select b From Board b WHERE b.name LIKE %:kwd%",
			countQuery = "SELECT COUNT(b.num) FROM Board b WHERE b.name LIKE %:kwd%"
	)
	public Page<Board> findByName(@Param("kwd") String kwd, Pageable pageable);
	
	// nativeQuery : false 이면 JPQL, true 이면 SQL, 기본은 false
	// @Modifying : update, delete 시 반드시 붙여야 하는  애노
	// 네이티브(오라클)을 이용한 쿼리. :num이 ?
	@Modifying
	@Query(value = "UPDATE bbs SET hitCount = hitCount+1 WHERE num = :num", 
			nativeQuery = true)
	public void updateHitCount(@Param("num") long num) throws SQLException;
	
	@Query(value = "SELECT * FROM bbs WHERE num>:num ORDER BY num ASC FETCH FIRST 1 ROWS ONLY ",
			nativeQuery = true )
	public Board findByPrev(@Param("num") long num);
	
	@Query(value = "SELECT * FROM bbs WHERE num>:num AND name LIKE '%'||:kwd||'%' ORDER BY num ASC FETCH FIRST 1 ROWS ONLY ", 
			nativeQuery = true)
	public Board findByPrevName(@Param("num") long num, @Param("kwd") String kwd );
	
	@Query(value = "SELECT * FROM bbs WHERE num>:num AND (subject LIKE '%'||:kwd||'%' OR content LIKE '%'||:kwd||'%') ORDER BY num ASC FETCH FIRST 1 ROWS ONLY ", 
			nativeQuery = true)
	public Board findByPrevAll(@Param("num") long num, @Param("kwd") String kwd );

	
	@Query(value = "SELECT * FROM bbs WHERE num<:num ORDER BY num DESC FETCH FIRST 1 ROWS ONLY ",
			nativeQuery = true )
	public Board findByNext(@Param("num") long num);
	
	@Query(value = "SELECT * FROM bbs WHERE num<:num AND name LIKE '%'||:kwd||'%' ORDER BY num DESC FETCH FIRST 1 ROWS ONLY ", 
			nativeQuery = true)
	public Board findByNextName(@Param("num") long num, @Param("kwd") String kwd);
	
	@Query(value = "SELECT * FROM bbs WHERE num<:num AND (subject LIKE '%'||:kwd||'%' OR content LIKE '%'||:kwd||'%') ORDER BY num DESC FETCH FIRST 1 ROWS ONLY ", 
			nativeQuery = true)
	public Board findByNextAll(@Param("num") long num, @Param("kwd") String kwd);
}
