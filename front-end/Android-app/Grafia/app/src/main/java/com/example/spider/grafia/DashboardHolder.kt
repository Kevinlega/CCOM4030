// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : DashboardHolder.kt
// Description : Displays a project at cell
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class DashboardHolder(itemView : View, private val mContext: Context) : RecyclerView.ViewHolder(itemView) {

    private val iview = itemView.findViewById<View>(R.id.iview) as ImageView
    private val tview = itemView.findViewById<View>(R.id.tview) as TextView

    // displays
    fun index(item : Int, s: String) {
        Glide.with(mContext).load(item).into(iview)

        tview.text = s
    }
}