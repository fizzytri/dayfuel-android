package com.diego.dayfuel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntryAdapter(context: Context, private val entries: List<FoodEntry>) :
    ArrayAdapter<FoodEntry>(context, 0, entries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_entry, parent, false)
        }

        val entry = entries[position]

        val icon = view!!.findViewById<ImageView>(R.id.mealIcon)
        val nameText = view.findViewById<TextView>(R.id.entryName)
        val metaText = view.findViewById<TextView>(R.id.entryMeta)
        val caloriesText = view.findViewById<TextView>(R.id.entryCalories)

        when (entry.meal) {
            MealType.BREAKFAST -> icon.setImageResource(R.drawable.ic_breakfast)
            MealType.LUNCH -> icon.setImageResource(R.drawable.ic_lunch)
            MealType.DINNER -> icon.setImageResource(R.drawable.ic_dinner)
            MealType.SNACK -> icon.setImageResource(R.drawable.ic_snack)
        }

        val format = SimpleDateFormat("dd.MM. HH:mm", Locale.getDefault())
        nameText.text = entry.name
        metaText.text = getMealName(entry.meal) + " - " + format.format(Date(entry.time))
        caloriesText.text = entry.calories.toString() + " kcal"

        return view
    }

    private fun getMealName(meal: MealType): String {
        return when (meal) {
            MealType.BREAKFAST -> "Breakfast"
            MealType.LUNCH -> "Lunch"
            MealType.DINNER -> "Dinner"
            MealType.SNACK -> "Snack"
        }
    }
}
