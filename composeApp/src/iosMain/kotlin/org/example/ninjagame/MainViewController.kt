package org.example.ninjagame

import androidx.compose.ui.window.ComposeUIViewController
import org.example.ninjagame.di.initializeKoin

fun MainViewController() = ComposeUIViewController (
    configure = { initializeKoin() }
){ App() }