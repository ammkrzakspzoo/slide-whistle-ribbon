package com.glissando.whistleribbon.domain.model

enum class WhistleTone(
    val requiredWhistles: Int,
    val secondHarmonic: Float,
    val thirdHarmonic: Float,
    val breath: Float
) {
    SLIDE(0, 0.22f, 0.08f, 0.05f),
    FLUTE(5, 0.10f, 0.03f, 0.12f),
    BIRD(12, 0.34f, 0.18f, 0.08f),
    OCARINA(22, 0.05f, 0.14f, 0.03f),
    KAZOO(34, 0.46f, 0.30f, 0.10f)
}
