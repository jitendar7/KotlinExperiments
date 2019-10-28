package com.experiment

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Suspending functions asynchronously returns a 'single value', how can we return multiple asynchronous computed values?
// Kotlin Flows is the solution

//Flow<Int>
fun foo(): Flow<Int> = flow {    //flow builder
    for (i in 1..3) {
        delay(100)      // replacing this with Thread.sleep, will block the main thread
        emit(i)             //emit next value
    }
}

fun main() = runBlocking<Unit>{
    // Launch a concurrent coroutine to check if the main thread is blocked
    launch{
        for(k in 1..3){
            println("I'm not blocked $k")
            delay(100)
        }
    }

    //Collect the flow
    foo().collect{value -> println(value)}
}

/*
    1. A builder function for 'Flow' is 'flow'
    2. Code inside the flow{ ... } builder block can suspend
    3. The function foo() is no longer marked with 'suspend' modifier
    4. Values are emitted from the flow using 'emit' function
    5. Values are collected from the flow using 'collect' function

 */