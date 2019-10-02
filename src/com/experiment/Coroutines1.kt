package com.experiment


import kotlinx.coroutines.*

//Ref: https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/basics.md#structured-concurrency

//'suspend' function
// how to call 'suspend' function, using GlobalScope.launch / coroutine builder functions
// Coroutine scope -> GlobalScope, lifetime of the new coroutine is limited only by the lifetime of the whole application


suspend fun test() {
    delay(1000L)
    println("World!")
}


fun main() {
    GlobalScope.launch{
        test()
    }

    println("Hello")
    runBlocking{   // blocks the main thread
        delay(50L)     //delay( -> non-blocking
    }

    Thread.sleep(10L)     //Thread.sleep( -> blocking

}