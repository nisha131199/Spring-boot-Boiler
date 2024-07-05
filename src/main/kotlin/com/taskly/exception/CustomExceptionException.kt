package com.taskly.exception

class UserNotFoundException(override val message: String): RuntimeException()
class InvalidTokenException(override val message: String): Exception()
class UnExpectedException(override val message: String): RuntimeException()