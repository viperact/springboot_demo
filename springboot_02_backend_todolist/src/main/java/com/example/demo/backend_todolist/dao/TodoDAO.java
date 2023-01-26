package com.example.demo.backend_todolist.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.demo.backend_todolist.dto.TodoDTO;

@Mapper // 메소드명(getTodoList)과 같은 이름의 값을 찾아줌
@Repository // 
public interface TodoDAO {
	//todoMapper.xml의 id값과 똑같은 메소드를 줌
	public List<TodoDTO> getTodoList() throws Exception;
	public int insertTodoList(TodoDTO dto) throws Exception;
	public int updateTodoList(TodoDTO dto) throws Exception;
	public int deleteTodoList(int id) throws Exception;
}
