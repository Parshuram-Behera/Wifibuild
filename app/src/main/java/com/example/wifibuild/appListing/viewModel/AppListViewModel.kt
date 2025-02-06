package com.example.wifibuild.appListing.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.wifibuild.appListing.dataModel.AppInfo

class AppListViewModel(application :Application): AndroidViewModel(application) {

   // val appList = MutableList<List<AppInfo>>

    var appList : MutableList<String>  = mutableListOf()



}