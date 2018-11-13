package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.json.JSONArray

class DashboardAdapter(private val project: MutableList<Int>, private val name: JSONArray, private val projects_id : JSONArray, private val mContext: Context,private val userId : Int)
    : RecyclerView.Adapter<DashboardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_recycler,parent, false)
        return DashboardHolder(v, mContext)

    }

    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {

        holder?.index(project[position],name.get(position) as String)

        holder.itemView.setOnClickListener {
            val pId = getItemId(position)

            val intent = Intent(mContext, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
//            intent.putExtra("pId",getItem(position))
//             start your next activity
            mContext.startActivity(intent)
        }
    }


    fun getItem(position: Int): Int {
        return projects_id.get(position) as Int

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return project.size
    }
}