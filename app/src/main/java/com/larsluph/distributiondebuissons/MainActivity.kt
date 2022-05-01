package com.larsluph.distributiondebuissons

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*


const val ANY: Int = 0
const val YELLOW: Int = 1
const val ORANGE: Int = 2
const val GREEN: Int = 3

class MainActivity : AppCompatActivity() {

    private var currentUser: String = ""
        set(value) {
            field = value
            updateDisplay()
        }
    private var today: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        set(value) {
            field = value
            updateDisplay()
        }
    private var lastColor: Int? = null

    private val users: Array<String> = arrayOf("Christel", "Lorianne", "Doro", "Aisha", "Aurèle", "Claudie", "Eva", "Sandrine", "Tulya", "Isabelle", "Jocelyne", "Angie", "Amandine", "Christian", "Sam")
    private val aliases: Array<String> = arrayOf("Christel", "Lorianne", "Pupuce", "Kikobiso", "Aurèle", "ISIS", "Eva 21200", "GlobeCookeuse", "Lila", "Tatazaza", "Jocelyne", "Mrs JONES Angie", "Paradises'Isle", "TAZ'ISLAND", "Sam")
    private val buissons: Array<Int> = arrayOf(YELLOW, ANY, ANY, ORANGE, ANY, ANY, GREEN, ANY, ANY, ORANGE, ANY, GREEN, ANY, ANY)
    private var isPopupOpened: Boolean = false
    private val neutralDay = users.size

    private fun cycleUserArray(arr: Array<Int>, i: Int): Array<Int> {
        if (i % neutralDay == 0 || i == 31) return Array(buissons.size) { ANY }

        val index = i % neutralDay - 1
        return arr.sliceArray(arr.count()-index until arr.count()) + arr.sliceArray(0 until arr.count()-index)
    }
    private fun cycleDayArray(arr: Array<String>, i: String): Array<String> {
        return arr.sliceArray(arr.indexOf(i) + 1 until arr.count()) + arr.sliceArray(0 until arr.indexOf(i))
    }

    private fun selectIdentity() {
        isPopupOpened = true

        AlertDialog.Builder(this)
                .setTitle("Qui êtes-vous ?")
                .setItems(users) { _, which -> currentUser = aliases[which];isPopupOpened = false }
                .show()
    }

    private fun updateDisplay() {
        Log.d(null, today.toString())
        val dests = cycleDayArray(aliases, currentUser)
        val buiss = cycleUserArray(buissons, today)

        findViewById<TextView>(R.id.selectedTextView).text = "Jour $today : $currentUser"

        findViewById<TextView>(R.id.textViewYellow).text = ""
        findViewById<TextView>(R.id.textViewGreen).text = ""
        findViewById<TextView>(R.id.textViewOrange).text = ""
        findViewById<TextView>(R.id.textViewAny).text = ""
//        findViewById<TextView>(R.id.textViewYellow).text = "Rouge :\n"
//        findViewById<TextView>(R.id.textViewGreen).text = "Vert :\n"
//        findViewById<TextView>(R.id.textViewOrange).text = "Bleu :\n"
//        findViewById<TextView>(R.id.textViewAny).text = "Temporaire ou Rien :\n"

        for (i in 0 until dests.count()) {
            val txt: TextView = findViewById(when (buiss[i]) {
                YELLOW -> R.id.textViewYellow
                GREEN -> R.id.textViewGreen
                ORANGE -> R.id.textViewOrange
                else -> R.id.textViewAny
            })

            when {
                txt.text == "" -> txt.text = txt.text.toString() + dests[i]
                lastColor != buiss[i] && buiss[i] == ANY -> txt.text = txt.text.toString() + "\n\n" + dests[i]
                else -> txt.text = txt.text.toString() + "\n" + dests[i]
            }

            lastColor = buiss[i]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (currentUser == "" && !isPopupOpened) selectIdentity()
    }

    override fun onResume() {
        super.onResume()
        if (currentUser == "" && !isPopupOpened) {
            selectIdentity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        calendar.roll(Calendar.MONTH, true)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val maxMonthDay = calendar.get(Calendar.DAY_OF_MONTH)

        when (item.itemId) {
            R.id.reset_actionbar -> today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            R.id.minus_actionbar -> today = if (today <= 1) 1 else today-1
            R.id.plus_actionbar -> today = if (today >= maxMonthDay) maxMonthDay else today+1
            R.id.user_actionbar -> selectIdentity()
        }
        return true
    }
}
