package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckAppVersionRequestDto(
    val primaryVersion: Int,
    val secondaryVersion: Int,
    val tertiaryVersion: Int,
    val alphaVersion: Int?,
    val betaVersion: Int?,
    val releaseCandidateVersion: Int?
)