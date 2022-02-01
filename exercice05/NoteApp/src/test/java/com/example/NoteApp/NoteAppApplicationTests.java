package com.example.NoteApp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static
		org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static
		org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static
		org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static
		org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
class NoteAppApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	public void getNotesShouldReturnEmptyArray()
			throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String response = this.mockMvc.perform(get("/notes"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Category> response_category =
				objectMapper.readValue(response, new TypeReference<List<Category>>(){});
		assertThat(response_category).isq;

	}

	@Test
	public void postCategoryShouldWork() throws Exception {
		Category category = new Category("Test");
		ObjectMapper objectMapper = new ObjectMapper();
		String category_json = objectMapper.writeValueAsString(category);
		String response = this.mockMvc.perform(post("/categories")
						.content(category_json)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		Category response_category =
				objectMapper.readValue(response, Category.class);
		assertThat(response_category.getName()).isEqualTo("Test");
	}

	@Test
	public void getCategoryShouldWork() throws Exception {
		Category category = new Category("Test");
		ObjectMapper objectMapper = new ObjectMapper();
		String category_json = objectMapper.writeValueAsString(category);
		String response = this.mockMvc.perform(post("/categories")
						.content(category_json)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		Category response_category =
				objectMapper.readValue(response, Category.class);
		UUID id = response_category.getId();
		response = this.mockMvc.perform(get(String.format("/categories/%s", id)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		response_category = objectMapper.readValue(response, Category.class);
		assertThat(response_category.getName()).isEqualTo("Test");
		assertThat(response_category.getId()).isEqualTo(id);
	}

	@Test
	public void deleteCategoryShouldWork() throws Exception {
		Category category = new Category("Test");
		ObjectMapper objectMapper = new ObjectMapper();
		String category_json = objectMapper.writeValueAsString(category);
		String response = this.mockMvc.perform(post("/categories")
						.content(category_json)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		Category response_category =
				objectMapper.readValue(response, Category.class);
		UUID id = response_category.getId();

		this.mockMvc.perform(delete(String.format("/categories/%s", id)))
				.andExpect(status().isOk());
		this.mockMvc.perform(get(String.format("/categories/%s", id)))
				.andExpect(status().isNotFound());
	}

	/*
	@Test
	public void PostNoteShouldReturnOk()
			throws Exception {
		Note note = new Note(1, "hallo", "text");
		ObjectMapper objectMapper = new ObjectMapper();
		String note_json =
				objectMapper.writeValueAsString(note);
		this.mockMvc.perform(post("/notes")
						.content(note_json)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
				//.andExpect(content().json(note_json));
	}

	 */
}
