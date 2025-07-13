package com.glanci.auth.domain.model

import com.glanci.auth.error.AuthError

object AppVersionValidator {

    private val requiredMinVersion = AppVersion(5, 0, 0, alphaVersion = 4)

    fun validateAppVersion(version: AppVersion) {
        if (version.primaryVersion < requiredMinVersion.primaryVersion ||
            version.secondaryVersion < requiredMinVersion.secondaryVersion ||
            version.tertiaryVersion < requiredMinVersion.tertiaryVersion
        ) {
            throw AuthError.AppVersionIsBelowRequired()
        }
    }

}