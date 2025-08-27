package org.example.ninjagame.util

import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import org.example.ninjagame.domain.GameStatus


suspend fun AwaitPointerEventScope.detectMoveGesture(//для анимирования персонажа
    gameStatus: GameStatus,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onFingerLifted: () -> Unit,
) {
    while (gameStatus == GameStatus.Started) {//блок While нужен только что б убедиться что
        // жесты применяются к игре только в запущенном состоянии
        val downEvent = awaitPointerEvent()//событие ожидания(ожидает след. действие к примеру касание)
        val initialDown = downEvent.changes.firstOrNull { it.pressed }//ищет первое событие касания когда пользователь нажал
        if(initialDown == null) continue//если такое событие не найдено

        val primaryPointerId = initialDown.id//отслеживает конкретный палец или указатель на экране
        //если касаются несколько пальцев каждый палец получит свой номер
        var previousPosition = initialDown.position//предыдущая позиция (первое касание)
        //используется для определение переместился ли палец
        while(true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull {
                it.id == primaryPointerId
            }

            if(change == null || !change.pressed) {//если пользователь убирает палец от экрана
                onFingerLifted()//лямбда о том что палец был поднят
                break//жест завершён
            }

            val currentPosition = change.position//переменная текущего положения пальца
            val deltaX = currentPosition.x - previousPosition.x//насколько палец переместился по диагонали

            if (deltaX < 0) {
                onLeft()
            } else if(deltaX > 0) {
                onRight()
            }

            previousPosition = currentPosition//обновление предыдущей позиции до текущей
            change.consume()//применение жеста (гарантирует не распространение дальше)
        }
    }
}