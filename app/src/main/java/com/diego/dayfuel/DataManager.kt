package com.diego.dayfuel

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataManager(context: Context) {

    private val prefs = context.getSharedPreferences("dayfuel", Context.MODE_PRIVATE)

    fun getToday(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

    fun getCalorieGoal(): Int {
        return prefs.getInt("calorieGoal", 2200)
    }

    fun setCalorieGoal(goal: Int) {
        prefs.edit().putInt("calorieGoal", goal).apply()
    }

    fun getWaterGoal(): Int {
        return prefs.getInt("waterGoal", 8)
    }

    fun setWaterGoal(goal: Int) {
        prefs.edit().putInt("waterGoal", goal).apply()
    }

    fun getRemindersOn(): Boolean {
        return prefs.getBoolean("reminders", false)
    }

    fun setRemindersOn(on: Boolean) {
        prefs.edit().putBoolean("reminders", on).apply()
    }

    fun getWater(date: String): Int {
        return prefs.getInt("water_" + date, 0)
    }

    fun addWater(date: String) {
        val glasses = getWater(date) + 1
        prefs.edit().putInt("water_" + date, glasses).apply()
        saveDate(date)
    }

    fun removeWater(date: String) {
        var glasses = getWater(date) - 1
        if (glasses < 0) {
            glasses = 0
        }
        prefs.edit().putInt("water_" + date, glasses).apply()
    }

    fun getEntries(date: String): ArrayList<FoodEntry> {
        val list = ArrayList<FoodEntry>()
        val text = prefs.getString("entries_" + date, null)
        if (text == null) {
            return list
        }
        val array = JSONArray(text)
        for (i in 0 until array.length()) {
            val json = array.getJSONObject(i)
            val entry = FoodEntry(
                json.getString("name"),
                json.getInt("calories"),
                MealType.valueOf(json.getString("meal")),
                date,
                json.getLong("time")
            )
            list.add(entry)
        }
        return list
    }

    fun getAllEntries(): ArrayList<FoodEntry> {
        val list = ArrayList<FoodEntry>()
        for (date in getDates()) {
            list.addAll(getEntries(date))
        }
        return list
    }

    fun addEntry(entry: FoodEntry) {
        val list = getEntries(entry.date)
        list.add(entry)
        saveEntries(entry.date, list)
        saveDate(entry.date)
    }

    fun deleteEntry(entry: FoodEntry) {
        val list = getEntries(entry.date)
        for (i in list.indices) {
            if (list[i].time == entry.time) {
                list.removeAt(i)
                break
            }
        }
        saveEntries(entry.date, list)
    }

    fun clearDay(date: String) {
        prefs.edit().remove("entries_" + date).apply()
        prefs.edit().remove("water_" + date).apply()
    }

    fun getTotalCalories(date: String): Int {
        var total = 0
        for (entry in getEntries(date)) {
            total += entry.calories
        }
        return total
    }

    private fun saveEntries(date: String, list: ArrayList<FoodEntry>) {
        val array = JSONArray()
        for (entry in list) {
            val json = JSONObject()
            json.put("name", entry.name)
            json.put("calories", entry.calories)
            json.put("meal", entry.meal.name)
            json.put("time", entry.time)
            array.put(json)
        }
        prefs.edit().putString("entries_" + date, array.toString()).apply()
    }

    private fun getDates(): ArrayList<String> {
        val list = ArrayList<String>()
        val text = prefs.getString("dates", null)
        if (text == null) {
            return list
        }
        val array = JSONArray(text)
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

    private fun saveDate(date: String) {
        val dates = getDates()
        if (!dates.contains(date)) {
            dates.add(date)
            val array = JSONArray()
            for (d in dates) {
                array.put(d)
            }
            prefs.edit().putString("dates", array.toString()).apply()
        }
    }
}
