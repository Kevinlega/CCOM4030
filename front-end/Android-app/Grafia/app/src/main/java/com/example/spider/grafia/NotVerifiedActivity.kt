// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : NotVerifiedActivity.kt
// Description : Verifies user account
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
import kotlinx.android.synthetic.main.activity_not_verified.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class NotVerifiedActivity : AppCompatActivity() {

    // Method to show an alert dialog with yes, no and cancel button
    private fun showInternetNotification(mContext: Context){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(mContext)

        // Set a title for alert dialog
        builder.setTitle("Lost Internet Connection.")

        // Set a message for alert dialog
        builder.setMessage("Do you want to return home or retry?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {

                    val intent = Intent(mContext, LoginActivity::class.java)
                    intent.putExtra("Failed",true)
                    mContext.startActivity(intent)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                    startActivity(intent)
                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Home",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("Retry",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    // verifies email request to API
    fun verify(email: String){
        if (isNetworkAvailable()) {
            val query = 4
            val connectToAPI = Connect(this@NotVerifiedActivity)
            try {
                val url = "http://54.81.239.120/updateAPI.php?queryType=$query&email=$email"
                println(url)
                connectToAPI.execute(url)
            } catch (error: Exception) {
            }
        } else {
            showInternetNotification(this@NotVerifiedActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_verified)

        supportActionBar!!.setTitle("Account Verification")

        val Email = findViewById<EditText>(R.id.emailVerify)
        // triggers verify
        Verify.setOnClickListener {

            val email = Email.text.toString()
            if (!email.isNullOrBlank()) {
                verify(email)
            }
        }

        backToLogin.setOnClickListener {
            val intent = Intent(this@NotVerifiedActivity, LoginActivity::class.java)
            finish()
            intent.putExtra("Failed",true)
            startActivity(intent)
        }
    }

    // verifies account
    companion object {
        class Connect(private val mContext: Context) :
            AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg p0: String?): String {
                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            override fun onPostExecute(result: String) {
                try {
                    val jSONObject = JSONObject(result)
                    println(jSONObject)


                    val update = jSONObject.getBoolean("updated")

                    if (update) {

                        val intent = Intent(mContext, LoginActivity::class.java)
                        mContext.startActivity(intent)

                    } else {
                        Toast.makeText(mContext, "Something went wrong, try again.", Toast.LENGTH_LONG).show()
                    }
                } catch (error: Exception) {}
                super.onPostExecute(result)
            }
        }
    }
}
