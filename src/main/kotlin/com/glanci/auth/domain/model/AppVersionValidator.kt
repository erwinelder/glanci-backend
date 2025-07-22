package com.glanci.auth.domain.model

import com.glanci.auth.error.AuthError
import com.glanci.request.domain.SimpleResult

object AppVersionValidator {

    private val requiredMinVersion = AppVersion(5, 0, 0, alphaVersion = 5)

    fun validateAppVersion(version: AppVersion): SimpleResult<AuthError> {
        if (version.primaryVersion < requiredMinVersion.primaryVersion ||
            version.secondaryVersion < requiredMinVersion.secondaryVersion ||
            version.tertiaryVersion < requiredMinVersion.tertiaryVersion
        ) {
            return SimpleResult.Error(AuthError.AppVersionIsBelowRequired)
        }
        return SimpleResult.Success()
    }

}