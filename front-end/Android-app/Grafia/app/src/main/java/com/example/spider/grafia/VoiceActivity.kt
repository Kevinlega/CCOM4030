package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_voice.*

class VoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice)

        supportActionBar!!.setTitle("Voice Recorder")

        val userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)

        backToProject2.setOnClickListener {
            val intent = Intent(this@VoiceActivity, ProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }
    }
}
