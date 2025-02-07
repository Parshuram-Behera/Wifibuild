package com.example.wifibuild.TestPack

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.wifibuild.R

class AllTest : AppCompatActivity() {

    private lateinit var motionLayout: MotionLayout
    private lateinit var swipeButton: ImageView
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_test)

        motionLayout = findViewById(R.id.mainMotion)
        swipeButton = findViewById(R.id.swipeButton)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound)


        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?, startId: Int, endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float
            ) {
                if (progress >= 0.9f) {

                    swipeButton.setImageResource(R.drawable.ic_check_mark)
                } else {

                    swipeButton.setImageResource(R.drawable.ic_arrow_right)
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                if (currentId == R.id.end) {
                    vibrator.vibrate(100)
                    mediaPlayer.start()
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float
            ) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}