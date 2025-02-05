package com.example.wifibuild.appListing.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wifibuild.R
import com.example.wifibuild.appListing.adapter.AppAdapter
import com.example.wifibuild.appListing.dataModel.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var packageManager: PackageManager
    private lateinit var appList: MutableList<AppInfo>
    private lateinit var appAdapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        recyclerView = findViewById(R.id.recyclerViewApp)
        packageManager = getPackageManager()

        appList = mutableListOf()

        // Get the list of installed apps
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfos) {
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val icon: Drawable = resolveInfo.loadIcon(packageManager)
            appList.add(AppInfo(appName, packageName, icon))
        }

        appAdapter = AppAdapter(this, appList)

        // Set up the RecyclerView with a GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = appAdapter
    }

    private fun loadApps() {
        lifecycleScope.launch(Dispatchers.IO) {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)

            val apps = resolveInfos.mapNotNull { resolveInfo ->
                val appName =
                    resolveInfo.loadLabel(packageManager)?.toString() ?: return@mapNotNull null
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
}


