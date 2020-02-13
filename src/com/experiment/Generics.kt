package com.experiment

//Generics
//Ref; https://kotlinlang.org/docs/reference/generics.html

//Generic type
class LootBox<T : Loot>(vararg item: T) {  //notice the class name has <T> defined, otherwise it's a compilation error
    //also notice that <T: Loot> will allow only classes which extend Loot class
    var open = false

    private var loot: Array<out T> = item   //notice the <out T> defined, vararg effect

    //generic function
    fun fetch(item: Int): T? {
        return loot[item].takeIf { open }
    }

    //multiple generic parameters
    fun <P> fetch(modFunction: (P)) {       //notice the '<P>' parameter defined after fun

    }

    fun <R> fetch(item: Int, lootModFunction: (T) -> R): R? {   //notice only '<R>' parameter defined after fun
        return lootModFunction(loot[item]).takeIf { open }
    }
}

open class Loot(val value: Int) //Generic class, which is now restricting LootBox

class Fedora(val name: String, value: Int) : Loot(value)

class Coin(value: Int) : Loot(value)


//producer, consumer generic types
//producer => readable (out) => defined as 'val' , eg: Lists are producers
//consumer => writable (in)
class Barrel1<out T>(val item: T) // notice that 'out', 'val' , producer

class Barrel2<in T>(item: T)      //notice that 'in', NO 'val'/'var' , consumer

//'out' keyword says that methods in a 'List' can only return type 'E' and they cannot take any 'E' type as an argument
// 'extends => out'

// interface List<out E> {
//  fun get(index: Int): E
// }

// 'in' keyword says that all methods in side the class can have 'T' as an argument but cannot return 'T' type
// interface Compare<in T> {
//  fun compare(first: T, second: T): Int
// }


inline fun <reified T>       // 'reified' keyword used for preserving type information at runtime
        randomOrBackupLoot(backupLoot: () -> T): T {
    val items = listOf(Coin(14), Fedora("a fedora", 150))
    val first: Loot = items.shuffled().first()
    return if (first is T) {        //notice this
        first
    } else {
        backupLoot()
    }
}


fun main() {
    //notice the <Loot> is the type defined for 'LootBox'
    val lootBoxOne: LootBox<Fedora> = LootBox(Fedora("a generic-looking fedora", 15))
    val lootBoxTwo: LootBox<Loot> = LootBox(Coin(12))

    //Below is no longer valid
    // val Coin: coin = lootBoxTwo.loot

    lootBoxOne.open = true
    lootBoxOne.fetch(0)?.run {
        //'run' returning the last statement
        println("You retrieve $name from the box!")
    }

    val coin = lootBoxOne.fetch(0) {
        Coin(it.value * 3)
    }
    coin?.let { println(it.value) }


    var fedoraBarrel: Barrel1<Fedora> = Barrel1(Fedora("a generic-looking fedora", 15))
    var lootBarrel: Barrel1<Loot>

    lootBarrel = fedoraBarrel       // this is possible only bcz it is 'out'
    val myFedora = lootBarrel.item  // smart casted

    var fedoraBarrel2: Barrel2<Fedora> = Barrel2(Fedora("a generic-looking fedora", 15))
    var lootBarrel2: Barrel2<Loot> = Barrel2(Coin(19))

    //lootBarrel2 = fedoraBarrel2       //this is not possible only bcz it it 'in'
    fedoraBarrel2 = lootBarrel2


}