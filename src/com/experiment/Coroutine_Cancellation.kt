package com.experiment

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val job = launch {
        repeat(1000){
            println("job:sleeping $it")
            delay(500L)
        }
    }

    delay(1300L)
    println("main: I'm tired of waiting")
    job.cancelAndJoin()
    println("main: Now I can quit.")

}