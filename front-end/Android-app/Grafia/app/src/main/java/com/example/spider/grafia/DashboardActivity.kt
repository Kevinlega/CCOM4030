// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : DashboardActivity.kt
// Description : Displays a project hub for user
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dashboard.*
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.GridLayout.VERTICAL
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL



class DashboardActivity : AppCompatActivity() {

    // Method to show an alert dialog with yes, no and cancel button
    private fun showInternetNotification(mContext: Context, intent: Intent){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(mContext)
        // Set a title for alert dialog
        builder.setTitle("Lost Internet Connection.")

        // Set a message for alert dialog
        builder.setMessage("Do you want to log out or retry?")

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {

                    val Logout = Intent(mContext, LoginActivity::class.java)
                    Logout.putExtra("Failed",true)
                    mContext.startActivity(Logout)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                    startActivity(intent)
                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Log Out",dialogClickListener)
        // Set the alert dialog negative/no button
        builder.setNegativeButton("Retry",dialogClickListener)
        // Initialize the AlertDialog using builder object
        dialog = builder.create()
        // Finally, display the alert dialog
        dialog.show()
    }

    private fun isNetworkAvailable(mContext: Context): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar!!.setTitle("Dashboard")

        // Data from previous Activity
        val userId = intent.getIntExtra("userId", -1)

        // To display projects
        val rview = findViewById<View>(R.id.rview) as RecyclerView
        val lManager = GridLayoutManager(this, 2, VERTICAL, false)
        rview.layoutManager = lManager

        // Connect to API and retrieve projects for the userId
        val downloadData = Connect(this@DashboardActivity, rview, userId)

        if (isNetworkAvailable(this@DashboardActivity)) {
            try {
                val url = "http://54.81.239.120/selectAPI.php?queryType=3&uid=$userId"
                downloadData.execute(url)

            } catch (e: Exception) {
                println(e.message)
            }
        } else {
            showInternetNotification(this@DashboardActivity,intent)
        }

        // Segue Handlers
        logout.setOnClickListener {
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            // start your next activity
            finish()
            startActivity(intent)
        }

        CreateProject.setOnClickListener {
            val intent = Intent(this@DashboardActivity, CreateProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            // start your next activity
            startActivity(intent)
        }

        Friends.setOnClickListener {
            val intent = Intent(this@DashboardActivity, FriendsActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            // Start your next activity
            startActivity(intent)
        }
    }

    // Connect to API and get every project that the current user participates in.
    companion object {
        class Connect(context: Context, recyclerView: RecyclerView, private val userId : Int): AsyncTask<String, Void, String>(){
            var names = JSONArray()
            var projects = JSONArray()
            val mContext = context
            val rview = recyclerView

            override fun doInBackground(vararg p0: String?): String{
                return downloadJSON(p0[0])

            }
            private fun downloadJSON(url: String?): String{
                return URL(url).readText()
            }

            // Get Response
            override fun onPostExecute(result: String?){
                try{
                    val jSONObject = JSONObject(result)

                    var empty = jSONObject.getBoolean("empty")

                    if (!empty) {

                        names = jSONObject.getJSONArray("project_name")
                        projects = jSONObject.getJSONArray("project_id")

                        val project : MutableList<Int> = ArrayList()

                        for(i in 0..projects.length()-1){
                            project.add(i,R.drawable.folder_icon)
                        }

                        rview.adapter = DashboardAdapter(project, names,projects,mContext,userId)
                    }
                }
                catch (error: Exception){}
                super.onPostExecute(result)
            }
        }
    }
}
