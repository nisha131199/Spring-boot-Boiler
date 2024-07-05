package com.taskly.model.auth

import jakarta.validation.constraints.*

data class AdminLoginRequest(
    @field:NotEmpty(message = "Email is mandatory")
    @field:NotBlank(message = "Email is mandatory")
    @field:NotNull(message = "Email is mandatory")
    @field:Email(message = "Email is not valid")
    val email: String? = null,

    @field:NotEmpty(message = "Password is mandatory")
    @field:NotBlank(message = "Password is mandatory")
    @field:NotNull(message = "Password is mandatory")
    val password:String?=null
)
