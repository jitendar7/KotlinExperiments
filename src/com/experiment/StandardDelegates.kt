package com.experiment

// lazy() is a function that takes a lambda and returns an instance of Lazy<T>, which serves as delegate for implementing
// a lazy property: the first call to get() executes the lambda passed to lazy() and remembers the result,
// subsequent calls to get() simply return the remembered result

val lazyValue: String by lazy{
    println("computed!")
    "Hello"
}

//By default, the evaluation of lazy properties is 'synchronized': the value is computed only in one thread, and all threads
// will see the same value

// Passing LazyThreadSafetyMode.PUBLICATION as a parameter to the lazy function ( if we don't need synchronization)
// If we are sure that the initialization will always happen on the same thread as the one where you use the property, we can use
// LazyThreadSafetyMode.NONE, it doesn't incur any thread-safety guarantees

fun main(){
    println(lazyValue)
    println(lazyValue)
}

//computed!
//Hello
//Hello