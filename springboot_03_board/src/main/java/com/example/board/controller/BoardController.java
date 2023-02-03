package com.example.board.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageDTO;
import com.example.board.service.BoardService;

//http://localhost:8090/list.sb

//http://localhost:8090/board/list

//@CrossOrigin("*")
@CrossOrigin(origins = {"http://localhost:3000"})


@RestController
//@Controller
public class BoardController {

	@Autowired
	private BoardService service;
	
	@Autowired
	private PageDTO pdto;
	
	private int currentPage;
	
	@Value("$(spring.servlet.multipart.location)")
	private String filePath;

	public BoardController() {

	}

	public void setService(BoardService service) {
		this.service = service;
	}

	
//	@RequestMapping("/list.sb")
	@RequestMapping("/board/list/{currentPage}") // react
//	public ModelAndView listMethod(PageDTO pv, ModelAndView mav) {
	public Map<String, Object> listMethod(@PathVariable("currentPage")  int currentPage, PageDTO pv) {
		
		Map<String, Object> map = new HashMap<>(); // react
		int totalRecord = service.countProcess();
		if (totalRecord >= 1) {
			if (pv.getCurrentPage() == 0)
				this.currentPage = 1;
			else
				this.currentPage = pv.getCurrentPage();

			this.pdto = new PageDTO(this.currentPage, totalRecord);
			List<BoardDTO> aList = service.listProcess(this.pdto);
//			mav.addObject("aList", aList);
//			mav.addObject("pv", this.pdto);
			System.out.println(aList); // react
			map.put("aList", aList); // react
			map.put("pv", this.pdto); // react
		}

//		mav.setViewName("board/list");
//		return mav;
		return map; // react
	}// end listMethod()

//	@RequestMapping(value = "/write.sb", method = RequestMethod.GET)
	@RequestMapping(value = "/board/write", method = RequestMethod.GET) // react
	public ModelAndView writeMethod(BoardDTO dto, PageDTO pv, ModelAndView mav) {
		if (dto.getRef() != 0) { // 답변글이면
			mav.addObject("currentPage", pv.getCurrentPage());
			mav.addObject("dto", dto);
		}
		mav.setViewName("board/write");
		return mav;
	}// end writeMethod()
	
	/*
	 * RequsetBody : json => 자바객체
	 * ResponseBody : 자바객체 => json
	 * @PathVariable : /board/list/:num => /board/list/1 => /board/list/{num}
	 * @RequsetParm : /board/list?name=value => /board/list?num=1 => /board/list
	 * multipart/form-data : @RequsetBody 선언없이 그냥 받음 BoardDTO dto
	 */

//	@RequestMapping(value = "/write.sb", method = RequestMethod.POST)
	@RequestMapping(value = "/board/write", method = RequestMethod.POST) // react
//	public String writeProMethod(BoardDTO dto, PageDTO pv, HttpServletRequest request) {
	public String writeProMethod(BoardDTO dto, PageDTO pv, HttpServletRequest request) throws IllegalStateException, IOException {
		MultipartFile file = dto.getFilename();
		if (file != null && !file.isEmpty()) { // 첨부파일이 비어있지 않으면
			UUID random = saveCopyFile(file, request);
			dto.setUpload(random + "_" + file.getOriginalFilename());
			// c:\\download\\temp 경로에 첨부파일 저장
			file.transferTo(new File(file.getOriginalFilename()));

		}

		dto.setIp(request.getRemoteAddr()); // 현재 서버에 접속한 클라이언트의 ip주소를 가져옴
		
		service.insertProcess(dto);

		// 답변글이면
		if (dto.getRef() != 0) {
//			return "redirect:/list.sb?currentPage=" + pv.getCurrentPage();
			return "redirect:/board/list/" + pv.getCurrentPage(); // react
		} else {
//			return "redirect:/list.sb";
			return "redirect:/board/list/1"; // react
		}
	}// end writeProMethod()
	
//	@RequestMapping(value = "/update.sb", method = RequestMethod.GET)
	@RequestMapping(value = "/board/update/{num}", method = RequestMethod.GET) // react
//	public ModelAndView updateMethod(int num, int currentPage, ModelAndView mav) {
	public BoardDTO updateMethod(int num) {
//			mav.addObject("dto", service.updateSelectProcess(num));
//			mav.addObject("currentPage", currentPage);
//			mav.setViewName("board/update");
//		return mav;
		
		return service.updateSelectProcess(num);
	}//end updateMethod()
	
//	@RequestMapping(value = "/update.sb", method = RequestMethod.POST)
	@RequestMapping(value = "/board/update", method = RequestMethod.PUT) // react
//	public String updateProMethod(BoardDTO dto, int currentPage, HttpServletRequest request) {
	public void updateProMethod(@RequestBody BoardDTO dto, HttpServletRequest request) {
//		System.out.printf("num: %d, writer:%s\n", dto.getWriter()); // react
		MultipartFile file = dto.getFilename();
//		if(!file.isEmpty()) {
		if(file != null && !file.isEmpty()) {
			UUID random = saveCopyFile(file, request);
			dto.setUpload(random + "_" + file.getOriginalFilename());
		}
		service.updateProcess(dto, urlPath(request));
//		return "redirect:/list.sb?currentPage=" + currentPage;
//		return "redirect:/board/list?currentPage=" + currentPage; // react
	}//end updateProMethod
	
//	@RequestMapping(value = "/delete.sb")
	@RequestMapping(value = "/board/delete/{num}", method = RequestMethod.DELETE) // react
//	public String deleteMethod(int num, int currentPage, HttpServletRequest request) {
	public void deleteMethod(@PathVariable("num") int num, HttpServletRequest request) {
		service.deleteProcess(num, urlPath(request));
		
		int totalRecord = service.countProcess();
		this.pdto = new PageDTO(this.currentPage, totalRecord);
		
//		return "redirect:/list.sb?currentPage=" + this.pdto.getCurrentPage();
		
	}//end deleteMethod
	
	

	private UUID saveCopyFile(MultipartFile file, HttpServletRequest request) {
		String fileName = file.getOriginalFilename();

		// 중복파일명을 처리하기 위해 난수 발생
		UUID random = UUID.randomUUID();

		File fe = new File(urlPath(request));
		if (!fe.exists()) {
			fe.mkdir();
		}

		File ff = new File(urlPath(request), random + "_" + fileName);

		try {
			FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(ff));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return random;
	}// end saveCopyFile()

	// 첨부파일이 넘어오면 첨부파일을 저장할 위치
	private String urlPath(HttpServletRequest request) {
		String root = request.getSession().getServletContext().getRealPath("/");
		// 경로 :
		// C:\big_study\spring_workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\spring_08_board
		System.out.println("root:" + root);
		String saveDirectory = root + "temp" + File.separator;
		return saveDirectory;
	}// end urlPath()

//	@RequestMapping("/view.sb")
	@RequestMapping("/board/view/{num}") // react
//	public ModelAndView viewMethod(int currentPage, int num, ModelAndView mav) {
	public BoardDTO viewMethod(@PathVariable("num") int num) {
//		mav.addObject("dto", service.contentProcess(num));
//		mav.addObject("currentPage", currentPage);
//		mav.setViewName("board/view");
//		return mav;
		
		return service.contentProcess(num);
	}// end viewMethod()

//	@RequestMapping("/contentdownload.sb")
	@RequestMapping("/board/contentdownload") // react
	public ModelAndView downMethod(int num, ModelAndView mav) {
		mav.addObject("num", num);
		mav.setViewName("download");
		return mav;
	}// end downMethod()

}// end class
