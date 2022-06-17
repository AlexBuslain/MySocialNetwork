package com.example.mysocialnetworkproject

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_post.*
import java.io.IOException
import java.util.*

class AddPostActivity : AppCompatActivity() {
    ////// Variables pour l'ajout d'une image au Storage ////////
    val PICK_IMAGE_REQUEST = 71
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
        //submit_post_button = findViewById(R.id.submit_post_button)
        imagePreview = findViewById(R.id.image_preview) // Pour la preview de l'image selectionnée

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        add_image_button.setOnClickListener { launchGallery() }
        //submit_post_button.setOnClickListener { uploadImage() }




        val db = Firebase.firestore
        val uid = Firebase.auth.currentUser?.uid

        val submitButton: Button = findViewById(R.id.submit_post_button)
        submitButton.setOnClickListener {
            uploadImage() // Appel de la fonction pour upload l'image sur Firebase Storage
            val content = findViewById<android.widget.EditText>(R.id.new_post_input).text.toString()
            val post = hashMapOf(
                "content" to content,
                "uid" to uid,
                "likes" to 0,
                "date" to FieldValue.serverTimestamp()
            )
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
            startActivity(intent)
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

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference?.child("myImages/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

        } else {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }    }
}