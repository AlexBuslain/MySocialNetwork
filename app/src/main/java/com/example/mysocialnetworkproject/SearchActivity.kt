package com.example.mysocialnetworkproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val searchResultsLayout = findViewById<LinearLayout>(R.id.search_results_layout)

        usersCollection.get()
            .addOnSuccessListener { result ->
                displayUsers(result, searchResultsLayout)
            }

        val searchView = findViewById<android.widget.SearchView>(R.id.search_view2)
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchResultsLayout.removeAllViews()
                if (query != null) {
                    val query1 = query.split(" ")[0]
                    val query2 = query.split(" ")[1]
                    usersCollection
                        .whereEqualTo("firstname", query1)
                        .whereEqualTo("lastname", query2)
                        .get()
                        .addOnSuccessListener { result ->
                            displayUsers(result, searchResultsLayout)
                        }
                        .addOnFailureListener { exception ->
                            Log.w("TAG", "Error getting users: ${exception.message}")
                        }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun displayUsers(result: QuerySnapshot, searchResultsLayout: LinearLayout) {
        for (document in result) {
            val firstname = document["firstname"] as String
            val lastname = document["lastname"] as String
            val uid = document["uid"] as String

            val userItem = TextView(this@SearchActivity)
            userItem.text = "$firstname $lastname"
            userItem.textSize = 30.0f
            userItem.setPadding(0, 0, 0, 20)
            searchResultsLayout.addView(userItem)
            userItem.setOnClickListener{
                val intent = Intent(this@SearchActivity, ProfileActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }
        }
    }


}