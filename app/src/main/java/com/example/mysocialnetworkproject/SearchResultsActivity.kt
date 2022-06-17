package com.example.mysocialnetworkproject

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SearchResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val bundle = intent.extras
        val query = bundle?.getString("query")

        val searchView = findViewById<android.widget.SearchView>(R.id.search_view2)
        searchView.setQuery(query, true)
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newQuery: String?): Boolean {
                if (newQuery != null) {
                    val users = search(newQuery)
                    displayUsers(users)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun search(query: String?): MutableList<String> {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val foundUIDs = mutableListOf("")
        val foundUsers = mutableListOf("")

        if (query != null) {
            // Search for users with part of the query in their firstname
            usersCollection.whereArrayContainsAny("firstname", query.split(' ')).get()
                .addOnSuccessListener { result ->
                    // For each user found, add their UID to the list of found UIDs
                    for (document in result) {
                        val uid = document["uid"] as String
                        if (uid !in foundUIDs) {
                            foundUIDs.add(uid)
                        }
                        // Search for users with part of the query in their lastname
                    }
                    usersCollection.whereIn("lastname", query.split(' ')).get()
                        .addOnSuccessListener { result ->
                            // For each user found, add their UID to the list of found UIDs
                            for (document in result) {
                                val uid = document["uid"] as String
                                if (uid !in foundUIDs) {
                                    foundUIDs.add(uid)
                                }
                            }
                            // Search for users based on UID list
                            for (uid in foundUIDs) {
                                usersCollection.whereEqualTo("uid", uid).get()
                                    .addOnSuccessListener { result ->
                                        // Fill the list of found users with the names of the users
                                        for (document in result) {
                                            val firstname = document["firstname"] as String
                                            val lastname = document["lastname"] as String
                                            val name = "$firstname $lastname"
                                            foundUsers.add(name)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w("TAG", "Error getting documents: ${exception.message}")
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("TAG", "Error getting documents: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ${exception.message}")
                }
        }
        return foundUsers
    }

    fun displayUsers(users: MutableList<String>): Unit? {
        // val array = mutableListOf("a", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val listView = findViewById<ListView>(R.id.user_list)
        val adapter = ArrayAdapter(this@SearchResultsActivity, R.layout.search_results, users)
        listView.adapter = adapter
        return null
    }
}