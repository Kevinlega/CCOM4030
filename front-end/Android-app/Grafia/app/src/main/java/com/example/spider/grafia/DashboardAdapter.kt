// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : DashboardAdapter.kt
// Description : Handles the Dashboard view
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.json.JSONArray


class DashboardAdapter(private val project: MutableList<Int>, private val name: JSONArray, private val projects_id : JSONArray, private val mContext: Context,private val userId : Int)
    : RecyclerView.Adapter<DashboardHolder>() {

    // creates the view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_recycler,parent, false)
        return DashboardHolder(v, mContext)

    }
    // Makes the cell
    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {

        holder?.index(project[position],name.get(position) as String)

        holder.itemView.setOnClickListener {
            val pId = getItemId(position)

            val intent = Intent(mContext, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId",getItem(position))
            intent.putExtra("projectName", getName(position))

//             start your next activity
            mContext.startActivity(intent)
        }
    }
    // gets the name of project at position
    fun getName(position: Int): String{
        return name.get(position) as String
    }

    // gets the project_id
    fun getItem(position: Int): Int {
        return (projects_id.get(position) as String).toInt()

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // get size of list
    override fun getItemCount(): Int {
        return project.size
    }
}