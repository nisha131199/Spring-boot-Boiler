package com.taskly.security

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.taskly.respository.user.Permission
import com.taskly.respository.user.Role
import com.taskly.utils.autowired
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.io.IOException
import java.util.*


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableJpaRepositories
class SecurityConfiguration {

    val jwtAuthFilter: JwtAuthenticationFilter by autowired()

    val authenticationProvider: AuthenticationProvider by autowired()

    @Autowired
    lateinit var logoutHandler: LogoutHandler


    @Value("\${aws.accessKey}")
    private val accessKeyId: String? = null

    @Value("\${aws.secretKey}")
    private val secretKey: String? = null


    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .cors { it.configure(http) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/auth/**",
                    "/api/v1/auth/login",
                    "/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/docs.html",
                    "/webjars/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v2/api-docs",
                    "swagger-ui/swagger-ui/**",
                    "/swagger-ui/index.html",
                    "/api/v1/tasker/onboard-success.html",
                    "/api/v1/payment/create/customer",
                    "/api/v1/bank/account/webhook",
                    "/api/v1/payment/intents/webhook",
                ).permitAll()

                it.requestMatchers("/api/v1/admin/**").hasAnyRole(Role.ADMIN.name)
                    .requestMatchers(HttpMethod.GET, "/api/v1/**")
                    .hasAnyAuthority(Permission.ADMIN_READ.name, Permission.USER_READ.name)
                    .requestMatchers(HttpMethod.POST, "/api/v1/**")
                    .hasAnyAuthority(Permission.ADMIN_CREATE.name, Permission.USER_UPDATE.name)
                    .requestMatchers(HttpMethod.PUT, "/api/v1/**")
                    .hasAnyAuthority(Permission.ADMIN_UPDATE.name, Permission.USER_UPDATE.name)
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/**")
                    .hasAnyAuthority(Permission.ADMIN_DELETE.name, Permission.USER_DELETE.name)
                    .anyRequest().authenticated()
            }
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .logout {
                it.logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler { request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication? -> SecurityContextHolder.clearContext() }
            }
        http.exceptionHandling {
            it.authenticationEntryPoint(AuthenticationEntryPoint { request, response, authException ->
                response.let { httpResponse ->
                    httpResponse.contentType = "application/json"
                    httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
                    httpResponse.outputStream.println("{\"error\":true,\"statusCode\":401,\"message\":\"Access Denied\",\"serverError\":\"\"}")
                }
            })
        }
        return http.build()
    }


    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return CustomAuthenticationEntryPoint()
    }


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.applyPermitDefaultValues()
        configuration.addAllowedMethod("PUT")
        configuration.addAllowedMethod("GET")
        configuration.addAllowedMethod("POST")
        configuration.addAllowedMethod("DELETE")
        configuration.addAllowedMethod("OPTIONS")
        configuration.addAllowedMethod("PATCH")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun ignoringCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers("/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/docs.html", "/swagger-ui.html" )
        }
    }


    @Bean
    fun amazonS3(): AmazonS3 {
        val awsCredentials = BasicAWSCredentials(accessKeyId, secretKey)
        return AmazonS3Client.builder()
            .withRegion("ap-southeast-1")
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .build()
    }

    //TODO(Firebase json)
  /*  @Bean
    @Throws(IOException::class)
    fun firebaseMessaging(): FirebaseMessaging {
        val googleCredentials = GoogleCredentials
            .fromStream(ClassPathResource("firebase.json").inputStream)
        val firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build()
        val app = FirebaseApp.initializeApp(firebaseOptions, FirebaseApp.DEFAULT_APP_NAME)
        return FirebaseMessaging.getInstance(app)
    }*/
}
