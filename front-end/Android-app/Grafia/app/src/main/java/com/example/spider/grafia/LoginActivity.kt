// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : LoginActivity.kt
// Description : Login with email and password
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.security.*
import android.net.ConnectivityManager



class LoginActivity : AppCompatActivity() {

    // Method to show an alert dialog with yes, no and cancel button
    private fun showInternetNotification(mContext: Context){
        // Late initialize an alert dialog object
        lateinit var dialog:AlertDialog


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar!!.setTitle("Grafía Login")

        var failed = false
        try {
            failed = intent.getBooleanExtra("Failed",false)
        } catch (error: Exception){}

        if (!failed) {
            val sharedPreferences = getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("Email", "")
            val password = sharedPreferences.getString("Password", "")

            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
                val intent = Intent(this@LoginActivity, FingerprintActivity::class.java)
                startActivity(intent)
            }
        }

        // Trigger fingerprint
        fingerprint.setOnClickListener {
            val sharedPreferences = getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("Email", "")
            val password = sharedPreferences.getString("Password", "")

            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
                val intent = Intent(this@LoginActivity, FingerprintActivity::class.java)
                startActivity(intent)
            } else{
                Toast.makeText(this@LoginActivity,"No credentials stored.", Toast.LENGTH_SHORT).show()
            }
        }


        // Trigger login
        loginButton.setOnClickListener {

            if(isNetworkAvailable()) {
                val Email = findViewById<EditText>(R.id.loginEmail)
                val Password = findViewById<EditText>(R.id.loginPassword)
                val email = Email.text.toString()
                val password = Password.text.toString()
                if (checkLogin(password, email)) {
                    isRegistered(email, password)
                }
            } else{
                showInternetNotification(this@LoginActivity)
            }
        }

        // Segues
        createAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
            startActivity(intent)
        }
        changePassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    // Checks if an email is already registered.
    private fun isRegistered(email:String,password:String){
        val query = 4
        val connectToAPI = Connect(this, 0,email,password,intent)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
            println(url)
            connectToAPI.execute(url)
        }
        catch (error: Exception){}
    }

    // Check if can Login
    private fun checkLogin(password:String, email:String): Boolean{
        var canLogin = true
        if(password.isNullOrBlank() || email.isNullOrBlank()){
            canLogin = false
            Toast.makeText(this, "All Fields are Required.", Toast.LENGTH_LONG).show()
        }

        return canLogin
    }

    // Verifies Login data
    companion object {
        class Connect(private val mContext: Context,private val flag: Int, private val email: String, private val password: String,private val intent: Intent) :
            AsyncTask<String, Void, String>() {

            // Send uid retrieve request
            private fun getUID(email:String,tryPassword: String){
                if (isNetworkAvailable()) {
                    val query = 2
                    val connectToAPI = LoginActivity.Companion.Connect(mContext, 1, email, tryPassword, intent)
                    try {
                        val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
                        println(url)
                        connectToAPI.execute(url)
                    } catch (error: Exception) {
                    }
                } else {
                    showInternetNotification()
                }
            }
            // Byte Array To String
            private fun byteArrayToHexString(array: Array<Byte>): String {

                var result = StringBuilder(array.size * 2)

                for (byte in array) {
                    val toAppend = String.format("%2X", byte).replace(" ", "0")
                    result.append(toAppend)
                }
                result.setLength(result.length)

                return result.toString()
            }

            // gets md5 of String
            private fun md5(data: String): String {

                var result = ""

                try{

                    val md5 = MessageDigest.getInstance("MD5")
                    val md5HashBytes = md5.digest(data.toByteArray()).toTypedArray()

                    result = byteArrayToHexString(md5HashBytes)

                } catch (e: Exception) {
                }

                return result
            }

            override fun doInBackground(vararg p0: String?): String {
                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            // Gets data and if its correct, retrieves the user id
            override fun onPostExecute(result: String) {
                try {
                    val jSONObject = JSONObject(result)
                    println(jSONObject)

                    if(flag == 0){
                        val registered = jSONObject.getBoolean("empty")

                        if (!registered) {

                            val dbPassword = jSONObject.getString("hashed_password")
                            val dbSalt = jSONObject.getString("salt")

                            val tryPassword = saltAndHash(password,dbSalt)
                            if (tryPassword == dbPassword) {
                                getUID(email,tryPassword)
                            } else{
                                Toast.makeText(mContext, "Incorrect login info, try again.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(mContext, "Incorrect login info, try again.", Toast.LENGTH_SHORT).show()
                        }
                    } else{
                        val uid = jSONObject.getInt("uid")
                        val verified = jSONObject.getInt("verified")

                        if (verified == 1) {

                            val sharedPreferences = mContext.getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
                            val em = sharedPreferences.getString("Email", "")
                            val pw = sharedPreferences.getString("Password", "")

                            if (email != em ||password != pw){
                                showDialog(mContext, email, password, uid)
                            } else {
                                val intent = Intent(mContext, DashboardActivity::class.java)
                                intent.putExtra("userId", uid)
                                mContext.startActivity(intent)
                            }
                        } else{
                            val intent = Intent(mContext, NotVerifiedActivity::class.java)
                            mContext.startActivity(intent)
                        }
                    }

                } catch (error: Exception) {
                }
                super.onPostExecute(result)
            }

            // Generates a salt and hashes a function
            private fun saltAndHash(password:String,salt:String): String{
                val salted = password + salt
                return md5(salted).toLowerCase()
            }

            // Method to show an alert dialog with yes, no and cancel button
            private fun showDialog(mContext: Context,email: String,password: String,uid: Int){
                // Late initialize an alert dialog object
                lateinit var dialog:AlertDialog


                // Initialize a new instance of alert dialog builder object
                val builder = AlertDialog.Builder(mContext)

                // Set a title for alert dialog
                builder.setTitle("Fingerprint Update.")

                // Set a message for alert dialog
                builder.setMessage("Do you want to update the credentials stored?")


                // On click listener for dialog buttons
                val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                    when(which){
                        DialogInterface.BUTTON_POSITIVE -> {
                            val sharedPreferences = mContext.getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.putString("Email", email)
                            editor.putString("Password", password)
                            editor.apply()

                            val intent = Intent(mContext, DashboardActivity::class.java)
                            intent.putExtra("userId", uid)
                            mContext.startActivity(intent)
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                            val intent = Intent(mContext, DashboardActivity::class.java)
                            intent.putExtra("userId", uid)
                            mContext.startActivity(intent)
                        }
                        DialogInterface.BUTTON_NEUTRAL -> {
                            val intent = Intent(mContext, DashboardActivity::class.java)
                            intent.putExtra("userId", uid)
                            mContext.startActivity(intent)
                        }
                    }
                }


                // Set the alert dialog positive/yes button
                builder.setPositiveButton("YES",dialogClickListener)

                // Set the alert dialog negative/no button
                builder.setNegativeButton("NO",dialogClickListener)

                // Set the alert dialog neutral/cancel button
                builder.setNeutralButton("CANCEL",dialogClickListener)


                // Initialize the AlertDialog using builder object
                dialog = builder.create()

                // Finally, display the alert dialog
                dialog.show()
            }

            // Method to show an alert dialog with yes, no and cancel button
            private fun showInternetNotification(){
                // Late initialize an alert dialog object
                lateinit var dialog:AlertDialog


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
                builder.setPositiveButton("Home",dialogClickListener)

                // Set the alert dialog negative/no button
                builder.setNegativeButton("Retry",dialogClickListener)

                // Initialize the AlertDialog using builder object
                dialog = builder.create()

                // Finally, display the alert dialog
                dialog.show()
            }

            private fun isNetworkAvailable(): Boolean {
                val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }
    }
}
