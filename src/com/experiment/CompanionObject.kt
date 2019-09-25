package com.experiment


//Companion objects are defined for 'static constants, static methods' just like in java

const val yellow = "Aiasdk"

fun main(args: Array<String>) {

    Foo.test()


}

class Foo {
    companion object Hello{

        @JvmStatic
        fun test() {
            println("This is rocking")
        }
    }
}