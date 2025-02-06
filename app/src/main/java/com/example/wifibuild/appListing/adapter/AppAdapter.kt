package com.example.wifibuild.appListing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wifibuild.R
import com.example.wifibuild.appListing.dataModel.AppInfo
class AppAdapter(
    private val context: Context,
    private val appList: List<AppInfo>,
    private val selectionCallback: (Int) -> Unit // Callback to update selection count
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private val selectedItems = mutableSetOf<Int>() // Stores selected positions
    private var isSelectionMode = false // Tracks if selection mode is active

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = appList[position]
        holder.appName.text = appInfo.name
        holder.appIcon.setImageDrawable(appInfo.icon)

        // Highlight selected items
        holder.itemView.isSelected = selectedItems.contains(position)
        if (selectedItems.contains(position)) {
            holder.selectCheck.visibility = View.VISIBLE
            holder.selectCheck.isChecked = true
        } else {
            holder.selectCheck.isChecked = false
            holder.selectCheck.visibility = View.GONE
        }

        // Long press to start multi-selection
        holder.itemView.setOnLongClickListener {
            toggleSelection(position)
            true
        }

        // Click to select/deselect or launch app
        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(position)
            } else {
                // Launch app if no selection mode
                val launchIntent =
                    context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                launchIntent?.let { context.startActivity(it) }
            }
        }
    }

    override fun getItemCount(): Int = appList.size

    private fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }

        isSelectionMode = selectedItems.isNotEmpty()
        selectionCallback(selectedItems.size) // Update UI with selected count
        notifyItemChanged(position)
    }

    fun getSelectedApps(): List<String> {
        return selectedItems.map { appList[it].name }
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appName: TextView = itemView.findViewById(R.id.appName)
        val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        val selectCheck: CheckBox = itemView.findViewById(R.id.SelectCheck)
    }
}
