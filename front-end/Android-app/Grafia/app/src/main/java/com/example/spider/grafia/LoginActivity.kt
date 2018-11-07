package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.lang.StringBuilder
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private fun byteArrayToHexString(array: Array<Byte>): String{

        var result = StringBuilder(array.size * 2)

        for (byte in array){
            val toAppend = String.format("%2X", byte).replace(" ","0")
            result.append(toAppend)
        }
        result.setLength(result.length)

        return result.toString()
    }

    private fun md5(data: String):String {

        var result = ""

        try {

            val md5 = MessageDigest.getInstance("MD5")
            val md5HashBytes = md5.digest(data.toByteArray()).toTypedArray()

            result = byteArrayToHexString(md5HashBytes)

        }catch (e: Exception){}

        return result
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
//            if verified
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
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
}
