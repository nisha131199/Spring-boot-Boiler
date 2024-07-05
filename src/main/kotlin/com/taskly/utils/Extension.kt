package com.taskly.utils

import com.google.firebase.auth.FirebaseAuth
import com.taskly.respository.user.Users
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


object Extension {

    fun Users.generatePseudoRandomNumber(): String {
        return "${this.email?.hashCode()}${System.currentTimeMillis()}"
    }

    fun generateOTP(): String {
        val randomPin = (Math.random() * 9000).toInt() + 1000
        return randomPin.toString()
    }


    fun String?.isFirebaseTokenValid(): String? {
        return try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(this)
            decodedToken.uid
        }catch (e:Exception){
            null
        }
    }

    fun generateOtp():Int{
        val code = (1000..9999).random() // Generates a random number between 1000 and 9999 (inclusive)
        return code
    }
    fun generate6DigitOtp():Int{
        val code = (100000..999999).random() // Generates a random number between 1000 and 9999 (inclusive)
        return code
    }

    fun ZonedDateTime.isExpired(expirationMinutes: Long): Boolean {
        val currentTime = ZonedDateTime.now()
        val difference = ChronoUnit.MINUTES.between(this, currentTime)
        return difference >= expirationMinutes
    }
    fun isPasswordMatch(old:String?,new:String?):Boolean {
        return BCryptPasswordEncoder().matches(old,new)
    }
}