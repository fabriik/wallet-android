package com.fabriik.buy.utils

fun formatCardNumber(input: String?): String {
    if (input.isNullOrEmpty()) {
        return ""
    }

    val delimiter = " "
    val cardNumberPlain = input.replace("\\s+".toRegex(), "")

    return cardNumberPlain.replace(
        ".{4}(?!$)".toRegex(), "$0$delimiter"
    )
}

fun formatDate(date: String?): String {
    if (date.isNullOrEmpty()) {
        return ""
    }

    val numbers = date.replace("[^0-9]".toRegex(), "")
    return if (numbers.length > 2) {
        val stringBuilder = StringBuilder(numbers)
        stringBuilder.insert(2, "/")
        stringBuilder.toString()
    } else numbers
}