package com.glanci.request.domain.error

import kotlinx.serialization.Serializable

typealias RootError = Error

@Serializable
sealed interface Error
