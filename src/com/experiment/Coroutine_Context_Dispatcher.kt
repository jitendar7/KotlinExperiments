package com.experiment

import kotlinx.coroutines.*


//https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/coroutine-context-and-dispatchers.md

//Coroutine always executes in some 'context' represented by a value of 'CoroutineContext' type,
// The Coroutine context is a set of various elements,
// The main elements are - Job, Dispatcher

//Dispatcher , determines what thread or threads the corresponding co-routine uses for its execution.
// it can confine coroutine execution to a single thread, dispatch it to a thread pool, or let it run unconfined


fun main() = runBlocking<Unit> {
    //sampleStart
    launch {
        // context of the parent, main runBlocking coroutine
        println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Unconfined) {
        // not confined -- will work with main thread
        println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Default) {
        // will get dispatched to DefaultDispatcher ( same dispatcher as GlobalScope.launch{ } )
        println("Default               : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(newSingleThreadContext("MyOwnThread")) {
        // will get its own new thread
        println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
    }
    //sampleEnd

    jumpingThreads()
    childrenCoroutine()
}


/*

Unconfined            : I'm working in thread main
Default               : I'm working in thread DefaultDispatcher-worker-1
newSingleThreadContext: I'm working in thread MyOwnThread
main runBlocking      : I'm working in thread main

*/


//Unconfined coroutine dispatcher starts a coroutine in the caller thread, but only until the first suspension point
// After the suspension it resumes the coroutine in the thread that is fully determined by the suspending function that
// was invoked.


//Debugging coroutines & threads

//Run the code with "-Dkotlinx.coroutines.debug" JVM option
// ${Thread.currentThread().name} will give the thread with which coroutine

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

//Jumping between the threads
fun jumpingThreads() {
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use { ctx2 ->
            runBlocking(ctx1) {
                // 'runBlocking' with explicitly specified context
                log("Started in ctx1")
                withContext(ctx2) {
                    //  changing context of coroutine while still staying in the same coroutine
                    log("Working in ctx2")
                }
                log("Back to ctx1")
            }
        }
    }
}

// The above example uses 'use' function to release threads created with 'newSingleThreadContext'

/*
[Ctx1 @coroutine#1] Started in ctx1
[Ctx2 @coroutine#1] Working in ctx2
[Ctx1 @coroutine#1] Back to ctx1
*/


// 'Job' is part of the context, can be retrieved using " ${coroutineContext[Job]} "

//  "coroutine#1":BlockingCoroutine{Active}@6d311334

// 'isActive' in 'CoroutineScope' is just a convenient shortcut for coroutineContext[Job]?.isActive == true


//Children of 'Coroutine'
// When a Coroutine is launched in the 'CoroutineScope' of another coroutine, it inherits its context via
// CoroutineScope.coroutineContext & the Job of the new Coroutine becomes the child of the parent coroutines job.
// When the parent is cancelled, all its children are recursively cancelled, too.

// But when the GolbalScope is used to launch a coroutine, there is no parent for the job of the new coroutine.
// It operates independently

fun childrenCoroutine() = runBlocking<Unit> {
    // launch a coroutine to process some kind of incoming request
    val request = launch {
        // it spawns two other jobs, one with GlobalScope
        GlobalScope.launch {
            println("job1: I run in GlobalScope and execute independently!")
            delay(1000)
            println("job1: I am not affected by cancellation of the request")
        }
        // and the other inherits the parent context
        launch {
            delay(100)
            println("job2: I am a child of the request coroutine")
            delay(1000)
            println("job2: I will not execute this line if my parent request is cancelled")
        }
    }
    delay(500)
    request.cancel() // cancel processing of the request
    delay(1000) // delay a second to see what happens
    println("main: Who has survived request cancellation?")
}

/*
job1: I run in GlobalScope and execute independently!
job2: I am a child of the request coroutine
job1: I am not affected by cancellation of the request
main: Who has survived request cancellation?
*/

//Parent coroutine always waits for completion of all its children.

//Naming Coroutines for debugging


