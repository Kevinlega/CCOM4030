package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_project.*

class ProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        supportActionBar!!.title = intent.getStringExtra("projectName")

        val userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)

        BackToDashboard.setOnClickListener {
            val intent = Intent(this@ProjectActivity, DashboardActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            // start your next activity
            startActivity(intent)
        }

        AddUsers.setOnClickListener {
            val intent = Intent(this@ProjectActivity, AddParticipantsActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)
//             start your next activity
            startActivity(intent)
        }

        Camera.setOnClickListener {
            val intent = Intent(this@ProjectActivity, CameraActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)
//             start your next activity
            startActivity(intent)
        }

        Voice.setOnClickListener {
            val intent = Intent(this@ProjectActivity, VoiceActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)
//             start your next activity
            startActivity(intent)
        }
    }
}
