package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    // Checks if an email is already registered.
    private fun isRegistered(email:String,password:String){
        val query = 4
        val connectToAPI = Connect(this, 0,email,password)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
            println(url)
            connectToAPI.execute(url)
        }
        catch (error: Exception){}
    }
    private fun checkLogin(password:String, email:String): Boolean{
        var canLogin = true
        if(password.isNullOrBlank() || email.isNullOrBlank()){
            canLogin = false
            Toast.makeText(this, "All Fields are Required.", Toast.LENGTH_LONG).show()
        }

        return canLogin
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar!!.setTitle("Grafía Login")

        loginButton.setOnClickListener {


            val Email = findViewById<EditText>(R.id.loginEmail)
            val Password = findViewById<EditText>(R.id.loginPassword)
            val email = Email.text.toString()
            val password = Password.text.toString()
            if (checkLogin(password,email)) {
                isRegistered(email, password)
            }

//            if verified
//            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
//            startActivity(intent)
//            else
//            val intent = Intent(this@LoginActivity,NotVerifiedActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
//            startActivity(intent)
        }

        createAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }
        changePassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ChangePasswordActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }
    }


    companion object {
        class Connect(private val mContext: Context,private val flag: Int, private val email: String, private val password: String) :
            AsyncTask<String, Void, String>() {


            private fun getUID(email:String){
                val query = 2
                val connectToAPI = LoginActivity.Companion.Connect(mContext, 1,email,"")
                try{
                    val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
                    println(url)
                    connectToAPI.execute(url)
                }
                catch (error: Exception){}
            }

            private fun byteArrayToHexString(array: Array<Byte>): String {

                var result = StringBuilder(array.size * 2)

                for (byte in array) {
                    val toAppend = String.format("%2X", byte).replace(" ", "0")
                    result.append(toAppend)
                }
                result.setLength(result.length)

                return result.toString()
            }

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
                                getUID(email)
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
                            val intent = Intent(mContext, DashboardActivity::class.java)
                            intent.putExtra("userId", uid)
                            mContext.startActivity(intent)
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
        }
    }
}
