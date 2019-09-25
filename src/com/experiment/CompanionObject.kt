package com.experiment

const val yellow = "Aiasdk"

class Game {
    init {
        println("New Game created")
    }
}

// Singleton - declare the class as usual way, but use the
// keyword 'object' instead of 'class'
object GameFactory {
    var game = mutableListOf<Game>()

    fun makeGame() {
        println("Make a Game")
    }
}

//Companion object created when u need to tie a function or property to the 'class'
//rather than the instance of it

//Companion objects are defined for 'static constants / static methods' just like in java

class Foo {
    companion object Hello {
        @JvmStatic
        fun test() {
            println("This is rocking")
        }
    }
}

//Main Function
fun main(args: Array<String>) {

    //object call
    GameFactory.makeGame()
    GameFactory.game

    //companion object call
    Foo.test()
}
