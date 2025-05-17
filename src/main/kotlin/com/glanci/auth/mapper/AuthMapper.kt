package com.glanci.auth.mapper

import com.glanci.auth.domain.dto.CheckAppVersionRequestDto
import com.glanci.auth.domain.dto.SignUpFormDto
import com.glanci.auth.domain.dto.UserDto
import com.glanci.auth.domain.model.AppVersion
import com.glanci.auth.domain.model.SignUpForm
import com.glanci.auth.domain.model.User
import com.glanci.core.domain.AppLanguage


fun SignUpFormDto.toDomainModel(): SignUpForm? {
    return SignUpForm(
        name = name,
        email = email,
        password = password,
        language = AppLanguage.fromLangCode(langCode = langCode) ?: return null
    )
}


fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        role = role,
        name = name,
        langCode = language.langCode,
        subscription = subscription
    )
}


fun CheckAppVersionRequestDto.toDomainModel(): AppVersion {
    return AppVersion(
        primaryVersion = primaryVersion,
        secondaryVersion = secondaryVersion,
        tertiaryVersion = tertiaryVersion
    )
}