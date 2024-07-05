package com.taskly.respository.user


import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.validator.constraints.UniqueElements
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import java.time.LocalDateTime

@Entity(name = "users")
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("email", "phone"))]
)
data class Users(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    var firstname: String? = null,

    var lastname: String? = null,

    var profileImage: String = "",

    @UniqueElements
    var email: String? = null,

    @UniqueElements
    var phone: String? = null,

    @JsonIgnore
    var keyPassword: String? = null,

    @Enumerated(EnumType.STRING)
    val role: Role? = null,

    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,

    var isNew: Boolean = true,

    @JsonIgnore
    var accountNonExpired: Boolean = true,

    @JsonIgnore
    var accountNonLocked: Boolean = true,

    @JsonIgnore
    var credentialsNonExpired: Boolean = true,

    var enabled: Boolean = true,

    var isDeleted: Boolean = false,

    ) : Serializable, UserDetails {

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return role?.authorities!!
    }

    @JsonIgnore
    override fun getPassword(): String {
        return keyPassword ?: ""
    }

    @JsonIgnore
    override fun getUsername(): String {
        return email!!
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }
}
