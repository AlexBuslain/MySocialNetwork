package com.example.mysocialnetworkproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Getting the user's uid from the intent
        val bundle: Bundle? = intent.extras
        val userId = bundle?.getString("uid")

        // Instantiating Firebase
        val db = Firebase.firestore

        // ScrollView binding to display posts
        val postsList = findViewById<LinearLayout>(R.id.post_iterator)

        // Posts and users collection instantiation
        val postCollection = db.collection("posts")
        val usersCollection = db.collection("users")

        val userNameText = findViewById<TextView>(R.id.profile_name)


        usersCollection.whereEqualTo("uid", userId).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Getting the user's name
                    userNameText.text = document.getString("firstname") + " " + document.getString("lastname")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error getting documents: ", exception)
            }

        // Query to get all posts
        postCollection
            .whereEqualTo("uid", userId)
            //.orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
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
                            Log.e("TAG", "Error getting user's name and lastname: ", exception)
                        }
                    postAuthor.setTextColor(Color.BLACK)
                    // TextView to display post content
                    val postContent = TextView(this)
                    // Set post content from post attributes
                    postContent.text = document.get("content").toString()
                    // Some weird shit to justify text
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            postContent.justificationMode =
                                LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                    }
                    val image = ImageView(this)
                    // Get image from post attributes
                    image.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val storage = FirebaseStorage.getInstance()
                    storage.reference.child("myImages/" + document.getString("imageId")).downloadUrl
                        .addOnSuccessListener { uri ->
                            Glide.with(this).load(uri).into(image)
                        }
                    //image.text = storageReference
                    val like = Button(this)
                    like.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
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
                Log.e("TAG","Error getting posts: $exception")
            }

    }
}