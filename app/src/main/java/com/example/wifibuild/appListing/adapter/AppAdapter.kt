package com.example.wifibuild.appListing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wifibuild.R
import com.example.wifibuild.appListing.dataModel.AppInfo

class AppAdapter(private val context: Context, private val appList: List<AppInfo>) :
    RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = appList[position]
        holder.appName.text = appInfo.name
        holder.appIcon.setImageDrawable(appInfo.icon)

        // want to implement onlong click item selection count like galley app
       // holder.appIcon.setOnLongClickListener
    }
    override fun getItemCount(): Int {
        return appList.size
    }
    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appName: TextView = itemView.findViewById(R.id.appName)
        val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
    }
}