package com.fabriik.buy.data.enums

import androidx.annotation.DrawableRes
import com.fabriik.buy.R

enum class CardType(@DrawableRes val icon: Int) {
    VISA(R.drawable.ic_visa),
    MASTERCARD(R.drawable.ic_mastercard),
    UNKNOWN(R.drawable.ic_credit_card)
}