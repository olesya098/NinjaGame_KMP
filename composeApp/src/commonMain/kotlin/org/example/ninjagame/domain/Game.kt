package org.example.ninjagame.domain

data class Game(//класс для отслеживания счета игры и настройки игры
    val status: GameStatus = GameStatus.Idle,
    val score: Int = 0,
    val settings: GameSettings = GameSettings()

)

data class GameSettings(
    val ninjaSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f,
)
