package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val userId = 1
        supportActionBar!!.setTitle("Dashboard")

        logout.setOnClickListener {
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            finish()
            startActivity(intent)
        }

        CreateProject.setOnClickListener {
            val intent = Intent(this@DashboardActivity, CreateProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }

        Friends.setOnClickListener {
            val intent = Intent(this@DashboardActivity, FriendsActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", 1)
//             start your next activity
            startActivity(intent)
        }

        ProjectView.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }


    }
}
