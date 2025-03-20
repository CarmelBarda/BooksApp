package com.colman.mobilePostsApp.data.user

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class UserFirebaseModel {

    private var mAuth: FirebaseAuth
    private var mUser: FirebaseUser?

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    init {
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
    }

    companion object {
        const val USERS_COLLECTION_PATH = "users"
    }

    fun getUser(id: String, optionalListener: UserModel.GetLoggedUserListener) {
        val documentRef = db.collection("users").document(id)

        documentRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        var user = document.toObject(User::class.java)
                        user?.id = document.id
                        optionalListener.onComplete(user ?: User("", "", ""))
                    } else {
                        optionalListener.onComplete(User("", "", ""))
                    }
                } else {
                    optionalListener.onComplete(User("", "", ""))
                }
            }
            .addOnFailureListener {
                optionalListener.onComplete(User("", "", ""))
            }
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
                callback(downloadUri.toString())
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
            .setPhotoUri(downloadUri)
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
