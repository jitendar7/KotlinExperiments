package com.experiment

//Extension Functions
//notice the . after 'String', and usage of 'this'
fun String.addEnthusiasm(nTimes: Int = 1) = this+"!".repeat(nTimes)


//Defining extension on a superclass
fun Any.easyPrint() = println(this)


//Generic extension function

fun Any.easyPrint1(): Any{
    println(this)
    return this
}


fun <T> T.easyPrint2(): T {
    println(this)
    return this
}

//Extension Properties
val String.numVowels
    get() = count { "aeiouy".contains(it) }

//Extensions on NullableTypes
infix fun String?.printWithDefault(default: String) = print(this ?: default)

//public inline fun <T> T.apply(block: T.() -> Unit): T{        //notice the T.() , is responsible for access to the receiver instance's property
//}

fun <T> ArrayList<T>.filterOnCondition(condition: (T) -> Boolean): ArrayList<T> {
    val result = arrayListOf<T>()
    for (item in this) {
        if (condition(item)) {
            result.add(item)
        }
    }
    return result
}

inline fun isMultipleOf(number: Int, multipleOf: Int): Boolean {
    return number % multipleOf == 0
}


fun main(){
    println("Hello".addEnthusiasm(2))

    "Hello".addEnthusiasm(2).easyPrint()

    //Doesn't compile, bcz the type is 'Any'
    //"Hello1".easyPrint1().addEnthusiasm(2)

    //Works, for generic extension type
    "Hello2".easyPrint2().addEnthusiasm(3)

    "How many vowels?".numVowels.easyPrint()

     val nullable: String? = null
     nullable.printWithDefault("Default String")


    var list = arrayListOf<Int>()
    for (number in 1..10){
        list.add(number)
    }

    val resultList = list.filterOnCondition { isMultipleOf(it, 5) }
    print(resultList)

}