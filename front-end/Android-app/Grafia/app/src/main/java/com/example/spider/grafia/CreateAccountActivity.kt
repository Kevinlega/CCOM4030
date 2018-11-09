package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_create_account.*
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.support.v7.app.AlertDialog
import java.util.Random
import android.os.AsyncTask
import android.os.SystemClock
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import android.view.*
import android.widget.*

class CreateAccountActivity : AppCompatActivity(){

    // Generates a random non-negative integer in [from, to - 1]
    fun UIGenerator(from:Int, to:Int): UInt{
        val random = Random().nextInt(to - from) + from
        return random.toUInt()
    }

    //Change the InitialValue
    fun feedback(initialValue: UInt): UInt{
        var newState = initialValue
        var count = 0
        while(count < 10) {
            if ((newState and 1.toUInt()) == 0.toUInt()){
                newState = newState shr 1
            }
            else {
                newState = (newState shr 1) xor 0x85913829.toUInt()
            }
            count += 1
        }
        return newState
    }

    // LFSR Algorithm
    fun LFSR(salt:String,initialValue:UInt): String{
        var newState = initialValue
        var hexadecimal: UInt
        var output = ""
        var char: UInt

        salt.forEach { character ->
            newState = feedback(initialValue)
            char = character.toByte().toUInt()
            hexadecimal = newState xor (char and 0xFF.toUInt())
            output += hexadecimal.toString()
        }
        return output
    }

    // Generates a salt and hashes a function
    fun saltAndHash(password:String,salt:String,initialValue: UInt): String{
        val salted = password + salt
//        salted = LFSR(salted,initialValue)
        return salted.hashCode().toString()
    }

    // Checks if an email is already registered.
    fun isRegistered(email:String): Boolean{
        val query = 0
        val connectToAPI = Connect(this)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
            println(url)
            connectToAPI.execute(url)
        }
        catch (error: Exception){}

        return connectToAPI.registered

    }

    fun checkLogin(name:String, password:String,confirm:String,email:String): Boolean{
        var canLogin = true
        if(name.isNullOrBlank() || password.isNullOrBlank() || confirm.isNullOrBlank() || email.isNullOrBlank()){
            canLogin = false
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Wrong input data")
            alertDialog.setMessage("All fields are required")
            alertDialog.setNeutralButton("OK", { dialogInterface: DialogInterface, i: Int -> })
            alertDialog.show()
        }

        if(password != confirm){
            canLogin = false
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Wrong input data")
            alertDialog.setMessage("Passwords don't match")
            alertDialog.setNeutralButton("OK", { dialogInterface: DialogInterface, i: Int -> })
            alertDialog.show()
        }
        return canLogin
    }

    fun register(name:String, password:String, email:String, salt: String, initialValue: UInt): Boolean{
        val query = 0
        val connectToAPI = Connect(this)

        try{
            val url = "http://54.81.239.120/insertAPI.php?queryType=$query&name=$name&password=$password&email=$email&salt=$salt&initialValue=$initialValue"
            println(url)
            connectToAPI.execute(url)
        }
        catch (error: Exception){}
        println(connectToAPI.registered)
        return connectToAPI.registered
    }


    override fun onCreate(savedInstanceState: Bundle?){
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
        CreateAccount.setOnClickListener{

            // Get input from user forms.
            val Name = findViewById(R.id.caName) as EditText
            val Email = findViewById(R.id.caEmail) as EditText
            val Password = findViewById(R.id.caPassword) as EditText
            val ConfirmPassword = findViewById(R.id.caConfirmPassword) as EditText
            val name = Name.text.toString()
            val email = Email.text.toString()
            val password = Password.text.toString()
            val confirm = ConfirmPassword.text.toString()
            if(checkLogin(name,password,confirm,email)) {
                if (!isRegistered(email)){
                    val initialValue = UIGenerator(0,101)
                    var salt = java.util.UUID.randomUUID().toString().replace("-", "")

//                    salt = LFSR(salt,initialValue)

                    val hashedPassword = saltAndHash(password,salt,initialValue)

                    if(register(name,hashedPassword,email,salt,initialValue)){
                        val intent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                else{
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Duplicate input data")
                    alertDialog.setMessage("User Already Registered")
                    alertDialog.setNeutralButton("OK", { dialogInterface: DialogInterface, i: Int -> })
                    alertDialog.show()

                }
            }
        }
    }

    // Connect class that checks if user is registered,
    // if not, registers said user.
    companion object {
        class Connect(context: Context): AsyncTask<String, Void, String>(){

            var registered = false

            override fun doInBackground(vararg p0: String?): String{
                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String{
                return URL(url).readText()
            }

            override fun onPostExecute(result: String?){
                try{
                    val jSONObject = JSONObject(result)
                    println(jSONObject)
                    registered = jSONObject.getBoolean("registered")
                }
                catch (error: Exception){}
                super.onPostExecute(result)
            }
        }
    }
}