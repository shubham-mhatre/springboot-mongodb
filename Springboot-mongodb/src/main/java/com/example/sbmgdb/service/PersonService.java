package com.example.sbmgdb.service;

import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.sbmgdb.collections.Person;

public interface PersonService {

	String save(Person person);

	List<Person> getPersonByFirstNameStartWith(String name);

	void deleteById(String id);

	List<Person> getPersonByAge(Integer minAge, Integer maxAge);

	Page<Person> search(String name, Integer maxAge, Integer minAge, String city, Pageable pg);

	List<Document> getOldestPersonByCity();

	List<Document> getPopulationByCity();

}
