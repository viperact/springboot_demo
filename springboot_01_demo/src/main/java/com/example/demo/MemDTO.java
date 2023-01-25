package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor // 파라미터가 없는 생성자
@AllArgsConstructor

@Setter
@Getter
public class MemDTO {
	private String name;
	private int age;
	private String loc;

}
