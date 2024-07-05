package com.taskly.utils

import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

object MultipartExtension {
    fun MultipartFile.getExtension(): String? {
        return StringUtils.getFilenameExtension(this.originalFilename)
    }
}