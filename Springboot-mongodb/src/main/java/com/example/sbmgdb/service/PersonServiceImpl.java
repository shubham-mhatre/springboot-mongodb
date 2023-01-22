package com.example.sbmgdb.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.example.sbmgdb.collections.Person;
import com.example.sbmgdb.repository.PersonRepository;

@Service
public class PersonServiceImpl implements PersonService {

	@Autowired private PersonRepository personRepository;

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public String save(Person person) {
		return personRepository.save(person).getPersonId();
	}

	@Override
	public List<Person> getPersonByFirstNameStartWith(String name) {
		return personRepository.findByFirstNameStartsWith(name);
	}

	@Override
	public void deleteById(String id) {
		personRepository.deleteById(id);

	}

	@Override
	public List<Person> getPersonByAge(Integer minAge, Integer maxAge) {
		return personRepository.findByPersonAgeBetween(minAge, maxAge);//using @Query annotation
	}

	@Override
	public Page<Person> search(String name, Integer maxAge, Integer minAge, String city, Pageable pg) {
		Query query = new Query().with(pg);

		List<Criteria> criteriaList = new ArrayList<>();

		if(name !=null && !name.isEmpty()) {
			criteriaList.add(Criteria.where("firstName").regex(name, "i"));
		}

		if(maxAge !=null && minAge !=null) {
			criteriaList.add(Criteria.where("age").gte(minAge).lte(maxAge));
		}

		if(city !=null && !city.isEmpty()) {
			criteriaList.add(Criteria.where("address.city").is(city));
		}

		if(!criteriaList.isEmpty()) {
			query.addCriteria(new Criteria()
					.andOperator(criteriaList.toArray(new Criteria[0])));
		}

		Page<Person>pperson = PageableExecutionUtils.getPage(
				mongoTemplate.find(query,Person.class),
						pg,
						()->mongoTemplate.count(query.skip(0).limit(0),Person.class));

		return pperson;
	}

	@Override
	public List<Document> getOldestPersonByCity() {
		UnwindOperation unWind = 
				Aggregation.unwind("address");//flaten the address, so city can be accessed directly
		
		SortOperation sortOp =
				Aggregation.sort(Sort.Direction.DESC,"age");//sort by age desc to get oldest
		
		GroupOperation groupOp =
				Aggregation.group("address.city").first(Aggregation.ROOT).as("oldestPerson");//group by city to get oldest person city wise
		
		Aggregation aggregation = 
				Aggregation.newAggregation(unWind, sortOp, groupOp);
		
		List<Document> personList =
				mongoTemplate.aggregate(aggregation, Person.class,Document.class).getMappedResults();
		
		return personList;
	}

	@Override
	public List<Document> getPopulationByCity() {
		UnwindOperation unWindOp = Aggregation.unwind("address");
		
		GroupOperation groupOp = Aggregation.group("address.city")
				.count().as("populationCount");
		
		SortOperation sortOp = Aggregation.sort(Sort.Direction.DESC,"populationCount");
		
		ProjectionOperation projectionOp = Aggregation.project()
				.andExpression("_id").as("city")
				.andExpression("populationCount").as("count")
				.andExclude("_id");
		
		Aggregation aggregation = Aggregation.newAggregation(unWindOp,groupOp,sortOp,projectionOp);
		
		List<Document>result = mongoTemplate.aggregate(aggregation, Person.class,Document.class)
				.getMappedResults();
		
		return result;
	}

}
