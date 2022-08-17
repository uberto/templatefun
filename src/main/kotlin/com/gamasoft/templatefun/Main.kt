package com.gamasoft.templatefun

import java.lang.ArithmeticException

    fun test(a: Int): Result<Int> = Result.success(a)
    fun test2(a: Int): Result<Int> = Result.failure(ArithmeticException("Not a valid number $a"))
    fun main(){

        val x = test(345)
        println("success $x")

        val y = test2(4)
        println("failure $y")
    }
