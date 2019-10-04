package com.experiment

import java.lang.Math.round

//Floating point for decimals
//https://medium.com/@elizarov/floating-point-for-decimals-fc2861898455


fun round2(x: Double) = round(x * 100) / 100


fun main() {

    //0.1 + 0.2 = 0.3
    val input = generateSequence{readLine()}
    println(input
        .map { it.toBigDecimal() }
        .fold(0.toBigDecimal()) { acc, element -> element + acc }
    )

    println(input
        .map { it.toDouble() }
        .fold(0.0) { acc, element -> round2(element + acc).toDouble() }
    )
}