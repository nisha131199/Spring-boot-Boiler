package com.taskly.model.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class LoginRequest(
    @field:NotEmpty(message = "Email is mandatory")
    @field:NotBlank(message = "Email is mandatory")
    @field:NotNull(message = "Email is mandatory")
    @field:Email(message = "Email is not valid")
    val email: String? = null,

    @field:NotEmpty(message = "Password is mandatory")
    @field:NotBlank(message = "Password is mandatory")
    @field:NotNull(message = "Password is mandatory")
    val password: String? = null,
)
