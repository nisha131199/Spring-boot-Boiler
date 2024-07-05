package com.taskly

import com.taskly.respository.user.Role
import com.taskly.respository.user.UserRepository
import com.taskly.respository.user.Users
import com.taskly.utils.autowired
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.CrossOrigin
import java.time.LocalDateTime

@SpringBootApplication(scanBasePackages = ["com.admin"])
@EnableJpaRepositories(basePackages = ["com.admin"])
@EntityScan(basePackages  = ["com.admin"])
@CrossOrigin
@EnableAsync
@OpenAPIDefinition(
	info = Info(
		title = "API Admin",
		description = "API for admin",
		termsOfService = "http://swagger.io/terms/",
		license = License(
			name = "Apache 2.0",
			url = "http://springdoc.org"),
		version = "1.0"
	)
)
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer"
)

class MyApplication  {
	@EventListener(ApplicationReadyEvent::class)
	fun runAfterStartup() {
		val userRepository: UserRepository by autowired()
		val bCryptPasswordEncoder: BCryptPasswordEncoder by autowired()
		val usersData = userRepository.findByEmail("admin@yopmail.com")
		val usersDataWithPhone = userRepository.findByPhone("1111111111")
		if (!usersData.isPresent && usersData.isEmpty && !usersDataWithPhone.isPresent && usersDataWithPhone.isEmpty){
			userRepository.save(
				Users(
					firstname = "Admin Prod",
					lastname = "Root",
					email = "admin@yopmail.com",
					keyPassword = bCryptPasswordEncoder.encode("Admin@123"),
					role = Role.ADMIN,
					phone = "1111111111",
					createdAt = LocalDateTime.now(),
					updatedAt = LocalDateTime.now(),
				)
			)
		}
	}
}
fun main(args: Array<String>) {
	runApplication<MyApplication>(*args)
}