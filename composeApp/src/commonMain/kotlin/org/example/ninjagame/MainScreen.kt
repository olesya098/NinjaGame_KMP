package org.example.ninjagame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteFlip
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ninjagame.composeapp.generated.resources.Res
import ninjagame.composeapp.generated.resources.background
import ninjagame.composeapp.generated.resources.kunai
import ninjagame.composeapp.generated.resources.run_sprite
import ninjagame.composeapp.generated.resources.standing_ninja
import org.example.ninjagame.domain.Game
import org.example.ninjagame.domain.GameStatus
import org.example.ninjagame.domain.MoveDirection
import org.example.ninjagame.util.detectMoveGesture
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

const val NINJA_FRAME_WIDTH = 253// ширина кадра спрайта
const val NINJA_FRAME_HEIGHT = 303
const val WEAPON_SPAWN_RATE = 150L
const val WEAPON_SIZE = 32f
const val TARGET_SPAWN_RATE = 1500L
const val TARGET_SIZE = 40f

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(Game()) }//игровой объект для передачи текущего состояния игры
    var moveDirection by remember { mutableStateOf(MoveDirection.None) }//свойство перемещения
    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }

    val runningSprite = rememberSpriteState(//используется для анимации бегущего нинзя
        totalFrames = 9,
        framesPerRow = 3
    )
    val standingSprite = rememberSpriteState(//не двигающийся нинзя
        totalFrames = 1,
        framesPerRow = 1
    )
    val currentRunningFrame by runningSprite.currentFrame.collectAsState()
    val currentStandingFrame by standingSprite.currentFrame.collectAsState()
    // наблюдение за первым работающим кадром(когда начало и конец)
    val isRunning by runningSprite.isRunning.collectAsState()
    val runningSpriteSpec = remember {// передача фактических листов анимации
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = NINJA_FRAME_WIDTH,
                frameHeight = NINJA_FRAME_HEIGHT,
                image = Res.drawable.run_sprite
            )
        )
    }
    val standingSpriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = NINJA_FRAME_WIDTH,
                frameHeight = NINJA_FRAME_HEIGHT,
                image = Res.drawable.standing_ninja
            )
        )
    }
    val runningImage = runningSpriteSpec.imageBitmap
    val standingImage = standingSpriteSpec.imageBitmap
    val kunaiImage = imageResource(Res.drawable.kunai)

    val ninjaOffsetX = remember(key1 = screenWidth) {
        Animatable(//переменная смещения по умолчанию на нижнем центре
            initialValue = ((screenWidth.toFloat()) / 2 - (NINJA_FRAME_WIDTH / 2))
        )
    }
    LaunchedEffect(Unit) {
        game = game.copy(status = GameStatus.Started)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenWidth = it.size.width
                screenHeight = it.size.height
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    detectMoveGesture(
                        gameStatus = game.status,
                        onLeft = {//анимируем таблицу спрайтов
                            moveDirection = MoveDirection.Left
                            runningSprite.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {//когда анимация остановлена(палец поднят) нинзя стоит
                                    ninjaOffsetX.animateTo(//когда нинзя бежит обновляем анимацию
                                        targetValue = if ((ninjaOffsetX.value - game.settings.ninjaSpeed) >= 0 - (NINJA_FRAME_WIDTH / 2))
                                            ninjaOffsetX.value - game.settings.ninjaSpeed else ninjaOffsetX.value,//минусы для того что б не убегал за край экрана
                                        animationSpec = tween(30)
                                    )
                                }
                            }
                        },
                        onRight = {
                            moveDirection = MoveDirection.Right
                            runningSprite.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {
                                    ninjaOffsetX.animateTo(
                                        targetValue = if ((ninjaOffsetX.value + game.settings.ninjaSpeed + NINJA_FRAME_WIDTH) <= screenWidth + (NINJA_FRAME_WIDTH / 2))
                                            ninjaOffsetX.value + game.settings.ninjaSpeed else ninjaOffsetX.value,
                                        animationSpec = tween(30)
                                    )
                                }
                            }
                        },
                        onFingerLifted = {
                            moveDirection = MoveDirection.None
                            runningSprite.stop()

                        }
                    )
                }
            }
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {//для рисовки нинзя
            drawSpriteView(
                spriteState = if (isRunning) runningSprite else standingSprite,
                spriteSpec = if (isRunning) runningSpriteSpec else standingSpriteSpec,
                currentFrame = if (isRunning) currentRunningFrame else currentStandingFrame,
                image = if (isRunning) runningImage else standingImage,
                spriteFlip = if (moveDirection == MoveDirection.Left) SpriteFlip.Horizontal else null ,//переворот и направление спрайта влево
                offset = IntOffset(//параметр смещения для правильного расчета движений
                    x = ninjaOffsetX.value.toInt(),
                    y = (screenHeight - NINJA_FRAME_HEIGHT - (NINJA_FRAME_HEIGHT / 2))//нинзя в нижней части экрана
                )
            )

        }
    }

}