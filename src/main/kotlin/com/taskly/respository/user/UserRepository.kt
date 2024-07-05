package com.taskly.respository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<Users, Long> {
    fun findByEmail(email: String): Optional<Users>
    fun findByPhone(phone: String): Optional<Users>
}
