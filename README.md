# DayFuel

Diako Jalal, student number: 2524490
Software Development Skills, mobile module

DayFuel is a small Android app for tracking how many calories you eat and how many glasses of
water you drink in a day. I picked this because I wanted something I would actually open, and
because it gave me a reason to use most of the things from the module videos.

## How to run it

1. Open Android Studio.
2. Choose File -> Open and select this folder (the one with settings.gradle.kts in it).
3. Wait for Gradle to finish syncing. The first time this takes a while.
4. Start an emulator from Device Manager, or plug in a phone with USB debugging on.
5. Press the green Run button.

The app needs Android 8.0 or newer.

## What the app does

- The main screen shows today's calories and water against your goals, with progress bars.
- Add food takes a name, the calories and which meal it was, and saves it.
- Food log lists everything in a ListView. You can switch between today and all days,
  and long press a row to delete it.
- Daily goals lets you change the calorie goal and the water goal, or wipe today's data.
- The menu has a share option that sends your day as text to another app.
- You can turn on water reminders, which show a notification every couple of hours.

## What I used

- Kotlin
- Four activities and intents to move between them
- A ListView with my own adapter and my own row layout
- ImageView with vector drawables I made for the meals
- SharedPreferences for saving, the entries are stored as JSON text
- A BroadcastReceiver plus AlarmManager for the water reminders
- Material components for the cards and buttons

## Things that could be better

- The reminder is every two hours and you cannot change the time.
- The food log reloads the whole list every time instead of only the row that changed.
- There is no way to edit an entry, you have to delete it and add it again.
- No dark theme.

## Repository contents

- This README
- The Android Studio project
- VIDEO.md with the link to my demo video
- My learning diary
