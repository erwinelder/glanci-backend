package com.glanci.auth.mapper

import com.glanci.auth.domain.model.AppVersion
import com.glanci.auth.domain.model.User
import com.glanci.auth.shared.dto.CheckAppVersionRequestDto
import com.glanci.auth.shared.dto.UserDto


fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        role = role,
        name = name,
        langCode = language.langCode,
        subscription = subscription,
        timestamp = timestamp
    )
}


fun CheckAppVersionRequestDto.toDomainModel(): AppVersion {
    return AppVersion(
        primaryVersion = primaryVersion,
        secondaryVersion = secondaryVersion,
        tertiaryVersion = tertiaryVersion
    )
}