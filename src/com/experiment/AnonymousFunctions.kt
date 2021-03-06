package com.experiment

// Exploring Anonymous functions ( Lambda functions)
// Reference Book-  Kotlin Programming: The Big Nerd Ranch Guide

fun main(args: Array<String>){
    //anonymous function defined inside a curly brace { }

    //Example
    {
        val test = "World"
        "Hello $test"
    }()     //notice the braces, this will call the anonymous function

    println(
        {
            val test = "World"
            "Hello print $test"
        }()
    )

    //Anonymous functions have a type called 'function type'
    //Assigning anonymous function to a variable
    val testFunction: () -> String =
        {
            val test = "World"
            "Hello $test"   //Implicit return
        }

    println("testFunction => "+testFunction)
    println("testFunction() =>"+testFunction())

    //Anonymous function with arguments

    val testFunction2: (String) -> String = //notice (String) argument for the anonymous function
        {
            "Welcome $it"   //implicitly the argument is 'it'
        }

    //with multiple arguments
    val testFunction3: (String, Int) -> String =    // explicit type definition i.e. (String, Int) -> String
        {name, data ->           //notice the 'it' is no longer valid , we need to mention the arguments explicitly, in this case
                                 // it is 'name', 'data'
            "Welcome $name $data times"
        }

    println(testFunction2("Jitendar"))
    println(testFunction3("Mandali",3))

    //Type Interface Support
    //If a variable is given an anonymous function as its value when it is declared,
    //  no explicit type definition is needed
    // i.e.

    val testFunction4 = {           //no explicit type definition
        "Hello World"
    }

    val testFunction5 = {               //no explicit type definition
        name: String, data: Int ->      //notice the types are defined for the aguments
        "Welcome $name $data times"
    }

}