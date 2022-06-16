@file:Suppress("NAME_SHADOWING")

package com.example.mysocialnetworkproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newPostButton = findViewById<FloatingActionButton>(R.id.newPost)
        newPostButton.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        val db = Firebase.firestore

        val postsList = findViewById<LinearLayout>(R.id.post_iterator)
        val postCollection = db.collection("posts")
        val usersCollection = db.collection("users")

        postCollection.orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val post = LinearLayout(this)
                    post.orientation = LinearLayout.VERTICAL
                    post.setPadding(0, 10, 0, 40)

                        val postAuthor = TextView(this)
                        val uid = document.getString("uid")
                        usersCollection.whereEqualTo("uid", uid).get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    postAuthor.text = document.getString("firstname") + " " + document.getString("lastname")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("TAG", "Error getting user's name and lastname: ", exception)
                            }
                        postAuthor.setTextColor(Color.BLACK)

                        val postContent = TextView(this)
                        postContent.text = document.get("content").toString()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                postContent.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                            }
                        }
                        post.addView(postAuthor)
                        post.addView(postContent)

                    postsList.addView(post)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG","Error getting posts: $exception")
            }
    }
}
