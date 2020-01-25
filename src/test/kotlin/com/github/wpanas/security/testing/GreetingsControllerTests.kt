package com.github.wpanas.security.testing

import org.hamcrest.CoreMatchers.hasItems
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
@WithMockUser
class GreetingsControllerTests {

	@Autowired
	lateinit var mockMvc: MockMvc

	@Autowired
	lateinit var greetingsRepository: GreetingsRepository

	@ParameterizedTest
	@ValueSource(strings = ["Mark", "Tom", "Billy"])
	fun `should save greeting by name`(name: String) {
		mockMvc.perform(post("/greetings/$name").with(csrf()))
				.andExpect(status().isCreated)
				.andExpect(jsonPath(".id").isNotEmpty)
				.andExpect(jsonPath("$.name").value(name))
	}

	@Test
	fun `should list saved greetings`() {
		greetingsRepository.save(Greeting.of("John"))
		greetingsRepository.save(Greeting.of("Mark"))
		mockMvc.perform(get("/greetings"))
				.andExpect(status().isOk)
				.andExpect(jsonPath("[*].name", hasItems("John", "Mark")))
	}

	@Test
	@WithAnonymousUser
	fun `should restrict access to greetings list`() {
		mockMvc.perform(get("/greetings"))
				.andExpect(status().isUnauthorized)
	}

	@Test
	@WithAnonymousUser
	fun `should restrict access to add greeting`() {
		mockMvc.perform(post("/greetings/Louis"))
				.andExpect(status().isForbidden)
	}

	@TestConfiguration
	class Configuration {
		@Bean
		fun greetingsRepository(): GreetingsRepository = InMemoryGreetingsRepository()
	}
}