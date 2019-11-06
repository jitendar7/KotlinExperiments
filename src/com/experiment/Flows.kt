package com.experiment

import kotlinx.coroutines.Dispatchers
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


//Transform operator
// we can imitate simple transformations like map & filter, as well as more complex transformations.
// we can 'emit' arbitrary values an arbitrary number of times
fun main_transform() = runBlocking<Unit> {
    //sampleStart
    (1..3).asFlow() // a flow of requests
            .transform { request ->
                emit("Making request $request")
                emit(performRequest(request))
            }
            .collect { response -> println(response) }
//sampleEnd
}

/*
Making request 1
response 1
Making request 2
response 2
Making request 3
response 3
*/

//Size-limiting operators
// 'take' will cancel the execution of the flow when the corresponding limit is reached


fun numbers(): Flow<Int> = flow {
    try{
        emit(1)
        emit(2)
        println("This line will not execute")
        emit(3)

    } finally {
        println("Finally in numbers")
    }
}

fun main_take() = runBlocking{
    numbers()
            .take(2)
            .collect{value -> println(value)}
}

//1
//2
//Finally in numbers


//Terminal flow operators
// These are suspending functions, that start a collection of the flow, 'collect' operator is the most
// basic one, like , 'toList', 'toSet',
//                  operators to get the 'first' value and to ensure that a flow emits a 'single' value
//                  reducing a flow to value with 'reduce' and 'fold'

//  val sum = (1..5).asFlow()
//        .map { it * it } // squares of numbers from 1 to 5
//        .reduce { a, b -> a + b } // sum them (terminal operator)


//Flows are sequential

fun main_sequential() = runBlocking<Unit> {
    //sampleStart
    (1..5).asFlow()
        .filter {
            println("Filter $it")
            it % 2 == 0
        }
        .map {
            println("Map $it")
            "string $it"
        }.collect {
            println("Collect $it")
        }
//sampleEnd
}


//Filter 1
//Filter 2
//Map 2
//Collect string 2
//Filter 3
//Filter 4
//Map 4
//Collect string 4
//Filter 5


//Flow context
// Collection of flow always happen in the context of the calling coroutine, called context preservation


/*

withContext(context) {
    foo.collect { value ->
        println(value) // run in the specified context
    }

}*/

//By default, code in flow{ } builder runs in the context that is provided by a collector of the corresponding flow

//fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
//
//fun foo(): Flow<Int> = flow {
//    log("Started foo flow")
//    for (i in 1..3) {
//        emit(i)
//    }
//}
//
//fun main() = runBlocking<Unit> {
//    foo().collect { value -> log("Collected $value") }
//}


//[main @coroutine#1] Started foo flow
//[main @coroutine#1] Collected 1
//[main @coroutine#1] Collected 2
//[main @coroutine#1] Collected 3


//since the foo().collect is called in the main thread, the body of foo 's flow is also called in the main thread


//correct way of switching the context for flows using 'flowOn'

//sampleStart
fun flow_foo(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // pretend we are computing it in CPU-consuming way
        log("Emitting $i")
        emit(i) // emit next value
    }
}.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder
    // this also changes sequential behavior

fun main_flowOn() = runBlocking<Unit> {
    flow_foo().collect { value ->
        log("Collected $value")
    }
}

// here the collection happens in one coroutine & emission happens in another coroutine



//Buffering - > run emitting code concurrently with collecting code, as opposed to running them sequentially:

//fun foo(): Flow<Int> = flow {
//    for (i in 1..3) {
//        delay(100) // pretend we are asynchronously waiting 100 ms
//        emit(i) // emit next value
//    }
//}
//
//fun main() = runBlocking<Unit> {
//    val time = measureTimeMillis {
//        foo()
//            .buffer() // buffer emissions, don't wait
//            .collect { value ->
//                delay(300) // pretend we are processing it for 300 ms
//                println(value)
//            }
//    }
//    println("Collected in $time ms")
//}


//1
//2
//3
//Collected in 1071 ms


//Conflation -> can be used to skip intermediate values when a collector is too slow to process them.

//val time = measureTimeMillis {
//        foo()
//            .conflate() // conflate emissions, don't process each one
//            .collect { value ->
//                delay(300) // pretend we are processing it for 300 ms
//                println(value)
//            }
//    }

//here second number was conflated and only the most recent one was delivered to the collector

//1
//3
//Collected in 758 ms


// processing the latest value using xxxLatest

//val time = measureTimeMillis {
//        foo()
//            .collectLatest { value -> // cancel & restart on the latest value
//                println("Collecting $value")
//                delay(300) // pretend we are processing it for 300 ms
//                println("Done $value")
//            }
//    }

//Collecting 1
//Collecting 2
//Collecting 3
//Done 3
//Collected in 741 ms


//Composing Multiple flows

//    val nums = (1..3).asFlow() // numbers 1..3
//    val strs = flowOf("one", "two", "three") // strings
//    nums.zip(strs) { a, b -> "$a -> $b" } // compose a single string
//        .collect { println(it) } // collect and print

//1 -> one
//2 -> two
//3 -> three