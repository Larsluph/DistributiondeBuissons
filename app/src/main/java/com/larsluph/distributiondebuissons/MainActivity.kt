package com.larsluph.distributiondebuissons

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.larsluph.distributiondebuissons.Colors.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var currentUser: User? = null
        set(value) {
            field = value
            updateDisplay()
        }
    private var today: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        set(value) {
            field = value
            updateDisplay()
        }
    private var isLogicReverted: Boolean = false
        set(value) {
            field = value
            updateDisplay()
        }
    private var lastColor: Colors? = null

    private val users: Array<User> = arrayOf(
        User("Christel", "Christel"),
        User("Lorianne", "Lorianne"),
        User("Doro", "Pupuce"),
        User("Aisha", "Kikobiso"),
        User("Aurèle", "Aurèle"),
        User("Claudie", "ISIS"),
        User("Marie-Laurence", "Guizmo"),
        User("Sandrine", "GlobeCookeuse"),
        User("Tulya", "Lila"),
        User("Isabelle", "Tatazaza"),
        User("Jocelyne", "Jocelyne"),
        User("Angie", "Mrs JONES Angie"),
        User("Amandine", "Paradises'Isle"),
        User("Christian", "TAZ'ISLAND"),
        User("Sam", "Sam")
    )
    private val buissons: Array<Colors> = arrayOf(PURPLE, ANY, ANY, ORANGE, ANY, ANY, GREEN, ANY, ANY, ORANGE, ANY, GREEN, ANY, ANY)
    private var isPopupOpened: Boolean = false
    private val neutralDay = users.size

    private fun cycleColorArray(arr: Array<Colors>, i: Int): Array<Colors> {
        if (i % neutralDay == 0 || i == 31) return Array(buissons.size) { ANY }

        val index = i % neutralDay - 1
        return arr.sliceArray(arr.count()-index until arr.count()) + arr.sliceArray(0 until arr.count()-index)
    }

    private fun cycleUserArray(arr: Array<User>, i: User): Array<User> {
        return arr.sliceArray(arr.indexOf(i) + 1 until arr.count()) + arr.sliceArray(0 until arr.indexOf(i))
    }

    private fun selectIdentity() {
        isPopupOpened = true

        val names = (users.map { it.name }).toTypedArray()

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.popup_user))
                .setItems(names) { _, which ->
                    run {
                        currentUser = users[which]
                        isPopupOpened = false
                    }
                }
                .show()
    }

    private fun updateDisplay() {
        Log.d(null, today.toString())

        findViewById<TextView>(R.id.selectedTextView).text = "Jour $today : ${currentUser!!.pseudo}"
        findViewById<TextView>(R.id.modeTextView).text = if (isLogicReverted) getString(R.string.text_toggle2) else getString(R.string.text_toggle1)

        findViewById<TextView>(R.id.textViewPurple).text = ""
        findViewById<TextView>(R.id.textViewGreen).text = ""
        findViewById<TextView>(R.id.textViewOrange).text = ""
        findViewById<TextView>(R.id.textViewAny).text = ""

        val buiss = cycleColorArray(buissons, today)
        val userMap: Map<User, Colors>

        if (isLogicReverted) {
            // Reception
            val others = cycleUserArray(users, currentUser!!)
            userMap = mutableMapOf()

            for (user in others) {
                val dests = cycleUserArray(users, user)
                val otherMap: Map<User, Colors> = (dests zip buiss).associate { it.first to it.second }
                userMap[user] = otherMap[currentUser]!!
            }
        }
        else {
            // Distribution
            val dests = cycleUserArray(users, currentUser!!)

            userMap = (dests zip buiss).associate { it.first to it.second }
        }

        renderUserMap(userMap)
    }

    private fun renderUserMap(userMap: Map<User, Colors>) {
        for ((dest, buis) in userMap.entries) {
            val pseudo = dest.pseudo

            val txt: TextView = findViewById(
                when (buis) {
                    PURPLE -> R.id.textViewPurple
                    GREEN -> R.id.textViewGreen
                    ORANGE -> R.id.textViewOrange
                    else -> R.id.textViewAny
                }
            )

            val newlineCount = when {
                txt.text == "" -> 0
                lastColor != buis && buis == ANY -> 2
                else -> 1
            }

            txt.text = txt.text.toString() + "\n".repeat(newlineCount) + pseudo

            lastColor = buis
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (currentUser == null && !isPopupOpened) selectIdentity()
    }

    override fun onResume() {
        super.onResume()
        if (currentUser == null && !isPopupOpened) {
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
            R.id.minus_actionbar -> today = if (today <= 1) 1 else today-1
            R.id.plus_actionbar -> today = if (today >= maxMonthDay) maxMonthDay else today+1
            R.id.reset_actionbar -> today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            R.id.user_actionbar -> selectIdentity()
            R.id.toggle_actionbar -> isLogicReverted = !isLogicReverted
        }
        return true
    }
}
