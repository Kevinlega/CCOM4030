// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : AddParticipantActivity.kt
// Description : Activity that allows user to add other users to project
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_participants.*
import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import android.view.*
import android.widget.*


class AddParticipantsActivity : AppCompatActivity() {

    // Initiating globals
    var selectedEmails: MutableList<String> = ArrayList()
    var FilteredNames = JSONArray()
    var FilteredEmail = JSONArray()
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_participants)

        supportActionBar!!.setTitle("Add Participants")

        // Retrieve data from another view
        val userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)
        name = intent.getStringExtra("projectName")


        // Local variables of view and view triggers
        val listView = findViewById<ListView>(R.id.listView)
        val mContext = this

        // Connect to API
        val downloadData = Connect(this,0,listView, selectedEmails)

        try
        {
            val url = "http://54.81.239.120/selectAPI.php?queryType=1&pid=$projectId&uid=$userId"
            downloadData.execute(url)

        }catch (e: Exception)
        {
            println(e.message)
        }

        // Search bar
        val search = findViewById<SearchView>(R.id.searchBarFriends)

        // Search bar listener for text change
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
                    listView.adapter = ListViewAdapter(mContext,FilteredNames , FilteredEmail,selectedEmails )
                }
                return true
            }
            })

        // Segue triggers
        BackToProject.setOnClickListener {
                val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
                // To pass any data to next activity
                intent.putExtra("userId", userId)
                intent.putExtra("pId",projectId)
                intent.putExtra("projectName",name)
                // start your next activity
                startActivity(intent)
            }

        AddParticipants.setOnClickListener {
            val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)

            if(downloadData.selectedEmails.size > 0){
                // Insert n selected users into project
                for (i in 0..(selectedEmails.size-1)){
                    try
                    {
                        println(i)
                        val insertData = Connect(this,1,listView, selectedEmails)
                        val email = selectedEmails.get(i)

                        val url = "http://54.81.239.120/insertAPI.php?queryType=1&pid=$projectId&email=$email"
                        insertData.execute(url)
                    }catch (e: Exception)
                    {
                        println(e.message)
                    }
                }
                // To pass any data to next activity
                intent.putExtra("userId", userId)
                intent.putExtra("pId",projectId)
                intent.putExtra("projectName",name)

                // start your next activity
                Toast.makeText(this, "Participants added.", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "No participants selected.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // Display users to add in project.
    private class ListViewAdapter(context: Context, names: JSONArray,emails: JSONArray, selectedEmail: MutableList<String>) : BaseAdapter() {

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

    // Connect class that sends request to server, to insert another user to project.
    companion object {
        class Connect(context: Context, queryType: Int,listView: ListView, selectedEmail: MutableList<String>) : AsyncTask<String, Void, String>(){

            private val mContext: Context
            private val list: ListView
            val selectedEmails = selectedEmail
            val type = queryType

            init {
                mContext = context
                list = listView
            }

            var names = JSONArray()
            var emails =  JSONArray()

            override fun doInBackground(vararg p0: String?): String {

                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            // Get Response
            override fun onPostExecute(result: String?) {

                try {
                    val jSONObject = JSONObject(result)

                    if (type == 0) {


                        var empty = jSONObject.getBoolean("empty")

                        if (!empty) {

                            names = jSONObject.getJSONArray("names")
                            emails = jSONObject.getJSONArray("emails")

                            list.adapter = ListViewAdapter(mContext, names, emails, selectedEmails)

                            }
                        }
                    else{
                        var reg = jSONObject.getBoolean("registered")
                        if (reg){
                            println("Success")
                        }
                        else{
                            println("bad")
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