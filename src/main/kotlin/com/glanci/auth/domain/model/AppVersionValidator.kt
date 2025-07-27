package com.glanci.auth.domain.model

import com.glanci.request.domain.error.AuthDataError
import com.glanci.request.domain.SimpleResult

object AppVersionValidator {

    private val requiredMinVersion = AppVersion(5, 0, 0, alphaVersion = 5)

    fun validateAppVersion(version: AppVersion): SimpleResult<AuthDataError> {
        if (version.primaryVersion < requiredMinVersion.primaryVersion ||
            version.secondaryVersion < requiredMinVersion.secondaryVersion ||
            version.tertiaryVersion < requiredMinVersion.tertiaryVersion
        ) {
            return SimpleResult.Error(AuthDataError.AppVersionIsBelowRequired)
        }
        return SimpleResult.Success()
    }

}