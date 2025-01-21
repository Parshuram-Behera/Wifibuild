package com.example.wifibuild.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private var updateButton: MutableLiveData<String> = MutableLiveData()
    val buttonData: LiveData<String> get() = updateButton
}