package com.experiment

//Generics


//generic type
class Loot<T>(var loot: T) {  //notice the class name has <T> defined, otherwise it's a compilation error
    var open = false

    //generic function
    fun fetch(): T? {
        return loot.takeIf { open }
    }
}




class Fedora(val name: String, val value: Int)

class Coin(val value: Int)


fun main() {
    val lootBoxOne: Loot<Fedora> = Loot(Fedora("a generic-looking fedora", 15))
    val lootBoxTwo: Loot<Coin> = Loot(Coin(12))

    lootBoxOne.fetch()?.run {               //'run' returning the last statement
        println("You retrieve $name from the box!")
    }

}