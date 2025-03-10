package com.colman.mobilePostsApp.data.sync

import android.content.Context
import com.colman.mobilePostsApp.data.db.AppDatabase
import com.colman.mobilePostsApp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreSync(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val userDao = AppDatabase.getDatabase(context).userDao()

    // ✅ Download Firestore users and store them in Room
    fun syncUsersFromFirestore() {
        usersCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    insertUserIntoRoom(user)
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching users from Firestore: $e")
            }
    }

    // ✅ Upload Room users to Firestore
    fun syncUsersToFirestore(users: List<User>) {
        for (user in users) {
            usersCollection.document(user.id)
                .set(user)
                .addOnSuccessListener { println("User ${user.id} synced to Firestore!") }
                .addOnFailureListener { e -> println("Error syncing user: $e") }
        }
    }

    private fun insertUserIntoRoom(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertUser(user)  // ✅ Now runs inside a coroutine
        }
    }
}
