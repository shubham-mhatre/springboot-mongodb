package com.example.sbmgdb.controllers;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sbmgdb.collections.Person;
import com.example.sbmgdb.service.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired private PersonService personService;
	
	@PostMapping //mongoRepositoy save method
	public String savePerson(@RequestBody Person person) {
		return personService.save(person);
	}
	
	@GetMapping("/nameStartWith") //mongoRepositoy findBy method
	public List<Person> getPersonByNameStartWith(@RequestParam("name") String name){
		return personService.getPersonByFirstNameStartWith(name);
	}
	
	@DeleteMapping("/delete/{id}") //mongoRepositoy deleteBy method
	public void deletePersonById(@PathVariable String id) {
		personService.deleteById(id);
	}
	
	
	@GetMapping("/age") //mongoRepositoy @Query annotation
	public List<Person> getPersonByAge(@RequestParam Integer minAge,@RequestParam Integer maxAge){
		return personService.getPersonByAge(minAge,maxAge);
	}
	
	@GetMapping("/search") //using mongoTemplate & Criteria with pagination
	public Page<Person> searchPerson(@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer maxAge,
			@RequestParam(required = false) Integer minAge,
			@RequestParam(required = false) String city,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "5") Integer size){
		Pageable pg = PageRequest.of(page, size);
		return personService.search(name,maxAge,minAge,city,pg);
	}
	
	@GetMapping("/getOldestPersonByCity")
	public List<Document>getOldestPersonByCity(){ //using mongotemplate Aggregation (unwind, sort, aggregate operation)
		return personService.getOldestPersonByCity();
	}
	
	@GetMapping
	public List<Document> getPopulationByCity(){
		return personService.getPopulationByCity();
	}
	
	
}
