// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : CreateProjectActivity.kt
// Description : Allows user to create a project
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_project.*
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class CreateProjectActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_create_project)

        supportActionBar!!.setTitle("Create Project")

        val userId = intent.getIntExtra("userId",-1)


        DontCreateProject.setOnClickListener {
            val intent = Intent(this@CreateProjectActivity, DashboardActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }

        CreateProject.setOnClickListener {

            Log.i("CreateProjectActivity", "Button Pressed.")
            var name = Name.text.toString()
            var location = Location.text.toString()
            var description = Description.text.toString()
            // Input validation
            if(name == "" || location == "" || description == "") {
                Log.i("CreateProjectActivity", "Empty input")
                Toast.makeText(this, "All fields are requiered.", Toast.LENGTH_SHORT).show()
            } else {

                // Connect to API
                val downloadData = Download(this,userId,name)
                if(isNetworkAvailable(this@CreateProjectActivity)) {
                    try {
                        var reqParam = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                        reqParam += "&location=" + URLEncoder.encode(location, "UTF-8")
                        reqParam += "&description=" + URLEncoder.encode(description, "UTF-8")
                        reqParam += "&user_id=" + URLEncoder.encode(userId.toString(), "UTF-8")
                        val url = "http://54.81.239.120/insertAPI.php?queryType=2&$reqParam"
                        Log.i("CreateProjectActivity", "URL: $url")

                        println(url)
                        downloadData.execute(url)

                    } catch (e: Exception) {
                        println(e.message)
                    }
                } else {
                    showInternetNotification(this@CreateProjectActivity,intent)
                }
            }
        }
    }


    // Download class that connects to API to insert project into database and retrieve project id and trigger activity segue
    companion object {
        class Download(private val mContext: Context, private val userId: Int, private val Name: String) : AsyncTask<String, Void, String>(){
            var projectId = -1

            override fun doInBackground(vararg p0: String?): String {

                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String
            {
                return URL(url).readText()
            }

            // Retrieve JSON file
            override fun onPostExecute(result: String?) {

                try
                {
                    val jSONObject = JSONObject(result)

                    val created = jSONObject.getBoolean("created")

                    if(created){
                        projectId = (jSONObject.getString("project_id")).toInt()

                        Toast.makeText(mContext, "Project Created.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(mContext, ProjectActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("pId", projectId)
                        intent.putExtra("projectName",Name)

                        mContext.startActivity(intent)

                    } else {
                        Toast.makeText(mContext, "Project Not Created.", Toast.LENGTH_SHORT).show()
                    }

                }catch (e: Exception){
                    println(e.message)
                }
                super.onPostExecute(result)
            }
        }
    }
}