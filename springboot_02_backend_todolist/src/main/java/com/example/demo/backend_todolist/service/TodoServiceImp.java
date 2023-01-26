package com.example.demo.backend_todolist.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.backend_todolist.dao.TodoDAO;
import com.example.demo.backend_todolist.dto.TodoDTO;

@Service // 내부에서 자바 로직을 처리함
public class TodoServiceImp implements TodoService{
	
	@Autowired //bean이 자동매핑
	private TodoDAO todoDAO;
	
	public TodoServiceImp() {
		
	}

	@Override
	public List<TodoDTO> search() throws Exception {
		return todoDAO.getTodoList();
	}

	@Override
	public int insert(TodoDTO dto) throws Exception {
		return todoDAO.insertTodoList(dto);
	}

	@Override
	public int update(TodoDTO dto) throws Exception {
		return todoDAO.updateTodoList(dto);
	}

	@Override
	public int delete(int id) throws Exception {
		return todoDAO.deleteTodoList(id);
	}
	
	
}
