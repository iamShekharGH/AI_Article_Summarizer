package com.shekharhandigol.aiarticlesummarizer.data.backup


import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Make it a singleton as it's a service
class AdditionService @Inject constructor() { // Simple constructor for now

    private val TAG = "AdditionService"

    fun add(num1: Int, num2: Int): Int {
        val sum = num1 + num2
        Log.d(TAG, "Adding $num1 + $num2 = $sum (from AdditionService)")
        return sum
    }
}