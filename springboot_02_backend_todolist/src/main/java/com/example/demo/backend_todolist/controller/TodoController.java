package com.example.demo.backend_todolist.controller;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.backend_todolist.dto.TodoDTO;
import com.example.demo.backend_todolist.service.TodoService;

//http://localhost:8090/todo/all

//@RestController = @Controller + @ResponseBody

@CrossOrigin("*") // 다른포트번호의 요청도 허가해주는 어노테이션 (모든요청허가)
//@CrossOrigin(origins = {"http://localhost:3000"}) // 포트번호 3000만 허가해준다, 자바는 배열을 {}로 처리
@RestController // 현재 class에서 데이터를 보내기만할때 사용
//@Controller
public class TodoController {
	
	@Autowired // 필요한 의존객체의 타입에 해당하는 빈을 찾아 주입한다.
	private TodoService todoService;
	
	public TodoController() {
		System.out.println("controller");
		
	}
	
	// http://localhost:8090/todo/all
	
	//@ResponseBody
	@GetMapping("/todo/all")
	public List<TodoDTO> getList() throws Exception{
		System.out.println("all");
		return todoService.search();
		
	}
	
	// http://localhost:8090/todo
	
	@PostMapping("/todo")
	public ResponseEntity<Object> postTodo(@RequestBody TodoDTO dto) throws Exception{
		System.out.println(dto.getTodoname());
		int chk = todoService.insert(dto);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		HashMap<String, String> map = new HashMap<>();
		map.put("createDate", new Date().toString());
		map.put("message", "insert ok");
		
		if(chk>=1) {

//			responseEntity<Object> body, headers, status
//			return new ResponseEntity<Object>(HttpStatus.OK);
//			return new ResponseEntity<Object>(header, HttpStatus.OK);
//			return new ResponseEntity<Object>(map, header, HttpStatus.OK);
//			return new ResponseEntity<Object>(header, HttpStatus.OK);
			return new ResponseEntity<Object>(map, HttpStatus.OK);
			
		}else {
			return new ResponseEntity<Object>(HttpStatus.NOT_ACCEPTABLE);
		}
		
	}//end postTodo()

	//todo?id=1%complete=0 : @RequestParam
	
	// http://localhost:8090/todo/1/0
	//PathVariable을 사용해 {}값을 넘겨받을 수 있다.
	@PutMapping("/todo/{id}/{completed}")
	public void putTodo(@PathVariable("id") int id, @PathVariable("completed") int completed) throws Exception{
		System.out.printf("id=%d, completed=%d\n", id, completed);
		TodoDTO dto = new TodoDTO();
		dto.setId(id);
		dto.setCompleted(completed == 0 ? 1 : 0); //completed 값이 0이면 1, 아니면 0을 넘겨준다
		todoService.update(dto);
	}//end putTodo()
	
	
	// http://localhost:8090/todo/1
	@DeleteMapping("/todo/{id}")
	public void deleteTodo(@PathVariable("id") int id) throws Exception{
		System.out.println("id:"+ id);
		todoService.delete(id);
	}//end deleteTodo()
	
}//end class














