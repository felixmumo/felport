package com.example.felport.ui.theme.portfolio

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.InputStream

// Initialize Firebase in your application
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

data class Portfolio(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val score: String = "",
    val imageUrl: String = ""
)

@Composable
fun AddScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUrl = uri }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Magenta)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = "Add Portfolio",
            modifier = Modifier.size(100.dp),
            tint = Color.White
        )

        Text(
            text = "Add Portfolio Here",
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            leadingIcon = { Icon(Icons.Filled.Add, contentDescription = "Add Portfolio Name") },
            label = { Text("Portfolio Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = "Date") },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = score,
            onValueChange = { score = it },
            label = { Text("Score") },
            leadingIcon = { Icon(Icons.Filled.AccountBox, contentDescription = "Score") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("Select Image")
        }

        imageUrl?.let {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                bitmap = loadImageBitmap(it, context).asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .border(2.dp, Color.White, shape = RoundedCornerShape(10.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val portfolio = Portfolio(name = name, date = date, score = score)
                        postPortfolio(portfolio, imageUrl) {
                            isLoading = false
                            Toast.makeText(context, "Portfolio added successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Add Portfolio", color = Color.White)
            }
        }
    }
}

fun loadImageBitmap(uri: Uri, context: Context): Bitmap {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    return BitmapFactory.decodeStream(inputStream) ?: throw IllegalArgumentException("Could not load image")
}

fun postPortfolio(portfolio: Portfolio, imageUri: Uri?, onSuccess: () -> Unit) {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val storage: StorageReference = FirebaseStorage.getInstance().reference

    val key = database.child("portfolios").push().key
    if (key != null && imageUri != null) {
        val imageRef = storage.child("images/$key.jpg")
        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.e("Firebase", "Image upload failed", it)
                        throw it
                    }
                }
                imageRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    val newPortfolio = portfolio.copy(id = key, imageUrl = downloadUri)
                    database.child("portfolios").child(key).setValue(newPortfolio)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Firebase", "Portfolio added successfully")
                                onSuccess()
                            } else {
                                Log.e("Firebase", "Failed to add portfolio", it.exception)
                            }
                        }
                } else {
                    Log.e("Firebase", "Failed to get download URL", task.exception)
                }
            }
    } else if (key != null) {
        val newPortfolio = portfolio.copy(id = key)
        database.child("portfolios").child(key).setValue(newPortfolio)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Firebase", "Portfolio added successfully")
                    onSuccess()
                } else {
                    Log.e("Firebase", "Failed to add portfolio", it.exception)
                }
            }
    } else {
        Log.e("Firebase", "Failed to generate a database key or image URI is null")
    }
}

@Preview
@Composable
fun AddScreenPreview() {
    AddScreen(rememberNavController())
}