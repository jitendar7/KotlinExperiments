package com.experiment

//Defining interfaces, abstract class & implementing the same

interface TestInt {
    var xyz: Int
    val diceCount: Int
        get() = (0 until 10).random()   //default property getters in implementation

    fun attack(): Int {                 //default function implementation
        return 0
    }
}

abstract class Villan : TestInt {       //notice there is no '( )' after TestInt
    abstract fun defence()

    override fun attack(): Int {
        println("Villan attack")
        return super.attack()           //super keyword used
    }
}

class Monster(override var xyz: Int) : Villan() {       //notice the override property variable
    override fun defence() {        //override keyword used for the function
        println("Defence - $xyz")   //string template
    }
}

fun main() {
    val monster = Monster(23)
    monster.defence()
    monster.attack()
}