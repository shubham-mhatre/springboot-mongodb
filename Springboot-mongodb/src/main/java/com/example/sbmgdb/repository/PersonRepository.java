package com.example.sbmgdb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.sbmgdb.collections.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
	
	List<Person> findByFirstNameStartsWith(String name);
	
	//List<Person> findByAgeBetween(Integer minAge,Integer maxAge);//default impl using between keyword
	
	@Query(value= "{age:{$gt : ?0, $lt:?1}}",
			fields= "{address:0}") //fields we can choose which fields to be selected, 1 for selected 0 to exclude
			//fields="{personId:1}")
			List<Person> findByPersonAgeBetween(Integer minAge,Integer maxAge);

}
