package com.colman.mobilePostsApp.data.user

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class UserFirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val USERS_COLLECTION_PATH = "users"
    }

    fun getAllUsers(since: Long, callback: (List<User>) -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(User.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val users: MutableList<User> = mutableListOf()
                        for (json in it.result) {
                            val user = User.fromJSON(json.data)
                            users.add(user)
                        }
                        callback(users)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getImage(imageId: String, callback: (Uri) -> Unit) {
        storage.reference.child("images/$USERS_COLLECTION_PATH/$imageId")
            .downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri)
            }
    }

    private fun updateFirestoreUserProfileImage(userId: String, downloadUri: Uri, callback: (String) -> Unit) {
        val userDocRef = Firebase.firestore.collection("users").document(userId)

        userDocRef.update("profileImage", downloadUri.toString())
            .addOnSuccessListener {
                Log.d("Firebase", "Profile image updated in Firestore")
                callback(downloadUri.toString())  // ✅ Pass the URL back to the app
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update Firestore: ${e.message}")
            }
    }


    private fun updateUserProfileImage(userId: String, downloadUri: Uri, callback: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("Firebase", "User not logged in.")
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(downloadUri)  // ✅ Use Firebase Storage URL
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                Log.d("Firebase", "Profile image updated in Firebase Auth")
                updateFirestoreUserProfileImage(userId, downloadUri, callback)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update profile image: ${e.message}")
            }
    }


    fun addUserImage(userId: String, selectedImageUri: Uri, callback: (String) -> Unit) {
        val storageRef = Firebase.storage.reference.child("users/$userId/profile.jpg")

        storageRef.putFile(selectedImageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // ✅ Pass the download URL to update Firebase Auth and Firestore
                    updateUserProfileImage(userId, downloadUri, callback)
                }.addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to get download URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to upload image: ${e.message}")
            }
    }


    fun updateUser(user: User?, callback: () -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .document(user!!.id).update(user.updateJson)
            .addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't update this user document: " + it.message)
            }
    }

    fun addUser(user: User, callback: () -> Unit) {
        db.collection(USERS_COLLECTION_PATH).document(user.id).set(user.json)
            .addOnSuccessListener {
                callback()
            }
    }
}
