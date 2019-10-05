package com.experiment

import kotlinx.coroutines.*
import java.lang.Math.round
import java.math.BigInteger
import java.util.*

//Floating point for decimals
//https://medium.com/@elizarov/floating-point-for-decimals-fc2861898455
//https://proandroiddev.com/what-is-concurrent-access-to-mutable-state-f386e5cb8292


//Relation between 'Concurrent' & 'Synchronization'
// Using 'suspend' function guarentees "happens before" relationship, it performs 'synchronization', it is not
// 'Concurrent' (sequential)
// Two operations are concurrent, if they are not ordered by "happens before" relation
// No two actions in the same coroutine can be concurrent, avoid sharing mutable state between coroutines


fun round2(x: Double) = round(x * 100) / 100

//'yield()' meaning => return, but next time start where you stopped
fun sampleGenerateFunction() = sequence {
    val start = 0
    yield(start)
    yieldAll(1..5 step 2)
    yieldAll(generateSequence(8) { it * 3 })
}

suspend fun findBigPrime(): BigInteger = withContext(Dispatchers.Default) {
    //Default dispatcher is optimized for such CPU-bound code, backed by Threadpool with as many as there are
    // CPU Cores in the system, making sure CPU bound code can saturate all physical resources as needed.
    // It doesn't over allocate threads, since that would not help to execute CPU bound tasks faster, but only waste memory
    BigInteger.probablePrime(4096, Random())
}

//Similar to Dispatchers.Default, we have Dispatchers.IO ,
// IO bound code does not actually consume CPU resources, so if we use the default dispatcher we may end
// up with a situation when, for example, on an 8-core machine with 8 threads allocated to the default dispatcher, all
// of the threads are blocked on IO, but they do not consume CPU, so our 8 core machine is underutilized.
//IO dispatcher allocates additional threads on top of the ones allocated to the default dispatcher, so we
// can use blocking IO and fully utilize machine's CPU resources at the same time

//use 'withContext' for non-blocking suspended function
//use 'delay' asynchronous library function, instead of 'Thread.sleep'

fun main() {

    //0.1 + 0.2 = 0.3
    val input = generateSequence { readLine() }
    println(input
        .map { it.toBigDecimal() }
        .fold(0.toBigDecimal()) { acc, element -> element + acc }
    )

    println(sampleGenerateFunction().take(10).toList())

    runBlocking {
        launch {
            println(findBigPrime())
        }
        println("Test the block")
    }

    println("Hello there..")
    /* println(input
         .map { it.toDouble() }
         .fold(0.0) { acc, element -> round2(element + acc).toDouble() }
     )*/
}