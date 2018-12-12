// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : CreateAccountActivity.kt
// Description : Allows user to create an account
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_create_account.*
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import org.json.JSONObject
import java.net.URL
import java.lang.StringBuilder
import java.security.*
import android.widget.Toast

open class CreateAccountActivity : AppCompatActivity(){



    // Method to show an alert dialog with yes, no and cancel button
    private fun showInternetNotification(mContext: Context, intent: Intent){
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
        builder.setPositiveButton("Home",dialogClickListener)
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

    // Checks if an email is already registered.
    private fun isRegistered(email:String,name: String,password: String,answer: String) {

        if (isNetworkAvailable(this@CreateAccountActivity)) {
            val query = 0
            val connectToAPI = Connect(this@CreateAccountActivity, 0, name, email, password, answer, intent)
            try {
                val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"

                connectToAPI.execute(url)

            } catch (error: Exception) {
            }
        } else {
            showInternetNotification(this@CreateAccountActivity,intent)
        }
    }

    // Check for empty fields and matching passwords
    private fun checkLogin(name:String, password:String,confirm:String,email:String, answer: String): Boolean{
        var canLogin = true
        if(name.isNullOrBlank() || password.isNullOrBlank() || confirm.isNullOrBlank() || email.isNullOrBlank() || answer.isNullOrBlank()){
            canLogin = false
            Toast.makeText(this, "All Fields are Required.", Toast.LENGTH_LONG).show()
        }

        if(password != confirm){
            canLogin = false
            Toast.makeText(this, "Passwords Do Not Match.", Toast.LENGTH_LONG).show()
        }
        return canLogin
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        supportActionBar!!.setTitle("Create Account")

        // When 'Create Account' button is pressed:
        // 1) Input validation:
        //   a) No empty fields
        //   b) Passwords must match
        // 2) No redundancies:
        //   a) Email must not already be registered
        // 3) If previous conditions are met, insert user in database.
        CreateAccount.setOnClickListener {

            // Get input from user forms.
            val Name = findViewById(R.id.caName) as EditText
            val Email = findViewById(R.id.caEmail) as EditText
            val Password = findViewById(R.id.caPassword) as EditText
            val ConfirmPassword = findViewById(R.id.caConfirmPassword) as EditText
            val answerText = findViewById<EditText>(R.id.Answer)

            val name = Name.text.toString()
            val email = Email.text.toString()
            val password = Password.text.toString()
            val confirm = ConfirmPassword.text.toString()
            val answer = answerText.text.toString()
            if (checkLogin(name, password, confirm, email,answer)) {
                isRegistered(email,name, password, answer)
            }
        }
    }

    // Connect class that checks if user is registered,
    // if not, registers said user.
    companion object {
        class Connect(private val mContext: Context, private val type : Int, private val name: String,private val email: String,private val password: String,private val answer: String, private val intent: Intent): AsyncTask<String, Void, String>(){

            // Method to show an alert dialog with yes, no and cancel button
            private fun showDialog(mContext: Context,email: String,password: String) {
                // Late initialize an alert dialog object
                lateinit var dialog: AlertDialog


                // Initialize a new instance of alert dialog builder object
                val builder = AlertDialog.Builder(mContext)

                // Set a title for alert dialog
                builder.setTitle("Fingerprint Access.")

                // Set a message for alert dialog
                builder.setMessage("Do you want to store the credentials.")

                // On click listener for dialog buttons
                val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            val sharedPreferences = mContext.getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("Email", email)
                            editor.putString("Password", password)
                            editor.apply()

                            val intent = Intent(mContext, LoginActivity::class.java)
                            Toast.makeText(mContext, "Account Created.", Toast.LENGTH_SHORT).show()
                            mContext.startActivity(intent)
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                            val intent = Intent(mContext, LoginActivity::class.java)
                            Toast.makeText(mContext, "Account Created.", Toast.LENGTH_SHORT).show()
                            mContext.startActivity(intent) }
                    }
                }

                // Set the alert dialog positive/yes button
                builder.setPositiveButton("YES",dialogClickListener)

                // Set the alert dialog negative/no button
                builder.setNegativeButton("NO",dialogClickListener)


                // Initialize the AlertDialog using builder object
                dialog = builder.create()

                // Finally, display the alert dialog
                dialog.show()


            }


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
                    val registered = jSONObject.getBoolean("registered")

                    if(type == 1) {
                        println("hello")
                        println(jSONObject)

                        if(registered) {
                            showDialog(mContext,email,password)

                        }else{
                            Toast.makeText(mContext, "Account Not Created.", Toast.LENGTH_SHORT).show()

                        }
                    } else{
                        if (registered){
                            Toast.makeText(mContext, "Account Already Exists.", Toast.LENGTH_SHORT).show()
                        } else{
                            val reg = Registered(name,email,password,mContext,answer,intent)
                            reg.triggerRegister()
                        }
                    }
                }
                catch (error: Exception){}
                super.onPostExecute(result)
            }
        }

        private class Registered(private val name: String,private val email: String,private val password: String, private val mContext: Context,private val answer: String, private val Move: Intent) : CreateAccountActivity(){
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_create_account)
                supportActionBar!!.setTitle("Create Account")
            }

            // Call to register user
            fun triggerRegister() {

                var salt = java.util.UUID.randomUUID().toString().replace("-", "")
                val hashedPassword = saltAndHash(password, salt)
                register(name, hashedPassword, email, salt)
            }

            // Convert bytes to hex
            private fun byteArrayToHexString(array: Array<Byte>): String{

                var result = StringBuilder(array.size * 2)

                for (byte in array){
                    val toAppend = String.format("%2X", byte).replace(" ","0")
                    result.append(toAppend)
                }
                result.setLength(result.length)

                return result.toString()
            }

            // Generates md5 hash of string
            private fun md5(data: String):String {

                var result = ""

                try {

                    val md5 = MessageDigest.getInstance("MD5")
                    val md5HashBytes = md5.digest(data.toByteArray()).toTypedArray()

                    result = byteArrayToHexString(md5HashBytes)

                }catch (e: java.lang.Exception){}

                return result
            }

            // Generates a salt and hashes a function
            private fun saltAndHash(password:String,salt:String): String{
                val salted = password + salt
                return md5(salted).toLowerCase()
            }

            // connects to API and registers
            private fun register(name:String, password:String, email:String, salt: String){
                if(isNetworkAvailable(mContext)) {

                    val query = 0
                    try {
                        val connectToAPI = Connect(mContext, 1, name, email, password, answer,Move)
                        println("url")
                        val url = "http://54.81.239.120/insertAPI.php?queryType=$query&name=$name&password=$password&email=$email&salt=$salt&answer=$answer"
                        println(url)
                        connectToAPI.execute(url)
                    } catch (error: Exception) {
                        println("Fuck")
                        println(error)
                    }
                } else {
                    showInternetNotification(mContext, Move)
                }
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
                            finish()
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
