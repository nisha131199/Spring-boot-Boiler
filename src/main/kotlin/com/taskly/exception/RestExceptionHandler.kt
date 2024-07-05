package com.taskly.exception

import com.taskly.model.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.hibernate.exception.SQLGrammarException
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.dao.InvalidDataAccessResourceUsageException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.ErrorResponseException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.FileNotFoundException
import java.time.format.DateTimeParseException
import java.util.stream.Collectors

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status).body(
            ApiResponse(
                error = true,
                message = "Not found",
                statusCode = status.value(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val fieldErrors = ex.bindingResult
            .fieldErrors
            .stream()
            .map { field: FieldError -> "" + field.defaultMessage }
            .collect(Collectors.toList())
        val globalErrors = ex.bindingResult
            .globalErrors
            .stream()
            .map { field: ObjectError -> "" + field.defaultMessage }
            .collect(Collectors.toList())
        val errors: MutableList<String> = ArrayList()
        errors.addAll(globalErrors)
        errors.addAll(fieldErrors)
        val message = if (fieldErrors.size > 0) {
            fieldErrors[0]
        }
        else {
            "Some field are missing or invalid"
        }

        val err = ApiResponse(
            error = true,
            message = message,
            statusCode = status.value(),
            serverError = ex.localizedMessage,
            data = null
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err)    }



    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest?
    ): ResponseEntity<Any?>? {
        val details: MutableList<String?> = ArrayList()
        details.add(ex.message)
        val err = ApiResponse(
            error = true,
            message = messageConvert(ex),
            statusCode = HttpStatus.BAD_REQUEST.value(),
            serverError = ex.localizedMessage,
            data = null
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err)
    }


    @ExceptionHandler(UserNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: UserNotFoundException
    ): ResponseEntity<Any> {
        val err = ApiResponse(
            error = true,
            message = ex.message,
            statusCode = HttpStatus.NOT_FOUND.value(),
            serverError = ex.localizedMessage,
            data = null
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err)
    }


    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(
        ex: InvalidTokenException
    ): ResponseEntity<Any> {
        val err = ApiResponse(
            error = true,
            message = ex.message,
            serverError = ex.localizedMessage,
            statusCode = HttpStatus.UNAUTHORIZED.value(),
            data = null
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err)
    }


    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val err = ApiResponse(
            error = true,
            message = ex.message.toString(),
            statusCode = status.value(),
            serverError = ex.localizedMessage,
            data = null
        )
        return ResponseEntity.status(status).body(err)
    }

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {

        return when (statusCode.value()) {
            HttpStatus.NOT_FOUND.value() -> {
                ResponseEntity.status(statusCode).body(
                    ApiResponse(
                        error = true,
                        message = "Not found",
                        statusCode = statusCode.value(),
                        serverError = ex.localizedMessage,
                        data = null
                    )
                )
            }
            HttpStatus.FORBIDDEN.value() -> {
                ResponseEntity.status(statusCode).body(
                    ApiResponse(
                        error = true,
                        message = "Requested resource is forbidden",
                        statusCode = statusCode.value(),
                        serverError = ex.localizedMessage,
                        data = null
                    )
                )
            }
            HttpStatus.INTERNAL_SERVER_ERROR.value() -> {
                ResponseEntity.status(statusCode).body(
                    ApiResponse(
                        error = true,
                        message = "Internal server error",
                        statusCode = statusCode.value(),
                        serverError = ex.localizedMessage,
                        data = null
                    )
                )
            }
            HttpStatus.BAD_REQUEST.value() -> {
                ResponseEntity.status(statusCode).body(
                    ApiResponse(
                        error = true,
                        message = "Bad request",
                        statusCode = statusCode.value(),
                        serverError = ex.localizedMessage,
                        data = null
                    )
                )
            }
            else -> ResponseEntity.status(statusCode).body(
                ApiResponse(
                    error = true,
                    message = ex.message.toString(),
                    serverError = ex.localizedMessage,
                    statusCode = statusCode.value(),
                    data = null
                )
            )
        }
    }

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status).body(
            ApiResponse(
                error = true,
                message = "Request method not allowed",
                serverError = ex.localizedMessage,
                statusCode = status.value(),
                data = null
            )
        )
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status).body(
            ApiResponse(
                error = true,
                message = "Request message not readable",
                statusCode = status.value(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun handleHttpMediaTypeNotAcceptable(
        ex: HttpMediaTypeNotAcceptableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status).body(
            ApiResponse(
                error = true,
                message = "Media type not acceptable",
                serverError = ex.localizedMessage,
                statusCode = status.value(),
                data = null
            )
        )
    }
    @ExceptionHandler(Exception::class)
    fun handle(
        ex: Exception?,
        request: HttpServletRequest?, response: HttpServletResponse?
    ): ResponseEntity<Any> {
        val statusCode: HttpStatus
        val message = when (ex) {
            is IllegalAccessException -> {
                statusCode = HttpStatus.UNAUTHORIZED
                "Access denied"
            }
            is NullPointerException -> {
                statusCode = HttpStatus.BAD_REQUEST
                "Fields are missing"
            }
            is IllegalArgumentException -> {
                statusCode = HttpStatus.BAD_REQUEST
                "Bad request"
            }
            is UnExpectedException -> {
                statusCode = HttpStatus.EXPECTATION_FAILED
                ex.message
            }
            is DateTimeParseException -> {
                statusCode = HttpStatus.BAD_REQUEST
                "Date not valid"
            }
            is NoHandlerFoundException -> {
                statusCode = HttpStatus.BAD_GATEWAY
                "No found"
            }
            is AccessDeniedException -> {
                statusCode = HttpStatus.UNAUTHORIZED
                "Requested resource is forbidden"
            }
            is EmptyResultDataAccessException -> {
                statusCode = HttpStatus.NOT_FOUND
                "Data not available"
            }
            is NoSuchElementException -> {
                statusCode = HttpStatus.NOT_FOUND
                "Data not present"
            }
            is SQLGrammarException -> {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                "Internal server error"
            }
            is InvalidDataAccessApiUsageException -> {
                statusCode = HttpStatus.BAD_REQUEST
                "Data type is incorrect"
            }
            is org.springframework.security.access.AccessDeniedException -> {
                statusCode = HttpStatus.UNAUTHORIZED
                "Requested resource is forbidden"
            }
            is InvalidDataAccessResourceUsageException -> {
                statusCode = HttpStatus.UNAUTHORIZED
                "Data not available"
            }
            is InvalidTokenException -> {
                statusCode = HttpStatus.UNAUTHORIZED
                ex.message
            }
            is FileNotFoundException -> {
                statusCode = HttpStatus.NOT_FOUND
                ex.message
            }
            is ExpiredJwtException -> {
                statusCode = HttpStatus.UNAUTHORIZED
                "Token expired"
            }
            is ConstraintViolationException -> {
                statusCode = HttpStatus.BAD_REQUEST
                messageConvert(ex)
            }
            else -> {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                "Something went wrong try again later"
            }
        }
        return ResponseEntity.status(statusCode).body(
            ApiResponse(
                error = true,
                message = message.toString(),
                statusCode = statusCode.value(),
                serverError = ex?.message.toString(),
                data = null
            )
        )
    }

    private fun messageConvert(ex: ConstraintViolationException) : String{
        return try {
            if (ex.message?.contains(":") == true){
                val array =  ex.message?.split(":")
                array?.get(1)!!
            }else {
                "Field is missing"
            }
        }catch (e:Exception){
            "Field is missing"
        }
    }




    override fun handleErrorResponseException(
        ex: ErrorResponseException,
        headers: HttpHeaders, status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message,
                serverError = ex.detailMessageCode,
                data = null
            )
        )
    }

    override fun handleConversionNotSupported(
        ex: ConversionNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message.toString(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }


    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message,
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message,
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message.toString(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun handleServletRequestBindingException(
        ex: ServletRequestBindingException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message.toString(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }


    override fun handleAsyncRequestTimeoutException(
        ex: AsyncRequestTimeoutException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message.toString(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message.toString(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }


    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(status.value()).body(
            ApiResponse(
                error = true,
                statusCode = status.value(),
                message = ex.message.toString(),
                serverError = ex.localizedMessage,
                data = null
            )
        )
    }

    override fun createResponseEntity(
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(statusCode.value()).body(
            ApiResponse(
                error = true,
                statusCode = statusCode.value(),
                message = "Error",
                serverError = "error",
                data = null
            )
        )
    }

}
