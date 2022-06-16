package com.example.mysocialnetworkproject

import android.content.Intent
import android.os.Build
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import java.util.*

class AddPostActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        var firstname = ""
        var lastname = ""

        db.collection("users").whereEqualTo("uid", user?.uid).get()
            .addOnSuccessListener { document ->
                firstname = document.documents[0].get("firstname").toString()
                lastname = document.documents[0].get("lastname").toString()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

        val submitButton: Button = findViewById(R.id.submit_post_button)
        submitButton.setOnClickListener {
            val content = findViewById<android.widget.EditText>(R.id.new_post_input).text.toString()
            val post = hashMapOf(
                "content" to content,
                "author" to "$firstname $lastname",
                "date" to FieldValue.serverTimestamp()
            )
            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG","DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG","Error adding document: $e")
                }
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        add_image_button.setOnClickListener {
            Log.d("AddPostActivity", "Try to see Add Picture")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)
        }
    }

    ///////////// Stockage de la photo dans Firebase Storage /////////////

    var selectedPhotoUri: Uri? = null

    private fun UploadImageToFirebase() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString() // Nom du fichier = random ID
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename") // Référence vers le storage

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("AddPostActivity", "Successfully upload")
            }
    }







    //////// Code supplémentaire pour ajouter l'image selectionnée dans le "add picture" ////////
    //override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //    super.onActivityResult(requestCode, resultCode, data)
            // On vient récupérer le résultat de la selection de la photo plus haut.
    //    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

    //        val uri = data.data
    //        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
    //        val showimage = BitmapDrawable(bitmap)
    //        add_image_button.setBackgroundDrawable(showimage)
    //    }
    //}
}