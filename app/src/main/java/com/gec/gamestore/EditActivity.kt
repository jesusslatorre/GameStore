package com.gec.gamestore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gec.gamestore.databinding.ActivityEditBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*

class EditActivity : AppCompatActivity() {
    private lateinit var bindingActivityEdit: ActivityEditBinding
    private val file = 1
    private var fileUri: Uri? = null
    private var imageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        MobileAds.initialize(this)

        super.onCreate(savedInstanceState)
        bindingActivityEdit = ActivityEditBinding.inflate(layoutInflater)
        val view = bindingActivityEdit.root
        setContentView(view)

        //AdMob
        val adRequest = AdRequest.Builder().build()
        bindingActivityEdit.banner2.loadAd(adRequest)

        val key = intent.getStringExtra("key")
        val database = Firebase.database

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val myRef = database.getReference("game").child(key.toString())

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mVideoGame: VideoGame? = dataSnapshot.getValue(VideoGame::class.java)
                if (mVideoGame != null) {

                    bindingActivityEdit.nameEditText.text =
                        Editable.Factory.getInstance().newEditable(mVideoGame.name)
                    bindingActivityEdit.dateEditText.text =
                        Editable.Factory.getInstance().newEditable(mVideoGame.date)

                    imageUrl = mVideoGame.url.toString()

                    if (fileUri == null) {
                        Glide.with(applicationContext)
                            .load(imageUrl)
                            .into(bindingActivityEdit.posterImageView)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        bindingActivityEdit.saveButton.setOnClickListener {


            val subject: String = bindingActivityEdit.descriptionTextView.text.toString()
            val message: String = bindingActivityEdit.descriptionEditText.text.toString()
            val email2: String = bindingActivityEdit.dateEditText.text.toString()


            val addresses = subject.split(",".toRegex()).toTypedArray()

            val intent = Intent(Intent.ACTION_SENDTO).apply {

                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email2))


            }
            startActivity(intent)

        }


    }

}