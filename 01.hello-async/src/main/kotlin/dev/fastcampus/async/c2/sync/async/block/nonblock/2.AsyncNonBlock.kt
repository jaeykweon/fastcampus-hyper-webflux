import mu.KotlinLogging
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

fun main() {
    thread { subA() }
    thread { subB() }
    thread { subC() }
}


private fun subA() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun subB() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun subC() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun workHard() {
    Thread.sleep(1000)
}