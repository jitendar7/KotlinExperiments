package com.experiment


const val DEBUGGABLE = false

//Higher order functions
fun <T, R> Collection<T>.fold(initial: R, combine: (acc: R, nextElement: T) -> R): R {
    var accumulator: R = initial
    for (element: T in this) {
        accumulator = combine(accumulator, element)
    }

    return accumulator
}

//How to capture debuggable logs at runtime using higher order functions
fun log(block: () -> Unit) {
    if (DEBUGGABLE) {
        block()
    }
}

fun main(args: Array<String>) {

    log {
        println("This is a Test Log")
    }

//Note the removal of second argument & attaching { }
    val items = listOf(1, 2, 3, 4, 5)
    items.fold(0) { acc: Int, i: Int ->
        println("acc = $acc, i = $i, ")
        val result = acc + i
        println("result = $result")
        result
    }

// Output for the above

//    acc = 0, i = 1, result = 1
//    acc = 1, i = 2, result = 3
//    acc = 3, i = 3, result = 6
//    acc = 6, i = 4, result = 10
//    acc = 10, i = 5, result = 15

    val joinedToString = items.fold("Elements: ", { acc, i -> acc + " " + i })
    println("joinedToString = $joinedToString")

//    joinedToString = Elements: 1 2 3 4 5
}