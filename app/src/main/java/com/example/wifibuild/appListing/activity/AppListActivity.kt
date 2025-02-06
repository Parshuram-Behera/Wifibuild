package com.example.wifibuild.appListing.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wifibuild.R
import com.example.wifibuild.appListing.adapter.AppAdapter
import com.example.wifibuild.appListing.dataModel.AppInfo
import com.example.wifibuild.appListing.viewModel.AppListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var packageManager: PackageManager
    private lateinit var appList: MutableList<AppInfo>
    private lateinit var appAdapter: AppAdapter
    private lateinit var selectionCountText: TextView
    private lateinit var shareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        recyclerView = findViewById(R.id.recyclerViewApp)
        selectionCountText = findViewById(R.id.selectionCountText)
        shareButton = findViewById(R.id.shareButton)
        packageManager = getPackageManager()
        appList = mutableListOf()

        // Load installed apps
        loadApps()

        appAdapter = AppAdapter(this, appList) { count ->
            updateSelectionCount(count)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = appAdapter


        shareButton.setOnClickListener {

            val selectedApps = appAdapter.getSelectedApps()

            if (selectedApps.isNotEmpty()){

                Log.d("SelectedApps", "Selected apps: $selectedApps")
            }
        }
    }

    private fun loadApps() {
        lifecycleScope.launch(Dispatchers.IO) {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)

            val apps = resolveInfos.mapNotNull { resolveInfo ->
                val appName = resolveInfo.loadLabel(packageManager)?.toString() ?: return@mapNotNull null
                val packageName = resolveInfo.activityInfo.packageName
                val icon: Drawable = resolveInfo.loadIcon(packageManager) ?: return@mapNotNull null
                AppInfo(appName, packageName, icon)
            }

            withContext(Dispatchers.Main) {
                appList.clear()
                appList.addAll(apps)
                appAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateSelectionCount(count: Int) {
        if (count > 0) {
            selectionCountText.text = "$count selected"
            selectionCountText.visibility = TextView.VISIBLE
            shareButton.visibility = Button.VISIBLE
        } else {
            selectionCountText.visibility = TextView.GONE
            shareButton.visibility = Button.GONE
        }
    }
}


