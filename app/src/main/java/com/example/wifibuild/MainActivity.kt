package com.example.wifibuild

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.wifibuild.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {


    lateinit var sendButton: LinearLayout
    lateinit var receiveButton: LinearLayout
    lateinit var animationLayout: LinearLayout
    lateinit var animationView: LottieAnimationView

    val viewModel by lazy { MainViewModel(Application()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendButton = findViewById(R.id.circle_send)
        receiveButton = findViewById(R.id.circle_receive)
        animationLayout = findViewById(R.id.animation_layout)
        animationView = findViewById(R.id.lottieAnimationView)



        sendButton.visibility = View.VISIBLE
        receiveButton.visibility = View.VISIBLE

        sendButton.setOnClickListener {
            animationLayout.visibility = View.VISIBLE
            sendButton.visibility = View.GONE
            receiveButton.visibility = View.GONE
            animationView.setAnimation(R.raw.send_lottie_json)
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.playAnimation()
        }

        receiveButton.setOnClickListener {
            animationLayout.visibility = View.VISIBLE
            sendButton.visibility = View.GONE
            receiveButton.visibility = View.GONE
            animationView.setAnimation(R.raw.receive_lottie_json)
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.playAnimation()
        }
    }
}