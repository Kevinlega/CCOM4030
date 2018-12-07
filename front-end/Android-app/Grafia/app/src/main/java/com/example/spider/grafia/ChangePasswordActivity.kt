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
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_change_password.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.security.MessageDigest

class ChangePasswordActivity : AppCompatActivity() {

    // Is user registered request
    private fun isRegistered(email:String, answer: String){
        val query = 0
        val connectToAPI = Connect(this, 0,email,answer)
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

            // Fetch text from activity text boxes
            val email = Email.text.toString()
            val answer = Answer.text.toString()
            if (!email.isNullOrBlank() && !answer.isNullOrBlank()) {
                isRegistered(email,answer)
            }
                else{
                    Toast.makeText(this@ChangePasswordActivity,"Fields are required.", Toast.LENGTH_LONG).show()
                }
            }
        }

    // Connects to API to check if user is registered,
    // if registered changes the password for such user
    companion object {
        class Connect(private val mContext: Context, private val flag: Int, private val email: String, private val answer: String) :
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

                            val query = 4
                            val connectToAPI = Connect(mContext, 1,email,answer)
                            try{
                                val url = "http://54.81.239.120/insertAPI.php?queryType=$query&email=$email&answer=$answer"
                                connectToAPI.execute(url)
                            }
                            catch (error: Exception){}

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

            // Generates a salt and hashes a function
            private fun saltAndHash(password:String,salt:String): String{
                val salted = password + salt
                return md5(salted).toLowerCase()
            }


            // Convert bytes to hex
            private fun byteArrayToHexString(array: Array<Byte>): String {

                var result = StringBuilder(array.size * 2)
                for (byte in array) {
                    val toAppend = String.format("%2X", byte).replace(" ", "0")
                    result.append(toAppend)
                }
                result.setLength(result.length)
                return result.toString()
            }

            // Generate md5 hash from given string
            private fun md5(data: String): String {
                var result = ""
                try{
                    val md5 = MessageDigest.getInstance("MD5")
                    val md5HashBytes = md5.digest(data.toByteArray()).toTypedArray()
                    result = byteArrayToHexString(md5HashBytes)
                } catch (e: Exception) {}
                return result
            }
        }
    }
}
