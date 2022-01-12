package com.example.springrest.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springrest.model.Users;
import com.example.springrest.repository.UserRepository;


@RestController
@CrossOrigin("http://localhost:3000")
public class WebController {
	
	
    private UserRepository UserRepo;

		@Autowired
		public WebController(UserRepository userRepository)
		{
			UserRepo = userRepository;
		}
		
///////////////////////////////////////////////////////////////////////////////////////////////		

		// get all users without pagination
		
		@GetMapping("/users")
		public ResponseEntity<List<Users>> getAllUsers()
		{
			// 1. Show all the data
			
		    
		    try {
		    	List<Users> list = UserRepo.findAll();
		    	
				if(list.isEmpty())
					return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
				
			    return ResponseEntity.of(Optional.of(list));
		    }
		    catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		    
		}
		
		
///////////////////////////////////////////////////////////////////////////////////////////////
		

		
//		 get all users using pagination after sorting on multiple fields
		
		@GetMapping("/users/api")
		public ResponseEntity<Page<Users>> getAllUsers(@RequestParam(required = false, defaultValue = "10") int pageSize, @RequestParam(required = false, defaultValue = "0") int pageNumber, 
				@RequestParam(required = false, defaultValue = "id, asc") String sort[])
		{
		
			final int defaultPageSize = 10;
		    try {

		    	if(pageSize < 0 )
		    		pageSize = defaultPageSize;

		    	
		    	List<Order> orders = new ArrayList<Order>();

		    	if (sort.length > 2) {
			        
			          // sortOrder="field, direction"
		    		for(int i=0;i<sort.length;i+=2)
		    		{
		    			orders.add(new Order(getSortDirection(sort[i+1]), sort[i]));
		    		}
			          
		        } else {
		          // sort=[field, direction]
		          orders.add(new Order(getSortDirection(sort[1]), sort[0]));
		        }
	
		        
		        Pageable pagingSort = PageRequest.of(pageNumber, pageSize, Sort.by(orders));
		        Page<Users> list = UserRepo.findAll(pagingSort);
		        return ResponseEntity.of(Optional.of(list));
		    }
		    catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		    
		}
		
	private Direction getSortDirection(String string) {
		if(string.equals("asc"))
			return Sort.Direction.ASC;
		if(string.equals("desc"))
			return Sort.Direction.DESC;
		return null;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////



	
		// get by id
		
		@GetMapping("/users/{id}")
		public ResponseEntity<Users> getById(@PathVariable String id)
		{
			try {
				if(!UserRepo.existsById(id))
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
					
				return ResponseEntity.of(UserRepo.findById(id));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}		
			
		}
		
	///////////////////////////////////////////////////////////////////////////////////////////
		
		
		// create user
		@PostMapping("/users")
		public ResponseEntity<?> addUsers(@RequestBody Users user)
		{
			try {
				// validation of name, phone and website
				if(user.getName() == null)
					return ResponseEntity.ok("Name is required");
				if(user.getPhone() == null)
					user.setPhone("Default Phone" );
				if(user.getWebsite() == null)
					user.setUserName("Default Website" );
				
				// if the id already exists
				if(UserRepo.existsById(user.getId()))
					return ResponseEntity.status(HttpStatus.CONFLICT).build();
				
				UserRepo.save(user);
				return ResponseEntity.status(HttpStatus.CREATED).build();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
		}
		
///////////////////////////////////////////////////////////////////////////////////////////
		
		// delete user by id
		@DeleteMapping("/users/{id}")
		public ResponseEntity<?> deleteUser(@PathVariable String id)
		{
			
			try {
				if(!UserRepo.existsById(id))
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				
				UserRepo.deleteById(id);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			catch(Exception e){
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
			
		}
		
///////////////////////////////////////////////////////////////////////////////////////////
		
		// delete all user
		@DeleteMapping("/users")
		public ResponseEntity<Void> deleteAll()
		{		
			try {
				UserRepo.deleteAll();
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			catch(Exception e){
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
		}
		
///////////////////////////////////////////////////////////////////////////////////////////	
		//update user using Patch
		
		@PatchMapping("/users/{userId}")
		public ResponseEntity<?> updateUserPartial(@PathVariable String userId, @RequestBody Users updatedUser)
		{
			try {
			
				
				// if the id does not exists
				if(!UserRepo.existsById(userId))
					return ResponseEntity.ok("User does not exists...");
				
				Optional<Users> oldUser = UserRepo.findById(userId);
				
				Users user =oldUser.get();
				
				user.setName(updatedUser.getName());
				user.setEmail(updatedUser.getEmail());
				user.setPhone(updatedUser.getPhone());
				user.setWebsite(updatedUser.getWebsite());
				
				UserRepo.save(user);
				
				return ResponseEntity.ok("User updation complete" );
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
		}
		
///////////////////////////////////////////////////////////////////////////////////////////
		
		// search operation(name, email, phone)
		
		@GetMapping("/users/search")
		
		public ResponseEntity<List<Users>> search(@RequestParam(required = true) String[] query)
		{
			try {
				if(query.length!=2)
				{
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
				
				
				 if(query[0].equals("name"))
				{
					return ResponseEntity.ok(UserRepo.findByName(query[1]));
				}
				else if(query[0].equals("email"))
				{
					return ResponseEntity.ok(UserRepo.findByEmail(query[1]));
				}
				else if(query[0].equals("phone"))
				{
					return ResponseEntity.ok(UserRepo.findByPhone(query[1]));
				}
					
				 return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// update user using Put
//		
//		@PutMapping("/users/{userId}")
//		public ResponseEntity<Void> updateUser(@RequestBody Users user, @PathVariable String userId)
//		{
//			try {
//				UserRepo.save(user);
//				return ResponseEntity.status(HttpStatus.OK).build();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//			}
//		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		public ResponseEntity<Page<Users>> getAllUsers()
//		{
//			int pageNumber = 5;
//    	int pageSize = 3;
//    	Pageable page = PageRequest.of(0, 3);
//    	Page<Users> list = UserRepo.findAll(page);
//    	return  ResponseEntity.of(Optional.of(list));
//		}
		
		
//		@CrossOrigin
//		@GetMapping("/users/api")
//		public ResponseEntity<Page<Users>> getAllUsers(Pageable page)
//		{
//			// 1. Show all the data
//			
//		    try {
//		    	
//		    	Page<Users> list = UserRepo.findAll(page);
//		    	System.out.println(page.getPageNumber());
//		    	System.out.println(page.getPageSize());
//		    	
//				
//				if(list.isEmpty())
//					return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//				
//			    return ResponseEntity.of(Optional.of(list));
//		    }
//		    catch(Exception e)
//			{
//				e.printStackTrace();
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//			}
//		    
//		}
//		
		
		
}
