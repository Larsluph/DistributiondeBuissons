package com.larsluph.distributiondebuissons

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import java.util.*


const val ANY: Int = 0
const val RED: Int = 1
const val BLUE: Int = 2
const val GREEN: Int = 3

class MainActivity : AppCompatActivity() {

    private var currentUser: String = ""
        set(value) {
            field = value
            updateDisplay()
        }

    private val users: Array<String> = arrayOf("Christel", "Lorianne", "Doro", "Aisha", "Aurèle", "Claudie", "Eva", "Sandrine", "Tulya", "Isabelle", "Jocelyne", "Angie", "Amandine", "Christian")
    private val aliases: Array<String> = arrayOf("Christel", "Lorianne", "Pupuce", "Kikobiso", "Aurèle", "ISIS", "Eva 21200", "GlobeCookeuse", "Lila", "Tatazaza", "Jocelyne", "Mrs JONES Angie", "Paradises'Isle", "TAZ'ISLAND")
    private val buissons: Array<Int> = arrayOf(RED, ANY, ANY, BLUE, ANY, ANY, BLUE, ANY, GREEN, ANY, GREEN, ANY, ANY)
    private var txtIds: Array<Int> = arrayOf(R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9, R.id.textView10, R.id.textView11, R.id.textView12, R.id.textView13)
    private var isPopupOpened: Boolean = false

    private fun cycleUserArray(arr: Array<Int>, i: Int): Array<Int> {
        val neutralDay = users.size
        if (i % neutralDay == 0) return Array(buissons.size) { ANY }

        val index = i % neutralDay - 1
        return arr.sliceArray(arr.count()-index until arr.count()) + arr.sliceArray(0 until arr.count()-index)
    }
    private fun cycleDayArray(arr: Array<String>, i: String): Array<String> {
        return arr.sliceArray(arr.indexOf(i) + 1 until arr.count()) + arr.sliceArray(0 until arr.indexOf(i))
    }

    private fun formatBuisson(i: Int): String {
        return when(i) {
            0 -> "Rien (ou temporaire)"
            1 -> "Rouge"
            2 -> "Bleu"
            3 -> "Vert"
            else -> ""
        }
    }

    private fun getTextColor(i: Int): Int {
        return ResourcesCompat.getColor(resources, when(i) {
            0 -> R.color.any
            1 -> R.color.red
            2 -> R.color.blue
            3 -> R.color.green
            else -> 0
        }, null)
    }

    private fun selectIdentity() {
        isPopupOpened = true

        AlertDialog.Builder(this)
                .setTitle("Qui êtes-vous ?")
                .setItems(users) { _, which -> currentUser = aliases[which];isPopupOpened = false }
                .show()
    }

    private fun updateDisplay() {
        val today: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        Log.d(null, today.toString())
        val dests = cycleDayArray(aliases, currentUser)
        val buiss = cycleUserArray(buissons, today)

        findViewById<TextView>(R.id.selectedTextView).text = "Utilisateur Selectionné : $currentUser"

        for (i in 0 until dests.count()) {
            val txt: TextView = findViewById(txtIds[i])
            txt.text = "${dests[i]} : ${formatBuisson(buiss[i])}"
            txt.setTextColor(getTextColor(buiss[i]))
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
        if (item.itemId == R.id.user_actionbar) selectIdentity()
        return super.onOptionsItemSelected(item)
    }
}
