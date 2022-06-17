package com.example.mysocialnetworkproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class AddPostActivity : AppCompatActivity() {
    // Variables pour l'ajout d'une image au Storage
    private val PICK_IMAGE_REQUEST = 71
    var filePath: Uri? = null
    var firebaseStore: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    lateinit var imagePreview: ImageView
    private lateinit var add_image_button: Button
    //private lateinit var submit_post_button: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        add_image_button = findViewById(R.id.add_image_button)
        imagePreview = findViewById(R.id.image_preview) // Pour la preview de l'image selectionnée

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        add_image_button.setOnClickListener { launchGallery() }

        val db = Firebase.firestore
        val uid = Firebase.auth.currentUser?.uid

        val submitButton: Button = findViewById(R.id.submit_post_button)
        submitButton.setOnClickListener {
            val imageId = uploadImage() // Appel de la fonction pour upload l'image sur Firebase Storage
            val content = findViewById<android.widget.EditText>(R.id.new_post_input).text.toString()
            val post = hashMapOf(
                "content" to content,
                "uid" to uid,
                "likes" to 0,
                "imageId" to imageId,
                "date" to FieldValue.serverTimestamp()
            )
            if (content.isEmpty() && imageId.isEmpty()) {
                Toast.makeText(this, "Please enter text and/or upload image", Toast.LENGTH_LONG).show()
            } else {
                db.collection("posts")
                    .add(post)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document: $e")
                    }

                Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show()
                intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
                finish()
            }
        }

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagePreview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(): String {
        return if (filePath != null) {
            val imageId = UUID.randomUUID().toString()
            val ref = storageReference?.child("myImages/$imageId")
            ref?.putFile(filePath!!)
            imageId
        } else {
            ""
        }
    }
}