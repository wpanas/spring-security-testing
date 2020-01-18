package com.github.wpanas.security.testing

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@RestController
class GreetingsController(private val greetingsRepository: GreetingsRepository) {

	@PostMapping("/greetings/{name}")
	@ResponseStatus(HttpStatus.CREATED)
	fun greet(@PathVariable("name") name: String): Greeting? {
		val greeting = Greeting.of(name)
		greetingsRepository.save(greeting)
		return greeting
	}

	@GetMapping("/greetings")
	fun greetings(): Collection<Greeting> = greetingsRepository.findAll()
}

data class Greeting(val id: UUID, val name: String) {
	companion object {
		fun of(name: String) = Greeting(UUID.randomUUID(), name)
	}
}

interface GreetingsRepository {
	fun save(greeting: Greeting)
	fun findAll(): Collection<Greeting>
}

@Repository
class InMemoryGreetingsRepository: GreetingsRepository {
	private val greetings = ConcurrentHashMap<UUID, Greeting>()

	override fun save(greeting: Greeting) {
		greetings.put(greeting.id, greeting)
	}

	override fun findAll() = greetings.values
}

