package com.glanci.request.domain.error

import kotlinx.serialization.Serializable

typealias DataError = Error

@Serializable
sealed interface Error
