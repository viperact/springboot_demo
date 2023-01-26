package com.example.demo.backend_todolist.dto;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
CREATE TABLE todolist(
id number PRIMARY KEY,
completed number(1) DEFAULT 0,
todoname VARCHAR(100) NOT NULL);

CREATE SEQUENCE todo_id_seq
START WITH 1
INCREMENT By 1
NOCACHE
NOCYCLE;

INSERT INTO todolist VALUES(todo_id_seq.nextval, 0, '여행');
COMMIT;
SELECT * FROM todolist;
 */

/*
 * 
 * @Data : @Getter, @Setter, @ToString, @EqualAndHashCode, @RequestedArgusConstructor를 합쳐놓은 어노테이션이다.
 * 오버라이딩하여 사용할 수 있도록 제공해준다.
 */

@Component //자동적으로 bean으로 생성되게 해주는 어노테이션
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString //멤버변수값을 문자열로 리턴해주는 어노테이션
//@Data

public class TodoDTO {
	private int id;
	private int completed;
	private String todoname;
	
	

}
