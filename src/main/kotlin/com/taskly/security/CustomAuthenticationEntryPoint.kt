package com.taskly.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskly.utils.Constants
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.IOException

class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(req: HttpServletRequest,
                          res: HttpServletResponse,
                          authException: AuthenticationException) {
        sendTokenInvalidResponse(res)
    }

    private fun sendTokenInvalidResponse(response: HttpServletResponse) {
        val mapper = ObjectMapper()
        val errorDetails: HashMap<String, Any> = HashMap()
        errorDetails["error"] = true
        errorDetails["statusCode"] = HttpStatus.FORBIDDEN.value()
        errorDetails["message"] = Constants.UNAUTHORIZED_ACCESS
        errorDetails["serverError"] = Constants.UNAUTHORIZED_ACCESS
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.writer, errorDetails)
    }
}