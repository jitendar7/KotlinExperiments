package com.experiment

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {    //this is like an adapter, used to start the main coroutine
                                    // this is also a way to write unit testcases for suspend functions
    val job = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    job.join()      // wait until the background job that we launched is complete

}