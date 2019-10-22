package com.experiment

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// Ref: https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/composing-suspending-functions.md

fun main() {

    // Suspended functions are called 'sequential' by default

    runBlocking {

        val time = measureTimeMillis {
            //sequential execution
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()

            println("The answer is ${one + two}")
        }

        println("Completed in $time ms")
    }

    // In Kotlin, explicit concurrency

    runBlocking {

        val time = measureTimeMillis {
            //concurrent execution
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }

            println("The answer is ${one.await() + two.await()}")
        }

        println("Completed in $time ms")

    }

    // Lazily started 'async'

    runBlocking {

        val time = measureTimeMillis {
            //concurrent execution
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }

            //explicitly start
            one.start()
            two.start()

            println("The answer is ${one.await() + two.await()}")   //or implicitly start by calling 'wait()'
        }

        println("Completed in $time ms")

    }


    // Structured Concurrency

    runBlocking {

        val time = measureTimeMillis {
            println("The answer is ${concurrentSum()}")
        }

        println("Completed in $time ms")
    }

    //Cancellation is always propagated through coroutines hierarchy

    runBlocking {
        try {
            failedConcurrentSum()
        } catch (e: ArithmeticException) {
            println("Computation failed due to Arithmetic Exception")
        }

    }
}

suspend fun failedConcurrentSum() = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE)
            42
        } finally {
            println("First Child cancelled")
        }
    }

    val two = async<Int> {
        println("Second Child throws Exception")
        throw ArithmeticException()
    }

    one.await() + two.await()
}


suspend fun concurrentSum() = coroutineScope {

    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }

    one.await() + two.await()
}


suspend fun doSomethingUsefulOne(): Int {
    delay(1000)
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1900)
    return 71
}

