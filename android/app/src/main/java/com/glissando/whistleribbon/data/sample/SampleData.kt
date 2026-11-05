package com.glissando.whistleribbon.data.sample

import com.glissando.whistleribbon.domain.model.Song

object SampleData {

    private val titles = listOf(
        "Paper Lantern March",
        "Tin Roof Tumble",
        "Sugar Parade",
        "Marmalade Waltz",
        "Brass Button Strut",
        "Ribbon Runaway",
        "Cloudline Skip",
        "Peppermint Parade",
        "Lazy Kite",
        "Bottle Cap Rag",
        "Copper Whistle Reel",
        "Carousel Lullaby",
        "Confetti Gallop",
        "Lemon Drop Shuffle",
        "Streamer Serenade",
        "Tuba Tuesday",
        "Honeycomb Hop",
        "Pinwheel Polka",
        "Rooftop Ramble",
        "Velvet Balloon",
        "Clover Field Canter",
        "Grape Soda Swing",
        "Saltwater Sway",
        "Candle Lane Trot",
        "Harbour Bell Round",
        "Tinsel Tango",
        "Apricot Amble",
        "Meadow Fair March",
        "Gumdrop Gavotte",
        "Lantern Moth Lull",
        "Firecracker Fanfare",
        "Butterscotch Bop",
        "Window Box Waltz",
        "Puddle Jump Jig",
        "Paper Crane Glide",
        "Cinnamon Caper",
        "Teacup Carousel",
        "Thunder Drum Parade",
        "Lilac Lamplight",
        "Cobblestone Canter",
        "Starling Spiral",
        "Sherbet Sunrise",
        "Moonlit Megaphone",
        "Golden Ribbon Finale",
        "Encore Avenue"
    )

    val songs: List<Song> = titles.mapIndexed { index, title ->
        val id = index + 1
        val difficulty = (index / 9 + 1).coerceIn(1, 5)
        Song(
            id = id,
            name = title,
            bpm = 86 + (index % 7) * 9,
            seed = 1871 + id * 193,
            difficulty = difficulty,
            sparkleCount = 10 + difficulty * 3
        )
    }
}
