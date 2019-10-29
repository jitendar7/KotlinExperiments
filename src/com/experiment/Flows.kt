package com.experiment

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
    cold_flow()
    main_intermediate_flow_of_operators()
}

/*
    1. A builder function for 'Flow' is 'flow'
    2. Code inside the flow{ ... } builder block can suspend
    3. The function foo() is no longer marked with 'suspend' modifier
    4. Values are emitted from the flow using 'emit' function
    5. Values are collected from the flow using 'collect' function

 */

// Flows are 'cold' streams, similar to sequences - the code inside the flow is not run until the flow is collected.
fun cold_flow() = runBlocking {
    println("Calling foo...")
    val flow = foo()
    println("Calling collect...")
    flow.collect { value -> println(value) }
    println("Calling collect again...")
    flow.collect { value -> println(value) }
}

/*
    Calling foo...
    Calling collect...
    Flow started
    1
    2
    3
    Calling collect again...
    Flow started
    1
    2
    3
*/

// Flow adheres to the general cooperative cancellation of coroutines - flow collection can be cancelled when the flow is
// suspended in a cancellable suspending functions and cannot be cancelled otherwise.

// Other builders of flow are
// 'flowOf' flow emitting a fixed set of values
// various collections & sequences can be converted to flows using .asFlow()

// Example: (1..3).asFlow().collect { value -> println(value) }

//Intermediate flow operators
//Flows can be transformed with operators, just as you would with collections & sequences
// Intermediate operators are applied to an upstream flow & return a downstream flow

// The difference to sequences is that blocks of code inside these operators can call suspending functions.


suspend fun performRequest(request: Int): String {
    delay(1000) // imitate long-running asynchronous work
    return "response $request"
}

fun main_intermediate_flow_of_operators() = runBlocking<Unit> {
    (1..3).asFlow() // a flow of requests
        .map { request -> performRequest(request) }
        .collect { response -> println(response) }      // 'cold'
}



