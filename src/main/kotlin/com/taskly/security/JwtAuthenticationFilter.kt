package com.taskly.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskly.utils.autowired
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {

    val jwtService: JwtService by autowired()
    val userDetailsService: UserDetailsService by autowired()
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me")
        response.setHeader("Access-Control-Expose-Headers", "Content-Length, Authorization")
        if (request.servletPath.contains("/api/v1/auth") ||
            request.servletPath.contains("/api/v1/admin/auth") ||
            request.servletPath.contains("/swagger-ui/swagger-ui/index.html")
            || request.servletPath.contains("swagger-ui/swagger-ui/**")
            || request.servletPath.contains("/swagger-ui/**")
            || request.servletPath.contains("swagger")
            || request.servletPath.contains("/v3/api-docs")
            || request.servletPath.contains("/v2/api-docs")
            || request.servletPath.contains("/api/v1/tasker/onboard-success.html")
            || request.servletPath.contains("/api/v1/payment/create/customer")
            || (request.servletPath.contains("/api/v1/web/task/"))
            || (request.servletPath.contains("/api/v1/bank/account/webhook"))
            || (request.servletPath.contains("/api/v1/payment/intents/webhook"))
                ) {
            filterChain.doFilter(request, response)
            return
        }
        if (request.getHeader("Authorization").isNullOrEmpty()){
            sendTokenInvalidResponse(response)
            return
        }
        val authHeader: String = request.getHeader("Authorization")
        val userEmail: String?
        if (!authHeader.startsWith("Bearer ")) {
            sendTokenInvalidResponse(response)
        }
        val jwt: String = authHeader.substring(7)
        try {
            userEmail = jwtService.extractUsername(jwt)
            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(userEmail)
                //TODO(find token in db first)
//                val isTokenValid: Boolean? = tokenRepository.findByToken(jwt)
//                    .map { t -> !t.expired && !t.revoked }
//                    .orElse(false)
                if (jwtService.isTokenValid(jwt, userDetails)/* && isTokenValid == true*/) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                    filterChain.doFilter(request, response)
                }else{
                    sendTokenInvalidResponse(response)
                }
            }else{
                sendTokenInvalidResponse(response)
            }
        }catch (e:Exception){
            sendTokenInvalidResponse(response)
        }
    }


    private fun sendTokenInvalidResponse(response: HttpServletResponse) {
        val mapper = ObjectMapper()
        val errorDetails: HashMap<String, Any> = HashMap()
        errorDetails["error"] = true
        errorDetails["statusCode"] = HttpStatus.FORBIDDEN.value()
        errorDetails["message"] = "Invalid Token"
        errorDetails["serverError"] = "Invalid token"
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.writer, errorDetails)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return ("/api-docs" == path ||
        "/v3/api-docs/**" == path ||
        "/swagger-ui/**" == path ||
        "/v2/api-docs" == path ||
        "swagger-ui/swagger-ui/**" == path ||
        "/webjars/swagger-ui/**" == path ||
        "/docs.html" == path ||
        "/swagger-ui/index.html" == path ||
        "/swagger-ui.html" == path
                )
    }
}