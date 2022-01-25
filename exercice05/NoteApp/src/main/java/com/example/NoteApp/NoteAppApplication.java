package com.example.NoteApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NoteAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteAppApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(
			@RequestParam(
					value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	// GET: localhost:8080/hallo2/Hans?name2=Schurr
	@GetMapping("/hello2/{name}")
	public String hello2(
			@PathVariable(value = "name") String name,
			@RequestParam(value = "name2", defaultValue = "Schurr")
					String secondname) {
		return String.format("Hello %s %s!", name, secondname);
	}

	@GetMapping("/hello3")
	public Test hello3() {
		return new Test("hallo", "welt");
	}

}
