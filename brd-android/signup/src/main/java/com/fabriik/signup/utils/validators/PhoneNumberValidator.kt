package com.fabriik.signup.utils.validators

import android.util.Patterns
import com.fabriik.common.utils.validators.Validator

object PhoneNumberValidator : Validator {

    private val pattern = Patterns.PHONE

    override fun invoke(input: String) = pattern.matcher(input).matches()
}