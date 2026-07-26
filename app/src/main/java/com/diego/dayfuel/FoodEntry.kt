package com.diego.dayfuel

data class FoodEntry(
    val name: String,
    val calories: Int,
    val meal: MealType,
    val date: String,
    val time: Long
)
