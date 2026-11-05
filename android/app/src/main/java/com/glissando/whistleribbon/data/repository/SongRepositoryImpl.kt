package com.glissando.whistleribbon.data.repository

import com.glissando.whistleribbon.data.sample.SampleData
import com.glissando.whistleribbon.domain.model.Song
import com.glissando.whistleribbon.domain.repository.SongRepository

class SongRepositoryImpl : SongRepository {

    override fun songs(): List<Song> = SampleData.songs

    override fun song(id: Int): Song =
        SampleData.songs.firstOrNull { it.id == id } ?: SampleData.songs.first()
}
