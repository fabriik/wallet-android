package com.fabriik.common.utils.validators

import java.util.regex.Pattern

object PasswordValidator : Validator {

    private val pattern = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,32}$"
    )

    override fun invoke(input: String) = pattern.matcher(input).matches()
}