@file:Suppress("NAME_SHADOWING")

package com.example.mysocialnetworkproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast", "SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button to add post
        val newPostButton = findViewById<FloatingActionButton>(R.id.newPost)
        newPostButton.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        // Instantiating Firebase
        val db = Firebase.firestore

        // ScrollView binding to display posts
        val postsList = findViewById<LinearLayout>(R.id.post_iterator)

        // Posts and users collection instantiation
        val postCollection = db.collection("posts")
        val usersCollection = db.collection("users")

        // Query to get all posts
        postCollection.orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                // For each post, get the user and content
                for (document in result) {
                    // One LinearLayout per post
                    val post = LinearLayout(this)
                    post.orientation = LinearLayout.VERTICAL
                    post.setPadding(0, 10, 0, 40)
                        // TextView to display post author
                        val postAuthor = TextView(this)
                        // Get uid from post attributes
                        val uid = document.getString("uid")
                        // Get user from users collection with uid
                        usersCollection.whereEqualTo("uid", uid).get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    // Set author name from user attributes
                                    postAuthor.text = document.getString("firstname") + " " + document.getString("lastname")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("TAG", "Error getting user's name and lastname: ", exception)
                            }
                        postAuthor.setTextColor(Color.BLACK)
                        // TextView to display post content
                        val postContent = TextView(this)
                        // Set post content from post attributes
                        postContent.text = document.get("content").toString()
                        // Some weird shit to justify text
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                postContent.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                            }
                        }
                        val image = ImageView(this)
                        // Get image from post attributes
                        image.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                        val storage = FirebaseStorage.getInstance()
                        storage.reference.child("myImages/" + document.getString("imageId")).downloadUrl
                            .addOnSuccessListener { uri ->
                            Glide.with(this).load(uri).into(image)
                        }
                        //image.text = storageReference
                        val like = Button(this)
                        like.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                        like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0)
                        like.text = document.get("likes").toString()
                        // Variable to make it possible to unlike
                        var liked = false
                        like.setOnClickListener {
                            // Increment likes count
                            if (!liked) {
                                val likes = document.getLong("likes")!! + 1
                                // Update likes count in post collection
                                postCollection.document(document.id).update("likes", likes)
                                // Update liked state
                                liked = true
                                // Update likes count in button text
                                like.text = likes.toString()
                            } else {
                                val likes = document.getLong("likes")!!
                                postCollection.document(document.id).update("likes", likes)
                                liked = false
                                like.text = likes.toString()
                            }
                        }
                        // Add Author TextView to LinearLayout
                        post.addView(postAuthor)
                        // Add Content TextView to LinearLayout
                        post.addView(postContent)
                        // Add ImageView to LinearLayout
                        post.addView(image)
                        // Add Like button to post
                        post.addView(like)
                    // Add LinearLayout to ScrollView
                    postsList.addView(post)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG","Error getting posts: $exception")
            }

        // Search view
        val searchView = findViewById<android.widget.SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    intent = Intent(this@MainActivity, SearchResultsActivity::class.java)
                    intent.putExtra("query", query)
                    startActivity(intent)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        @Override
        fun onResume() {
            super.onResume();
            this.onCreate(null);
        }
    }
}
