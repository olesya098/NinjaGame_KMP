package org.example.ninjagame

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform