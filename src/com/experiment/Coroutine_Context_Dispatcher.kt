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
    coroutineNameSample()
    explicitContextElement()

    val activity = Activity()
    activity.doSomething()

    contextSwitching()
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
// 'CoroutineName' serves the same purpose as thread name

fun coroutineNameSample() = runBlocking(CoroutineName("main")) {
    //sampleStart
    log("Started main coroutine")
    // run two background value computations
    val v1 = async(CoroutineName("v1coroutine")) {
        delay(500)
        log("Computing v1")
        252
    }
    val v2 = async(CoroutineName("v2coroutine")) {
        delay(1000)
        log("Computing v2")
        6
    }
    log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
//sampleEnd
}

/*
[main @main#1] Started main coroutine
[main @v1coroutine#2] Computing v1
[main @v2coroutine#3] Computing v2
[main @main#1] The answer for v1 / v2 = 42
*/


//Combining context elements
fun explicitContextElement() = runBlocking<Unit> {
    //sampleStart
    launch(Dispatchers.Default + CoroutineName("test")) {
        println("I'm working in thread ${Thread.currentThread().name}")
    }
//sampleEnd
}


//Managing lifecycles of our coroutines by creating an instance of 'CoroutineScope'

// CoroutineScope() => general purpose scope
// MainScope() => scope for UI applications & uses 'Dispatcher.Main' as default dispatcher

class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default){
    private val mainScope = MainScope()

    fun destroy() {
        mainScope.cancel()
    }

// instead of defining 'mainscope' as an instance,
// alternatively, u can implement 'CoroutineScope' interface in this 'Activity' class

//class Activity: CoroutineScope by CoroutineScope(Dispatchers.Default)
// Now we can launch coroutines in the scope of this 'Activity' without having to specify the context

    //inside the activity class
// class Activity continues
    fun doSomething() {
        // launch ten coroutines for a demo, each working for a different time
        repeat(10) { i ->
            launch{
                delay((i + 1) * 200L) // variable delay 200ms, 400ms, ... etc
                println("Coroutine $i is done")
            }
        }
    }
}


// passing data between coroutines, using threadlocal
// using 'asContextElement' extension function is here for the rescue,

val threadLocal = ThreadLocal<String?>() // declare thread-local variable

fun contextSwitching() = runBlocking<Unit> {
    //sampleStart
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
//sampleEnd
}


/*

Pre-main, current thread: Thread[main @coroutine#1,5,main], thread local value: 'main'
Launch start, current thread: Thread[DefaultDispatcher-worker-1 @coroutine#2,5,main], thread local value: 'launch'
After yield, current thread: Thread[DefaultDispatcher-worker-2 @coroutine#2,5,main], thread local value: 'launch'
Post-main, current thread: Thread[main @coroutine#1,5,main], thread local value: 'main'

*/
