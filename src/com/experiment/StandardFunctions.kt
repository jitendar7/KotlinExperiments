package com.experiment

import java.io.File

//Working with Standard Functions - (similarities)
// apply, run
// run, with
// let , also
// takeIf, takeUnless

fun nameIsLong(name: String) = name.length >= 20

fun playerCreateMessage(nameTooLong: Boolean): String {
    return if (nameTooLong) {
        "Name is too long. Please choose another name."
    } else {
        "Welcome, adventurer"
    }
}

fun main() {

    // "apply" - is applied on receiver, for configuration & returns the receiver
    // Ex:
    val menuFile = File("menu-file.txt").apply {
        setReadable(true)
        setWritable(true)
        setExecutable(false)
    }

    //"run" - similar to "apply", except that it doesn't return the receiver, it returns a lambda result
    val menuFile2 = File("menu-file.txt")
    val servesDragonBreath = menuFile2.run {
        readText().contains("Dragon's Breath")
    }

    //"run" can also be used for function reference
    val testLength = "Test".run(::nameIsLong)

    //"run" can also add to chaining based on the lambda return value
    //Ex:
    "PolarCube"
        .run(::nameIsLong)
        .run(::playerCreateMessage)

    //"with" is a variant of "run", "with" requires an argument to be accepted as the first parameter
    // rather than calling it on the receiver

    val nameTooLong = with("PolarCube") {
        length > 20
    }

    // "let" - scopes a variable to the lambda provided, & returns the last statement
    // 'it' is readonly variable
    // Ex:
    val firstItemSquared = listOf(1, 2, 3).first().let {
        it * it
    }

    val guest: String? = "JM"
    guest?.let {
        "Welcome $it"
    }

    //"also" works very similar to the "let" function,
    //"also" passes the receiver you call it on as an argument & returns the receiver, rather than the result of lambda
    var fileConstants: List<String>
    File("file.txt").also {
        println(it.name)
    }.also {
        fileConstants = it.readLines()
    }


    //"takeIf" evaluates a condition provided in a lambda, called a predicate, ( returns either True or False )
    // if the condition evaluates to true, the 'receiver' is returned or 'null' if false
    val file = File("myFile.txt")
    val fileContents = if (file.canRead() && file.canWrite()) {
        file.readText()
    } else {
        null
    }

    //rewriting with "takeIf"
    val fileContents2 = File("myFile.txt").takeIf {
        it.canRead() && it.canWrite()
    }?.readText()

    //"takeUnless" same as "takeIf" except that the 'receiver' is returned if condition is false
    val fileContents3 = File("myFile.txt").takeUnless {
        it.isHidden
    }?.readText()

}