package com.dailycodework.quiz_online.controller;


import com.dailycodework.quiz_online.model.Question;
import com.dailycodework.quiz_online.service.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import com.dailycodework.quiz_online.dto.LoginDto;
import com.dailycodework.quiz_online.model.User;
import com.dailycodework.quiz_online.response.LoginResponse;
import com.dailycodework.quiz_online.service.JWTService;
import com.dailycodework.quiz_online.service.UserService;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;


@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JWTService jwtService;

	private final IQuestionService questionService;

	@GetMapping("/auth/all-questions")
	public ResponseEntity<List<Question>> getAllQuestions(){
		List<Question> questions= questionService.getAllQuestions();
		return ResponseEntity.ok(questions);
	}

	@PostMapping("/auth/create-new-question")
	public ResponseEntity<Question> createQuestion(@Valid @RequestBody Question question){
		Question createdQuestion = questionService.createQuestion(question);
		return ResponseEntity.status(CREATED).body(createdQuestion);
	}


	@GetMapping("/auth/question/{id}")
	public ResponseEntity<Question> getQuestionById(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
		Optional<Question> theQuestion = questionService.getQuestionById(id);
		if (theQuestion.isPresent()){
			return ResponseEntity.ok(theQuestion.get());
		}else {
			throw new ChangeSetPersister.NotFoundException();
		}
	}

	@PutMapping("/auth/question/{id}/update")
	public ResponseEntity<Question> updateQuestion(
			@PathVariable Long id, @RequestBody Question question) throws ChangeSetPersister.NotFoundException {
		Question updatedQuestion = questionService.updateQuestion(id, question);
		return ResponseEntity.ok(updatedQuestion);
	}

	@DeleteMapping("/auth/question/{id}/delete")
	public ResponseEntity<Void> deleteQuestion(@PathVariable Long id){
		questionService.deleteQuestion(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/auth/subjects")
	public ResponseEntity<List<String>> getAllSubjects(){
		List<String> subjects = questionService.getAllSubjects();
		return ResponseEntity.ok(subjects);
	}

	@CrossOrigin("http://localhost:5173")
	@GetMapping("/auth/quiz/fetch-questions-for-user")
	public ResponseEntity<List<Question>> getQuestionsForUser(
			@RequestParam Integer numOfQuestions, @RequestParam String subject){
		List<Question> allQuestions = questionService.getQuestionsForUser(numOfQuestions, subject);

		List<Question> mutableQuestions = new ArrayList<>(allQuestions);
		Collections.shuffle(mutableQuestions);

		int availableQuestions = Math.min(numOfQuestions, mutableQuestions.size());
		List<Question> randomQuestions = mutableQuestions.subList(0, availableQuestions);






		return ResponseEntity.ok(randomQuestions);
	}






	@PostMapping("/auth/signup")
	public ResponseEntity<User> postMethodName(@RequestBody User user) {
		
		User user2 = userService.signup(user);
		
		return ResponseEntity.ok(user2);
		
	}
	

	@PostMapping("/auth/login")
	public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDto loginDto) {
		
		User user = userService.loginUser(loginDto);
		
		String jwtToken = jwtService.generateToken(new HashMap<>(), user);
		
		LoginResponse loginResponse = new LoginResponse();
		
		loginResponse.setToken(jwtToken);
		loginResponse.setTokenExpireTime(jwtService.getExpirationTime());
		
		return ResponseEntity.ok(loginResponse);
		
	}
	

	@GetMapping("/auth/getUsers")
	public ResponseEntity<List<User>> getAllUsers() {
		
		List<User> users = userService.getAllUsers();

		return ResponseEntity.ok(users);
	}
}
