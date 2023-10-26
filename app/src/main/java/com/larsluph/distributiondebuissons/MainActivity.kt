package com.larsluph.distributiondebuissons

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.larsluph.distributiondebuissons.Colors.ANY
import com.larsluph.distributiondebuissons.Colors.GREEN
import com.larsluph.distributiondebuissons.Colors.ORANGE
import com.larsluph.distributiondebuissons.Colors.PURPLE
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var menu: Menu

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

    private var users: Array<User> = emptyArray()
    private val buissons: Array<Colors> = arrayOf(PURPLE, ANY, ORANGE, ANY, ANY, GREEN, ANY, ORANGE, ANY, ANY, GREEN, ANY, ANY)
    private var isPopupOpened: Boolean = false
    private val neutralDay = 15
    private lateinit var queue: RequestQueue

    private fun cycleColorArray(arr: Array<Colors>, i: Int): Array<Colors> {
        if (i % neutralDay == 0 || i == 31) return Array(buissons.size) { ANY }

        val index = (i % neutralDay - 1) % arr.count()
        return arr.sliceArray(arr.count()-index until arr.count()) + arr.sliceArray(0 until arr.count()-index)
    }

    private fun cycleUserArray(arr: Array<User>, i: User): Array<User> {
        return arr.sliceArray(arr.indexOf(i) + 1 until arr.count()) + arr.sliceArray(0 until arr.indexOf(i))
    }

    private fun selectIdentity() {
        if (isPopupOpened) return

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

        findViewById<TextView>(R.id.selectedTextView).text = getString(R.string.title_template, today, currentUser!!.pseudo)
        findViewById<TextView>(R.id.modeTextView).text = if (isLogicReverted) getString(R.string.text_toggle2) else getString(R.string.text_toggle1)
        menu.findItem(R.id.toggle_actionbar).title = if (isLogicReverted) getString(R.string.actionbar_toggle1) else getString(R.string.actionbar_toggle2)

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

            @SuppressLint("SetTextI18n")
            txt.text = txt.text.toString() + "\n".repeat(newlineCount) + pseudo

            lastColor = buis
        }
    }

    private fun updateUserList() {
        val url = "https://gist.githubusercontent.com/Larsluph/6f9491535d7717d23b5a6f7d07cf1132/raw/data_buissons.json"

        val request = JsonArrayRequest(Method.GET, url, null,
            { response ->
                users = Array(response.length()) { i ->
                    val user = response.getJSONObject(i)
                    User(user)
                }
                saveData()
                Toast.makeText(this, "Users list updated!", Toast.LENGTH_SHORT).show()
                selectIdentity()
            },
            { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() })

        queue.add(request)
    }

    private fun loadData(): Boolean {
        val sp = getSharedPreferences(getString(R.string.packagename), MODE_PRIVATE)
        val storeData = sp.getString(getString(R.string.users_key), "")
        if (storeData.equals("")) {
            return false
        }

        users = Gson().fromJson(storeData, object : TypeToken<Array<User>>() {}.type)
        return true
    }

    private fun saveData() {
        val sp = getSharedPreferences(getString(R.string.packagename), MODE_PRIVATE).edit()
        sp.putString(getString(R.string.users_key), Gson().toJson(users))
        sp.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        queue = Volley.newRequestQueue(this)
    }

    override fun onResume() {
        super.onResume()

        if (!loadData()) updateUserList()
        else if (currentUser == null) selectIdentity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        if (menu != null) {
            this.menu = menu
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val calendar: Calendar = Calendar.getInstance()

        when (item.itemId) {
            R.id.minus_actionbar -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val maxPrevMonthDay = calendar.get(Calendar.DAY_OF_MONTH)
                today = if (today <= 1) maxPrevMonthDay else today -1
            }
            R.id.plus_actionbar -> {
                calendar.roll(Calendar.MONTH, true)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val maxMonthDay = calendar.get(Calendar.DAY_OF_MONTH)
                today = if (today >= maxMonthDay) 1 else today + 1
            }
            R.id.reset_actionbar -> today = calendar.get(Calendar.DAY_OF_MONTH)
            R.id.user_actionbar -> selectIdentity()
            R.id.toggle_actionbar -> isLogicReverted = !isLogicReverted
            R.id.refresh_actionbar -> updateUserList()
        }
        return true
    }
}
