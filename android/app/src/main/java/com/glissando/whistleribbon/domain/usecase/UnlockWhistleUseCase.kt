package com.glissando.whistleribbon.domain.usecase

import com.glissando.whistleribbon.domain.model.WhistleTone

class UnlockWhistleUseCase {

    fun isUnlocked(tone: WhistleTone, goldenWhistles: Int): Boolean =
        goldenWhistles >= tone.requiredWhistles

    fun unlocked(goldenWhistles: Int): List<WhistleTone> =
        WhistleTone.entries.filter { isUnlocked(it, goldenWhistles) }
}
