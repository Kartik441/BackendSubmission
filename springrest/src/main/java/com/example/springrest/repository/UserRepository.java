package com.example.springrest.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.springrest.model.Users;

public interface UserRepository extends MongoRepository<Users, String>{
	
	// search
	
	public List<Users> findByEmail(String email);
	public List<Users> findByName(String name);
	public List<Users> findByPhone(String phone);


}
