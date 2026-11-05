package com.glissando.whistleribbon.domain.repository

import com.glissando.whistleribbon.domain.model.Song

interface SongRepository {
    fun songs(): List<Song>
    fun song(id: Int): Song
}
