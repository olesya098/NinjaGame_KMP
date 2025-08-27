package org.example.ninjagame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import ninjagame.composeapp.generated.resources.Res
import ninjagame.composeapp.generated.resources.background
import org.example.ninjagame.domain.Game
import org.example.ninjagame.util.detectMoveGesture
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainScreen() {
    val game = remember { Game() }//игровой объект для передачи текущего состояния игры
    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit){//элемент события
                awaitPointerEventScope {
                    detectMoveGesture(
                        gameStatus = game.status,
                        onLeft = {},
                        onRight = {},
                        onFingerLifted = {}
                    )
                }
            }
    ){
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

}