package com.diego.dayfuel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddEntryActivity : AppCompatActivity() {

    private lateinit var data: DataManager
    private lateinit var nameInput: EditText
    private lateinit var caloriesInput: EditText
    private lateinit var mealGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = DataManager(this)

        nameInput = findViewById(R.id.nameInput)
        caloriesInput = findViewById(R.id.caloriesInput)
        mealGroup = findViewById(R.id.mealGroup)

        selectMealByTime()

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveEntry()
        }

        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun selectMealByTime() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour < 11) {
            mealGroup.check(R.id.radioBreakfast)
        } else if (hour < 15) {
            mealGroup.check(R.id.radioLunch)
        } else if (hour < 22) {
            mealGroup.check(R.id.radioDinner)
        } else {
            mealGroup.check(R.id.radioSnack)
        }
    }

    private fun saveEntry() {
        val name = nameInput.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Give the food a name", Toast.LENGTH_SHORT).show()
            return
        }

        val caloriesText = caloriesInput.text.toString().trim()
        val calories = caloriesText.toIntOrNull()
        if (calories == null || calories < 1 || calories > 5000) {
            Toast.makeText(this, "Enter calories between 1 and 5000", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = FoodEntry(
            name,
            calories,
            getSelectedMeal(),
            data.getToday(),
            System.currentTimeMillis()
        )
        data.addEntry(entry)

        Toast.makeText(this, name + " added", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getSelectedMeal(): MealType {
        val id = mealGroup.checkedRadioButtonId
        if (id == R.id.radioBreakfast) {
            return MealType.BREAKFAST
        }
        if (id == R.id.radioLunch) {
            return MealType.LUNCH
        }
        if (id == R.id.radioDinner) {
            return MealType.DINNER
        }
        return MealType.SNACK
    }
}
