package com.diego.dayfuel

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var data: DataManager

    private lateinit var dateText: TextView
    private lateinit var calorieText: TextView
    private lateinit var calorieBar: ProgressBar
    private lateinit var calorieLeftText: TextView
    private lateinit var waterText: TextView
    private lateinit var waterBar: ProgressBar
    private lateinit var reminderSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        data = DataManager(this)

        dateText = findViewById(R.id.dateText)
        calorieText = findViewById(R.id.calorieText)
        calorieBar = findViewById(R.id.calorieBar)
        calorieLeftText = findViewById(R.id.calorieLeftText)
        waterText = findViewById(R.id.waterText)
        waterBar = findViewById(R.id.waterBar)
        reminderSwitch = findViewById(R.id.reminderSwitch)

        createNotificationChannel()

        val addFoodButton = findViewById<Button>(R.id.addFoodButton)
        addFoodButton.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }

        val viewLogButton = findViewById<Button>(R.id.viewLogButton)
        viewLogButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        val goalsButton = findViewById<Button>(R.id.goalsButton)
        goalsButton.setOnClickListener {
            val intent = Intent(this, GoalsActivity::class.java)
            startActivity(intent)
        }

        val addWaterButton = findViewById<Button>(R.id.addWaterButton)
        addWaterButton.setOnClickListener {
            data.addWater(data.getToday())
            showData()
        }

        val removeWaterButton = findViewById<Button>(R.id.removeWaterButton)
        removeWaterButton.setOnClickListener {
            data.removeWater(data.getToday())
            showData()
        }

        reminderSwitch.isChecked = data.getRemindersOn()
        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                askNotificationPermission()
                turnRemindersOn()
            } else {
                turnRemindersOff()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showData()
    }

    private fun showData() {
        val today = data.getToday()
        val calories = data.getTotalCalories(today)
        val calorieGoal = data.getCalorieGoal()
        val water = data.getWater(today)
        val waterGoal = data.getWaterGoal()

        val dateFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
        dateText.text = dateFormat.format(Date())

        calorieText.text = calories.toString() + " / " + calorieGoal + " kcal"
        calorieBar.progress = getPercent(calories, calorieGoal)

        val left = calorieGoal - calories
        if (left >= 0) {
            calorieLeftText.text = left.toString() + " kcal left"
        } else {
            calorieLeftText.text = (-left).toString() + " kcal over goal"
        }

        waterText.text = water.toString() + " / " + waterGoal + " glasses"
        waterBar.progress = getPercent(water, waterGoal)
    }

    private fun getPercent(value: Int, goal: Int): Int {
        if (goal <= 0) {
            return 0
        }
        var percent = value * 100 / goal
        if (percent > 100) {
            percent = 100
        }
        return percent
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "water",
                "Water reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (granted != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    private fun turnRemindersOn() {
        data.setRemindersOn(true)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val twoHours = AlarmManager.INTERVAL_HOUR * 2
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + twoHours,
            twoHours,
            pendingIntent
        )
        Toast.makeText(this, "Reminders on", Toast.LENGTH_SHORT).show()
    }

    private fun turnRemindersOff() {
        data.setRemindersOn(false)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Reminders off", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            shareSummary()
            return true
        }
        if (item.itemId == R.id.action_goals) {
            val intent = Intent(this, GoalsActivity::class.java)
            startActivity(intent)
            return true
        }
        if (item.itemId == R.id.action_about) {
            showAbout()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareSummary() {
        val today = data.getToday()
        val text = "DayFuel\n" +
            data.getTotalCalories(today) + " / " + data.getCalorieGoal() + " kcal\n" +
            data.getWater(today) + " / " + data.getWaterGoal() + " glasses of water"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, "Share"))
    }

    private fun showAbout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("About DayFuel")
        builder.setMessage("A calorie and water tracker made for the Software Development Skills mobile module.")
        builder.setPositiveButton("Close", null)
        builder.show()
    }
}
