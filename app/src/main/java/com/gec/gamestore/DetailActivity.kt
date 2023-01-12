package com.gec.gamestore

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.bumptech.glide.Glide
import com.gec.gamestore.databinding.ActivityDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {
    private lateinit var bindingActivityDetail: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityDetail = ActivityDetailBinding.inflate(layoutInflater)
        val view = bindingActivityDetail.root

        setContentView(view)
        fullScreen()

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef =
            database.getReference("game").child(
                key.toString()
            )

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mVideoGame: VideoGame? = dataSnapshot.getValue(VideoGame::class.java)
                if (mVideoGame != null) {
                    bindingActivityDetail.nameTextView.text = mVideoGame.name.toString()
                    bindingActivityDetail.priceTextView.text = mVideoGame.price.toString() + "€"
                    bindingActivityDetail.descriptionTextView.text =
                        mVideoGame.description.toString()
                    Glide.with(applicationContext)
                        .load(mVideoGame.url.toString())
                        .into(bindingActivityDetail.posterImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}