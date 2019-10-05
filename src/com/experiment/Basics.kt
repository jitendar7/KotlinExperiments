package com.experiment

import java.lang.Math.round

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


fun main() {

    //0.1 + 0.2 = 0.3
    val input = generateSequence { readLine() }
    println(input
        .map { it.toBigDecimal() }
        .fold(0.toBigDecimal()) { acc, element -> element + acc }
    )

    println(sampleGenerateFunction().take(10).toList())

    /* println(input
         .map { it.toDouble() }
         .fold(0.0) { acc, element -> round2(element + acc).toDouble() }
     )*/
}