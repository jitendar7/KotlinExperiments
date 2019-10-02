package com.experiment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

suspend fun work(i: Int) = withContext(Dispatchers.Default) {
    //note 'suspend' function, defined withContext
    Thread.sleep(1000)
    println("Work $i done")
}

fun main() {
    var time = measureTimeMillis {
        runBlocking {
            (1..2).forEach {
                launch {
                    //To define a Context for launch, we could have used "launch(Dispatchers.Default)"
                    work(it)
                }
            }
        }
    }

    println("Done in $time in ms")
}