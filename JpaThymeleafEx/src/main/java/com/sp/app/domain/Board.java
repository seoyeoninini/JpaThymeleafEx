package com.sp.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 테이블 생성 애노
@Table(name = "bbs") // 테이블 이름 설정(생략시 클래스 명으로)
@Getter
@Setter
@NoArgsConstructor
public class Board {
	
	@Id
	@Column(name = "num", columnDefinition = "NUMBER")
	@SequenceGenerator(name = "S_MY_SEQ", sequenceName = "bbs_seq", allocationSize = 1) 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_MY_SEQ")
	private long num;
	
	@Column(name = "name", nullable = false, length = 30)
	private String name;
	
	@Column(name = "pwd", nullable = false, length = 50)
	private String pwd;
	
	@Column(name = "subject", nullable = false, length = 500)
	private String subject;
	
	@Column(nullable = false, length = 4000)
	private String content;
	
	// 주의 : 필드명이 ipAddr 처럼 중간에 대문자가 있으면
	// 컬럼명은 ip_addr로 설정되므로 name 속성으로 컬럼명을 모두 소문자로 명시해야 함 
	@Column(name = "ipaddr", nullable = false, length = 50, updatable = false)
	private String ipAddr;
	
	@Column(nullable = false, columnDefinition = "DATE DEFAULT SYSDATE", insertable = false, updatable = false)
	private String reg_date;
	
	@Column(name = "hitcount", nullable = false, columnDefinition = "NUMBER DEFAULT 0",  insertable = false, updatable = false)
	private int hitCount;
	
	
}
