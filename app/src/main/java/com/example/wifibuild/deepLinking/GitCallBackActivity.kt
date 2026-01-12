package com.example.wifibuild.deepLinking

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class GitCallBackActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /**
         Here I Am Handling the Github CallBack Response from Github
         */


        val callBackData = intent?.data

        if (callBackData != null && callBackData.toString().startsWith("wifibuild://auth/callback")){
            val code = callBackData.getQueryParameter("code")

            Log.d("GitCallBackActivityTest", "Code: $code")
        }

    }

}