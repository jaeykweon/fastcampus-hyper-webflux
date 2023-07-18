package dev.fastcampus.coroutine.c6.async.nonblock

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun main() {
    logger.debug { "start" }
    val dispatcher = newSingleThreadContext("single")
    coroutineScope {
        launch(dispatcher) {
            subA()
        }
        launch(dispatcher) {
            subA()
        }
    }
    logger.debug { "end" }
}

private suspend fun subA() {
    logger.debug { "start" }
    subB()
    logger.debug { "end" }
}

private suspend fun subB() {
    logger.debug { "start" }
    delay(5000)
    logger.debug { "end" }
}