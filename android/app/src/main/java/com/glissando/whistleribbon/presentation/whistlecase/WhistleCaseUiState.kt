package com.glissando.whistleribbon.presentation.whistlecase

import com.glissando.whistleribbon.domain.model.WhistleTone

data class WhistleCardItem(
    val tone: WhistleTone,
    val unlocked: Boolean,
    val equipped: Boolean
)

data class WhistleCaseUiState(
    val goldenWhistles: Int = 0,
    val cards: List<WhistleCardItem> = emptyList()
)
