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

enum class GameLevel(val score: Int) {// границы левлов(пределы)
    One(score = 10),
    Two(score = 20),
    Three(score = 30),
    Four(score = 40),
    Five(score = 50)
}

val levelSettings = listOf(//усложнение скорости
    GameSettings(ninjaSpeed = 1f, weaponSpeed = 1f, targetSpeed = 5f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 1f, targetSpeed = 6f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 1f, targetSpeed = 7f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 0f, targetSpeed = 8f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 0f, targetSpeed = 9f),
)

val levels = listOf(//сопоставление левла с усложнением
    GameLevel.One to levelSettings[0],
    GameLevel.Two to levelSettings[1],
    GameLevel.Three to levelSettings[2],
    GameLevel.Four to levelSettings[3],
    GameLevel.Five to levelSettings[4],
)