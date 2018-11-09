package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_friends.*
import kotlinx.android.synthetic.main.activity_project.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class FriendsActivity : AppCompatActivity() {

    var selectedEmails: MutableList<String> = ArrayList()
    var FilteredNames = JSONArray()
    var FilteredEmail = JSONArray()
    var userId = -1
//    val listView = findViewById<ListView>(R.id.listaFriends)
//    var downloadData = ConnectFriends(this,-1,listView,selectedEmails)


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_search -> {
                supportActionBar!!.setTitle("Add Friends")
                ButtonAction.visibility = View.INVISIBLE
                ButtonAction2.visibility = View.VISIBLE
                ButtonAction2.setText("Send Request")


                // Below is this view actions

                ButtonAction2.setOnClickListener {
                    Toast.makeText(this, "Request Sent.", Toast.LENGTH_SHORT).show()
                }

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pending -> {
                supportActionBar!!.setTitle("Pending Requests")
                ButtonAction.visibility = View.VISIBLE
                ButtonAction2.visibility = View.VISIBLE
                ButtonAction.setText("Accept Request")
                ButtonAction2.setText("Reject Request")
                pendingRequest()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_friends -> {
                supportActionBar!!.setTitle("All Friends")
                ButtonAction.visibility = View.INVISIBLE
                ButtonAction2.visibility = View.INVISIBLE

                // Below is this view actions
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        userId = intent.getIntExtra("userId",-1)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_friends
        navigation.selectedItemId = R.id.navigation_search

        Return.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("userId", userId)
            // start your next activity
            startActivity(intent)
        }
    }

    fun pendingRequest(){
        // Below is this view actions
        val listView = findViewById<ListView>(R.id.listaFriends)

        val downloadData = ConnectFriends(this,1,listView,selectedEmails)
        try
        {
            val url = "http://54.81.239.120/selectAPI.php?queryType=7&uid=$userId"
            downloadData.execute(url)

        }catch (e: Exception)
        {
            println(e.message)
        }

        val search = findViewById<SearchView>(R.id.searchBarFriends)

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var empty: Boolean
                if (downloadData.names.length() > 0) {
                    if (newText!!.isNotEmpty()) {
                        empty = true
                        val search = newText.toLowerCase()
                        for (i in 0..(downloadData.names.length()-1)) {
                            if ((downloadData.names[i] as String).toLowerCase().contains(search)) {
                                var found = false
                                for(j in 0..(FilteredEmail.length()-1)){
                                    if(FilteredEmail[j] == downloadData.emails[i]){
                                        found = true
                                        empty = false
                                    }
                                }
                                if(!found){
                                    empty = false
                                    FilteredNames.put(downloadData.names[i] as String)
                                    FilteredEmail.put(downloadData.emails[i] as String)
                                }
                            }
                        }

                        if(empty){
                            FilteredNames = JSONArray()
                            FilteredEmail = JSONArray()
                        }

                    } else {
                        FilteredNames = downloadData.names
                        FilteredEmail = downloadData.emails
                    }
                    listView.adapter = ListFriendAdapter(this@FriendsActivity,FilteredNames, FilteredEmail,selectedEmails)
                }
                return true
            }
        })

        ButtonAction.setOnClickListener {
            if(selectedEmails.size > 0){

                for (i in 0..(selectedEmails.size-1)){
                    try
                    {
                                val insertData = ConnectFriends(this,2,listView, selectedEmails)

                                val email = selectedEmails.get(i)

                                val url = "http://54.81.239.120/updateAPI.php?queryType=2&uid=$userId&email=$email"
                                insertData.execute(url)
                    }catch (e: Exception)
                    {
                        println(e.message)
                    }
                }

                Toast.makeText(this, "Accepted Request.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No Request to Accept.", Toast.LENGTH_SHORT).show()
            }

        }

        ButtonAction2.setOnClickListener {
            if(selectedEmails.size > 0) {
                Toast.makeText(this, "Rejected Request.", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "No Request to Reject.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class ListFriendAdapter(context: Context, names: JSONArray, emails: JSONArray, selectedEmail: MutableList<String>) : BaseAdapter() {

        private val mContext: Context
        private val namesArray: JSONArray
        private val emailsArray: JSONArray
        private val selected: MutableList<Int> = ArrayList()
        val selectedEmails = selectedEmail

        init {
            mContext = context
            namesArray = names
            emailsArray = emails
        }

        override fun getCount(): Int {
            return namesArray.length()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return emailsArray.get(position)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.list_row, parent, false)

            row.setOnClickListener(object : View.OnClickListener {

                override fun onClick(v: View?) {
                    //use getItem(position) to get the item

                    if (selected.contains(position)){
                        val index = selected.indexOf(position)
                        selectedEmails.removeAt(index)
                        selected.removeAt(index)

                        row.setBackgroundColor(Color.parseColor("#c0c0c0"))
                    }
                    else{
                        selected.add(position)
                        selectedEmails.add(emailsArray.get(position) as String)
                        row.setBackgroundColor(Color.rgb(173,173,173))
                    }
                }
            })

            val PersonName= row.findViewById<TextView>(R.id.PersonName)
            PersonName.text = (namesArray.get(position)) as CharSequence


            val PersonEmail = row.findViewById<TextView>(R.id.PersonEmail)
            PersonEmail.text = (emailsArray.get(position)) as CharSequence

            return row

        }
    }

    companion object {
        class ConnectFriends(context: Context, queryType: Int, listView: ListView, selectedEmail: MutableList<String>) :
            AsyncTask<String, Void, String>() {

            private val mContext: Context
            private val list: ListView
            val selectedEmails = selectedEmail
            val type = queryType

            init {
                mContext = context
                list = listView
            }

            var names = JSONArray()
            var emails = JSONArray()

            override fun doInBackground(vararg p0: String?): String {

                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            override fun onPostExecute(result: String?) {

                try {
                    val jSONObject = JSONObject(result)

                    if (type == 1) {


                        var empty = jSONObject.getBoolean("empty")

                        if (!empty) {

                            names = jSONObject.getJSONArray("name")
                            emails = jSONObject.getJSONArray("email")

                            list.adapter = ListFriendAdapter(mContext, names, emails, selectedEmails)

                        }
                    } else if(type == 2){
                        var updated = jSONObject.getBoolean("updated")
                        if (updated) {
                            println("Success")
                        } else{
                            println("Failed")
                        }

                    }
                } catch (e: Exception) {
                    println(e.message)
                }
                super.onPostExecute(result)
            }
        }
    }
}
