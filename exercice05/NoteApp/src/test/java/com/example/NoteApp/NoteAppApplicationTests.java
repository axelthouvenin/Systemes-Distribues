package com.example.NoteApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

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
		String initial_data = "[{\"id\":1,\"body\":\"Buy cake\",\"title\":\"TODO\",\"category\":{\"id\":\"60d06f89-8ab4-4f0a-bbad-df9c7f500bb6\",\"name\":\"Default\"}},{\"id\":2,\"body\":\"First test\",\"title\":\"TODO\",\"category\":{\"id\":\"41c40faf-3f54-4fd7-b13b-3acadd3c2765\",\"name\":\"Test\"}},{\"id\":3,\"body\":\"Buy concert tickets\",\"title\":\"concert\",\"category\":{\"id\":\"60d06f89-8ab4-4f0a-bbad-df9c7f500bb6\",\"name\":\"Default\"}},{\"id\":4,\"body\":\"Second test\",\"title\":\"TODO\",\"category\":{\"id\":\"41c40faf-3f54-4fd7-b13b-3acadd3c2765\",\"name\":\"Test\"}}]";
		this.mockMvc.perform(get("/notes"))
				.andDo(print())
				.andExpect(status().isOk());
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
		assert(response_category.getName().equals("Test"));
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
		assert(response_category.getName().equals("Test"));
		assert(response_category.getId().equals(id));
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
