package com.experiment


import kotlinx.coroutines.*

//'suspend' function
// how to call 'suspend' function, using GlobalScope.launch / coroutine builder functions

suspend fun test() {
    delay(1000L)
    println("32")
}


fun main() {
    GlobalScope.launch{
        test()
    }
    println("Hello")
    runBlocking {
        delay(1500L)
    }
}