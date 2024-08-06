package com.sp.app.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.domain.Board;
import com.sp.app.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bbs/*")
public class BoardController {
	private final BoardService service;

	@RequestMapping(value = "list", method = {RequestMethod.GET, RequestMethod.POST})
	public String list(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			HttpServletRequest req,
			Model model) throws Exception {

		try {
			if (req.getMethod().equalsIgnoreCase("GET")) { // GET 방식인 경우
				kwd = URLDecoder.decode(kwd, "utf-8");
			}

			int total_page = 0;
			int size = 10;
			long dataCount = 0;
			List<Board> list = null;
			
			Page<Board> pageBoard = service.listPage(schType, kwd, current_page, size);
			
			if(! pageBoard.isEmpty()) {
				total_page = pageBoard.getTotalPages();
				if (current_page > total_page) {
					current_page = total_page;
					pageBoard = service.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pageBoard.getTotalElements();
				
				list = pageBoard.getContent();
				for (Board dto : list) {
					dto.setReg_date(dto.getReg_date().substring(0, 10));
				}
			} else {
				current_page = 0;
			}
				
			model.addAttribute("list", list);
			model.addAttribute("page", current_page);
			model.addAttribute("dataCount", dataCount);
			model.addAttribute("size", size);
			model.addAttribute("total_page", total_page);

			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);

		} catch (Exception e) {
			log.info("BoardController - list : ", e);
		}

		return "bbs/list";
	}

	@GetMapping("write")
	public String writeForm(Model model) throws Exception {
		model.addAttribute("mode", "write");
		return "bbs/write";
	}

	@PostMapping("write")
	public String writeSubmit(Board dto, HttpServletRequest req) throws Exception {
		try {
			dto.setIpAddr(req.getRemoteAddr());
			service.insertBoard(dto);
		} catch (Exception e) {
			log.info("BoardController - writeSubmit : ", e);
		}

		return "redirect:/bbs/list";
	}

	@GetMapping("article/{num}")
	public String article(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model) throws Exception {

		kwd = URLDecoder.decode(kwd, "utf-8");

		String query = "page=" + page;
		if (kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
		}

		// 조회수 증가 및 해당 레코드 가져 오기
		service.updateHitCount(num);

		Board dto = service.findById(num);
		if (dto == null) {
			return "redirect:/bbs/list?" + query;
		}

		// 스타일로 처리하는 경우 : style="white-space:pre;"
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

		// 이전 글, 다음 글
		Board prevDto = service.findByPrev(schType, kwd, num);
		Board nextDto = service.findByNext(schType, kwd, num);

		model.addAttribute("dto", dto);
		model.addAttribute("prevDto", prevDto);
		model.addAttribute("nextDto", nextDto);

		model.addAttribute("page", page);
		model.addAttribute("query", query);
		model.addAttribute("schType", schType);
		model.addAttribute("kwd", kwd);

		return "bbs/article";
	}

	@GetMapping("delete/{num}")
	public String delete(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd)
			throws Exception {

		kwd = URLDecoder.decode(kwd, "utf-8");
		String query = "page=" + page;
		if (kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
		}

		try {
			// 자료 삭제
			service.deleteBoard(num);
		} catch (Exception e) {
		}

		return "redirect:/bbs/list?" + query;
	}

	@GetMapping("update/{num}")
	public String updateForm(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			Model model) throws Exception {

		Board dto = service.findById(num);
		if (dto == null) {
			return "redirect:/bbs/list?page=" + page;
		}

		model.addAttribute("mode", "update");
		model.addAttribute("page", page);
		model.addAttribute("dto", dto);

		return "bbs/write";
	}

	@PostMapping("update")
	public String updateSubmit(Board dto, @RequestParam(name = "page") String page) throws Exception {

		try {
			// 수정 하기
			service.updateBoard(dto);
		} catch (Exception e) {
		}

		return "redirect:/bbs/list?page=" + page;
	}

}
