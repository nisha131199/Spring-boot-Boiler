package com.taskly.controller.user

import com.taskly.service.user.IUserService
import com.taskly.utils.autowired
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/")
@CrossOrigin("*")
@Tag(name = "Admin User Apis")
@SecurityRequirement(name = "bearerAuth")
class AdminUserController {
    val iUserService: IUserService by autowired()
}