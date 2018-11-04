package com.example.spider.grafia

import android.app.Person
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_participants.*


import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import android.graphics.Color.parseColor



class AddParticipantsActivity : AppCompatActivity() {

    var selectedEmails: MutableList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_participants)


        val listView = findViewById<ListView>(R.id.listView)

        // retrieve data from another view

        val userId = intent.getStringExtra("userId")
        val projectId  = intent.getStringExtra("projectId")


        val downloadData = Connect(this,0,listView, selectedEmails)

        try
        {
            val url = "http://54.81.239.120/selectAPI.php?queryType=1&pid=$projectId&uid=$userId"
            downloadData.execute(url)

        }catch (e: Exception)
        {
            println(e.message)
        }

        // Segue trigger
        BackToProject.setOnClickListener {
                val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
                // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
                // start your next activity
                startActivity(intent)
            }

        AddParticipants.setOnClickListener {
            val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
            // start your next activity
            if(downloadData.selectedEmails.size > 0){

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
                startActivity(intent)
            }
            else{
                println("bye")
            }

        }
    }

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

            var names =  JSONArray()
            var emails =  JSONArray()
            override fun doInBackground(vararg p0: String?): String {

                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String
            {
                return URL(url).readText()
            }

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