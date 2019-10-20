package com.experiment

import kotlinx.coroutines.*

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

    cooperative_cancellation1()
    cooperative_cancellation2()
    non_cancellable_block()
    timeout()
}

// cancellation is a cooperative process, all suspend functions are cancellable, they check for 'CancellationException'
// on cancellation and here 'isActive' is a cancellable operation
fun cooperative_cancellation1() = runBlocking {
    //sampleStart
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // cancellable computation loop
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
    //sampleEnd
}

//cancellation & closing the resources
fun cooperative_cancellation2() = runBlocking {
    val job = launch{
        try{
            repeat(1000) {i ->
                println("job: sleeping $i")
                delay(500L)
            }
        }finally {
            println("job: I'm running finally")
        }
    }

    delay(1300L)
    println("main: I'm tired of waiting")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}

//Run non-cancellable block
// Any attempt to use a suspending function in the 'finally' block, causes 'CancellationException',
// bcz coroutine running this code is cancelled.
//If we need a suspend function in a cancelled coroutine you can wrap the corresponding code in
// 'withContext(NonCancellable){ ..  }
fun non_cancellable_block() = runBlocking {
    val job = launch{
        try{
            repeat(1000){
                println("job: I'm sleeping $it")
                delay(500L)
            }
        }finally {
            withContext(NonCancellable){        // << using withContext, NonCancellable
                println("job: I'm running finally")
                delay(1000L)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }

    delay(1300L)
    println("I'm tired of waiting!")
    job.cancelAndJoin() //cancels the job and waits for its completion
    println("main: Now I can quit.")
}

//Timeout coroutine - using 'withTimeout'
// 'TimeoutCancellationException' that is thrown by 'withTimeout' is a subclass of 'CancellationException'
// Alternately 'withTimeoutOrNull' function, returns 'null' instead of throwing exception

fun timeout() = runBlocking {
    withTimeout(1300L){
        repeat(1000){
            it -> println("I'm sleeping $it ... ")
            delay(500L)
        }
    }
}