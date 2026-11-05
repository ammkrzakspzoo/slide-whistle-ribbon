package com.glissando.whistleribbon.domain.model

enum class Award {
    NONE,
    BRONZE,
    SILVER,
    GOLD;

    val isEarned: Boolean get() = this != NONE
}
