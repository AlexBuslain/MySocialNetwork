package com.example.mysocialnetworkproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newPostButton = findViewById<FloatingActionButton>(R.id.newPost)
        newPostButton.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        val user = Firebase.auth.currentUser
            user?.let {
                val email = user.email

                val uid = user.uid
            }

        if (user != null) {
            println(user.uid)
        }

        val db = Firebase.firestore
        // val postCollection = db.collection("posts")
        val post = hashMapOf(
            "id" to 1,
            "content" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse rutrum urna laoreet ultrices facilisis. Nulla viverra malesuada ultricies. Pellentesque aliquet ligula vitae urna cursus pretium. Nullam id nibh felis. Cras purus tellus, lacinia nec faucibus id, ornare vitae magna. Ut vestibulum, nisi a congue viverra, ex eros tristique ligula, sed.",
            "author" to "John Doe",
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }

        val postsLinearLayout = findViewById<LinearLayout>(R.id.post_iterator)
        val postCollection = db.collection("posts")

        postCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val postContent: TextView = TextView(this);
                    postContent.text = document.get("content").toString()
                    val postAuthor: TextView = TextView(this);

                    postAuthor.textSize = 20.0f
                    postAuthor.text = document.get("author").toString()

                    postsLinearLayout.addView(postAuthor)
                    postsLinearLayout.addView(postContent)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting posts: $exception")
            }
    }
}
