package com.experiment

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {    //this: CoroutineScope
    launch {            //launch a new Coroutine in the scope of runBlocking
        delay(9000L)
        println("Task from runBlocking ")
    }

    coroutineScope {    //Creates a coroutine scope, doesnot complete until all launched children complete
        launch{
            delay(5100L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutine scope")
    }

    println("Coroutine scope is over")
}