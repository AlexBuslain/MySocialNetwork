
package com.example.mysocialnetworkproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddPostActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val user = Firebase.auth.currentUser
        user?.let {
            val email = user.email

            val uid = user.uid
        }

        val db = Firebase.firestore

        val submitButton: Button = findViewById(R.id.submit_post_button)
        submitButton.setOnClickListener {
            val content = findViewById<android.widget.EditText>(R.id.new_post_input).text.toString()
            val post = hashMapOf(
                "content" to content,
                "author" to "@" + user?.email?.takeWhile{ it != '@' },
                "date" to FieldValue.serverTimestamp()
            )
            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    println("DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    println("Error adding document: $e")
                }
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}