package org.example.ninjagame.di

import org.example.ninjagame.domain.audio.AudioPlayer
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioPlayer> { AudioPlayer() }
}