package com.fabriik.common.utils.validators

import java.util.regex.Pattern

object ConfirmationCodeValidator : Validator {

    private val pattern = Pattern.compile("^[0-9]{6}$")

    override fun invoke(input: String) = pattern.matcher(input).matches()
}