package com.example.sbmgdb.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Document(collection="person")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {

	@Id
	private String personId;
	private String firstName;
	private String lastName;
	private Integer age;
	private List<String>hobbies;
	private List<Address>address;
	
}
