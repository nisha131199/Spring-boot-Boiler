package com.taskly.respository.user

enum class Permission {
    ADMIN_READ,
    ADMIN_UPDATE,
    ADMIN_CREATE,
    ADMIN_DELETE,

    USER_READ,
    USER_UPDATE,
    USER_CREATE,
    USER_DELETE;

    private val permission: String? = null
}
