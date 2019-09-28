package com.experiment

//1. "Transform function" returns a copy of the modified collection
// and execution proceeds to the next function in the chain (original collection unchanged)
// Two commonly used transforms are 'map' and 'flatMap'


//2. "Filter function" accepts a predicate function that checks each element in a collection against a condition
// and returns either true or false. If the predicate returns 'true' add element to the new collection


//3. "Combines" takes difference collections and merge them into a new one.
// 'zip' , 'fold'

val animals = listOf("zebra", "giraffi", "elephant", "rat")

fun main() {

    val babies = animals.map { animal -> "A baby $animal" }
    println(babies)

    //flatmap works with collection of collections
    println(listOf(listOf(1, 2, 4), listOf(6, 7, 8)).flatMap { it })

    val itemsOfManyColors = listOf("red apple", "green apple", "red fish", "pink elephant")
    println(itemsOfManyColors.filter { it.contains("red") })

    //'zip' combines two collections and merge them
    println(animals.zip(itemsOfManyColors))
    println(animals.zip(itemsOfManyColors).toMap())

    //'fold' accepts initial accumulator value, which is updated
    // with result of an anonymous function that is called for each item. The accumulator then carried forward to
    // the next anonymous function
    val foldedValue = listOf(1, 2, 3, 4).fold(0) { accumulator, number ->
        println("Accumulated value: $accumulator")
        accumulator + (number * 3)
    }


    //lazy collection -> sequences
    generateSequence(0) { it + 1 }

}