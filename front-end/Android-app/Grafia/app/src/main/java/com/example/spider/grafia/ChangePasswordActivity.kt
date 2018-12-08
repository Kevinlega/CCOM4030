// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : ChangePassword.kt
// Description : Allows user to change password
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
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_change_password.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.security.MessageDigest

class ChangePasswordActivity : AppCompatActivity() {


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

    // Is user registered request
    private fun isRegistered(email:String, answer: String){
        val query = 0
        val connectToAPI = Connect(this, 0,email,answer, intent)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
            connectToAPI.execute(url)
        }
        catch (error: Exception){}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar!!.setTitle("Change Password")

        // text boxes
        val Email = findViewById<EditText>(R.id.emailChange)
        val Answer = findViewById<EditText>(R.id.passwordChange)


        ChangePassword.setOnClickListener {
            if (isNetworkAvailable(this@ChangePasswordActivity)) {
                // Fetch text from activity text boxes
                val email = Email.text.toString()
                val answer = Answer.text.toString()
                if (!email.isNullOrBlank() && !answer.isNullOrBlank()) {
                    isRegistered(email, answer)
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "Fields are required.", Toast.LENGTH_LONG).show()
                }
            } else {
                showInternetNotification(this@ChangePasswordActivity,intent)
            }
        }
    }

    // Connects to API to check if user is registered,
    // if registered changes the password for such user
    companion object {
        class Connect(private val mContext: Context, private val flag: Int, private val email: String, private val answer: String,private val intent: Intent) :
            AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg p0: String?): String {
                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            // Get Response
            override fun onPostExecute(result: String) {
                try {
                    val jSONObject = JSONObject(result)
                    println(jSONObject)

                    if(flag == 0){
                        val registered = jSONObject.getBoolean("registered")

                        if (registered) {
                            if (isNetworkAvailable(mContext)) {
                                val query = 4
                                val connectToAPI = Connect(mContext, 1, email, answer, intent)
                                try {
                                    val url =
                                        "http://54.81.239.120/insertAPI.php?queryType=$query&email=$email&answer=$answer"
                                    connectToAPI.execute(url)
                                } catch (error: Exception) {
                                }
                            } else{
                                showInternetNotification(mContext,intent)
                            }

                        } else {
                            Toast.makeText(mContext, "Something went wrong cannot send change password request, try again.", Toast.LENGTH_LONG).show()
                        }
                    } else{

                        val update = jSONObject.getBoolean("inserted")
                        if (update) {
                            val intent = Intent(mContext, LoginActivity::class.java)
                            mContext.startActivity(intent)
                            Toast.makeText(mContext, "Sent change password request.", Toast.LENGTH_LONG).show()
                        } else{
                            Toast.makeText(mContext, "Something went wrong cannot send change password request, try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (error: Exception) {}
                super.onPostExecute(result)
            }

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
                            mContext.startActivity(intent)
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


        }
    }
}
