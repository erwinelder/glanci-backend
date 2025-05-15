package com.glanci.auth.domain.model

object UserDataValidator {

    fun validateName(name: String): Boolean {
        return name.atLeastNumberOfChars(2) &&
                name.asMostNumberOfChars(30)
    }

    fun validateEmail(email: String): Boolean {
        return email.isValidEmail()
    }

    fun validatePassword(password: String): Boolean {
        return password.atLeastNumberOfChars(8) &&
                password.asMostNumberOfChars(30) &&
                password.atLeastOneUppercaseLetter() &&
                password.atLeastOneLowercaseLetter() &&
                password.atLeastOneDigit() &&
                password.atLeastOneSpecChar()
    }


    private fun String.isValidEmail(): Boolean {
        return length <= 50 &&
                substringAfterLast('@').isNotEmpty() &&
                substringBefore('@').isNotEmpty()
    }

    private fun String.atLeastNumberOfChars(number: Int): Boolean {
        return trim().length >= number
    }

    private fun String.asMostNumberOfChars(number: Int): Boolean {
        return trim().length <= number
    }

    private fun String.atLeastOneUppercaseLetter(): Boolean {
        return any { it.isUpperCase() }
    }

    private fun String.atLeastOneLowercaseLetter(): Boolean {
        return any { it.isLowerCase() }
    }

    private fun String.atLeastOneDigit(): Boolean {
        return any { it.isDigit() }
    }

    private fun String.atLeastOneSpecChar(): Boolean {
        return any { it in "@\$!%*?&#_+-" }
    }

}