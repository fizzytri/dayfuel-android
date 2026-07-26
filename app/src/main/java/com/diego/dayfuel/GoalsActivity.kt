package com.diego.dayfuel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class GoalsActivity : AppCompatActivity() {

    private lateinit var data: DataManager
    private lateinit var calorieGoalInput: EditText
    private lateinit var waterGoalText: TextView
    private lateinit var waterSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = DataManager(this)

        calorieGoalInput = findViewById(R.id.calorieGoalInput)
        waterGoalText = findViewById(R.id.waterGoalText)
        waterSeekBar = findViewById(R.id.waterSeekBar)

        calorieGoalInput.setText(data.getCalorieGoal().toString())

        waterSeekBar.progress = data.getWaterGoal() - 1
        showWaterGoal(data.getWaterGoal())

        waterSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                showWaterGoal(progress + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val saveButton = findViewById<Button>(R.id.saveGoalsButton)
        saveButton.setOnClickListener {
            saveGoals()
        }

        val resetButton = findViewById<Button>(R.id.resetTodayButton)
        resetButton.setOnClickListener {
            askReset()
        }
    }

    private fun showWaterGoal(glasses: Int) {
        waterGoalText.text = "Daily water goal: " + glasses + " glasses"
    }

    private fun saveGoals() {
        val calorieGoal = calorieGoalInput.text.toString().trim().toIntOrNull()
        if (calorieGoal == null || calorieGoal < 800 || calorieGoal > 6000) {
            Toast.makeText(this, "Use a calorie goal between 800 and 6000", Toast.LENGTH_SHORT).show()
            return
        }

        data.setCalorieGoal(calorieGoal)
        data.setWaterGoal(waterSeekBar.progress + 1)

        Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun askReset() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset today?")
        builder.setMessage("Every food entry and glass of water logged today will be deleted.")
        builder.setNegativeButton("Cancel", null)
        builder.setPositiveButton("Delete") { _, _ ->
            data.clearDay(data.getToday())
            Toast.makeText(this, "Today's data cleared", Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.show()
    }
}
