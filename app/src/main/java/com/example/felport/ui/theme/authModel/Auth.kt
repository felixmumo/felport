package com.example.felport.ui.theme.authModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Send verification email
                        sendVerificationEmail(user) { isEmailSent, message ->
                            if (isEmailSent) {
                                saveUserToFirestore(user) { isSuccessful, message ->
                                    onResult(isSuccessful, message)
                                }
                            } else {
                                onResult(false, message)
                            }
                        }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser, onResult: (Boolean, String?) -> Unit) {
        user.sendEmailVerification()
            .addOnSuccessListener {
                onResult(true, "Verification email sent. Please check your inbox.")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to send verification email: ${e.message}")
            }
    }

    private fun saveUserToFirestore(user: FirebaseUser, onResult: (Boolean, String?) -> Unit) {
        val userMap = mapOf(
            "uid" to user.uid,
            "email" to user.email
        )

        firestore.collection("users").document(user.uid)
            .set(userMap)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}