package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_project.*

class ProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        supportActionBar!!.setTitle("Project Name")


        BackToDashboard.setOnClickListener {
            val intent = Intent(this@ProjectActivity, DashboardActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }

        AddUsers.setOnClickListener {
            val intent = Intent(this@ProjectActivity, AddParticipantsActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }


//        SaveImage.setOnClickListener {
//            val intent = Intent(this@ProjectActivity, DashboardActivity::class.java)
//            // To pass any data to next activity
////            intent.putExtra("keyIdentifier", value)
////             start your next activity
//            startActivity(intent)
//        }

//        Camera.setOnClickListener {
//            val intent = Intent(this@ProjectActivity, DashboardActivity::class.java)
//            // To pass any data to next activity
////            intent.putExtra("keyIdentifier", value)
////             start your next activity
//            startActivity(intent)
//        }

//        Gallery.setOnClickListener {
//            val intent = Intent(this@ProjectActivity, DashboardActivity::class.java)
//            // To pass any data to next activity
////            intent.putExtra("keyIdentifier", value)
////             start your next activity
//            startActivity(intent)
//        }


    }
}
