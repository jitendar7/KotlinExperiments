package com.experiment

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

//https://medium.com/@elizarov/the-reason-to-avoid-globalscope-835337445abc

suspend fun work(i: Int) = withContext(Dispatchers.Default) {   //concurrency by using 'Dispatchers.Default'
    //note 'suspend' function, defined withContext
    Thread.sleep(1000)
    delay(1)
    println("Work $i done")
}

//GlobalScope.launch creates global coroutines, its developers responsibility to keep track
// of their lifetime

//launch(Dispatchers.Default) creates 'children' coroutines in 'runBlocking' scope,
//so 'runBlocking' waits for their completion automatically

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