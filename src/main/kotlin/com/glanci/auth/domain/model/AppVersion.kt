package com.glanci.auth.domain.model

data class AppVersion(
    val primaryVersion: Int,
    val secondaryVersion: Int,
    val tertiaryVersion: Int,
    val alphaVersion: Int? = null,
    val betaVersion: Int? = null,
    val releaseCandidateVersion: Int? = null
)
