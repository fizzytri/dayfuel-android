package com.diego.dayfuel

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var data: DataManager
    private lateinit var entryList: ListView
    private lateinit var summaryText: TextView
    private lateinit var emptyText: TextView

    private var entries = ArrayList<FoodEntry>()
    private var showAll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = DataManager(this)

        entryList = findViewById(R.id.entryList)
        summaryText = findViewById(R.id.summaryText)
        emptyText = findViewById(R.id.emptyText)

        val todayButton = findViewById<Button>(R.id.todayButton)
        todayButton.setOnClickListener {
            showAll = false
            showEntries()
        }

        val allButton = findViewById<Button>(R.id.allButton)
        allButton.setOnClickListener {
            showAll = true
            showEntries()
        }

        entryList.setOnItemLongClickListener { _, _, position, _ ->
            askDelete(entries[position])
            true
        }

        showEntries()
    }

    private fun showEntries() {
        if (showAll) {
            entries = data.getAllEntries()
        } else {
            entries = data.getEntries(data.getToday())
        }
        entries.sortByDescending { it.time }

        val adapter = EntryAdapter(this, entries)
        entryList.adapter = adapter

        var total = 0
        for (entry in entries) {
            total += entry.calories
        }
        summaryText.text = entries.size.toString() + " entries, " + total + " kcal"

        if (entries.size == 0) {
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE
        }
    }

    private fun askDelete(entry: FoodEntry) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete entry?")
        builder.setMessage(entry.name + " will be removed from your log.")
        builder.setNegativeButton("Cancel", null)
        builder.setPositiveButton("Delete") { _, _ ->
            data.deleteEntry(entry)
            showEntries()
            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}
