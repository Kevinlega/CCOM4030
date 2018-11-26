package com.example.spider.grafia

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class NotVerifiedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_verified)

        supportActionBar!!.setTitle("Account not verified")
    }
}
