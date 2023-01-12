package com.gec.gamestore

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.gec.gamestore.databinding.ActivityAddBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddActivity : AppCompatActivity() {
    private lateinit var bindingActivityAdd: ActivityAddBinding
    private val database = Firebase.database
    private val myRef = database.getReference("game")
    private val file = 1
    private var fileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {


        //AdMob
        MobileAds.initialize(this)


        super.onCreate(savedInstanceState)
        bindingActivityAdd = ActivityAddBinding.inflate(layoutInflater)
        val view = bindingActivityAdd.root
        setContentView(view)
        initLoadAds()

        bindingActivityAdd.saveButton.setOnClickListener {

            val name: String = bindingActivityAdd.nameEditText.text.toString()
            val date: String = bindingActivityAdd.dateEditText.text.toString()
            val price: String = bindingActivityAdd.priceEditText.text.toString()
            val description: String = bindingActivityAdd.descriptionEditText.text.toString()
            val key: String = myRef.push().key.toString()
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("game")
            val videoGameReference: StorageReference = folder.child("img$key")

            if (fileUri == null) {
                val mVideoGame = VideoGame(name, date, price, description)
                myRef.child(key).setValue(mVideoGame)
            } else {
                videoGameReference.putFile(fileUri!!).addOnSuccessListener {
                    videoGameReference.downloadUrl.addOnSuccessListener { uri ->
                        val mVideoGame = VideoGame(name, date, price, description, uri.toString())
                        myRef.child(key).setValue(mVideoGame)
                    }
                }
            }

            finish()
        }

        bindingActivityAdd.posterImageView.setOnClickListener {
            fileUpload()
        }
    }

    private fun fileUpload() {
        previewImage.launch("image/*")
    }

    private val previewImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            fileUri = uri
            bindingActivityAdd.posterImageView.setImageURI(uri)
        }

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        bindingActivityAdd.banner1.loadAd(adRequest)

    }
}