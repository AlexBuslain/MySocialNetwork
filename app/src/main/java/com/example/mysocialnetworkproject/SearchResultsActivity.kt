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

        searchAndDisplay(query)

        val searchView = findViewById<android.widget.SearchView>(R.id.search_view2)
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newQuery: String?): Boolean {
                if (newQuery != null) {
                    searchAndDisplay(newQuery)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun searchAndDisplay(query: String?) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val foundUIDs: MutableList<String> = mutableListOf()
        val foundUsers: MutableList<String> = mutableListOf()

        if (query != null) {
            usersCollection.whereIn("firstname", query.split(' ')).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val uid = document["uid"] as String
                        if (uid !in foundUIDs) {
                            foundUIDs.add(uid)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ${exception.message}")
                }
            usersCollection.whereIn("lastname", query.split(' ')).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val uid = document["uid"] as String
                        foundUIDs.add(uid)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ${exception.message}")
                }
        }

        for (uid in foundUIDs) {
            usersCollection.whereEqualTo("uid", uid).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val firstname = document["firstname"] as String
                        val lastname = document["lastname"] as String
                        val name = "$firstname $lastname"
                        foundUsers.add(name)
                        Log.d("TAG", "NTM")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ${exception.message}")
                }
        }
        val array = mutableListOf("a", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val listView = findViewById<ListView>(R.id.user_list)
        val adapter = ArrayAdapter(this, R.layout.search_results, foundUsers)
        listView.adapter = adapter
    }
}