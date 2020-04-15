package com.example.demo.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {

	@Value("${password_check_regex}")
	String regex;
	
	private Pattern pattern;
	private Matcher matcher;
	
	
	private final UserRepository repository;
	public UserController(UserRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping("/user/{username}/{pwd}")
	public ResponseEntity<?> getUser(@PathVariable String username, @PathVariable String pwd) {
		if(repository.findByUserNameAndPwd(username, pwd) != null) {
			return new ResponseEntity<>(repository.findByUserNameAndPwd(username, pwd), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/user",method = RequestMethod.POST,consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> addUser(@RequestBody User user) {
		//System.out.println(user.getUserName());
		User user1 = new User();
		user1 = repository.findByUserNameAndPwd(user.getUserName(), user.getPwd());
		int flag = 0;
		pattern = Pattern.compile(regex);
		if(user1 == null) {
			if(pattern.matcher(user.getPwd()).matches()) {
				flag = 1;
			}
		}
		if(flag == 1) {
			repository.save(user);
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	
}
