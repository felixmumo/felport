package com.example.felport.ui.theme.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Visibility
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.felport.navigation.ROUTE_VIEW

// Define route variables
const val ROUTE_ADD = "add_portfolio"
const val ROUTE_VIEW = "view_portfolio"
//const val ROUTE_VIEW = "view_portfolio"

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Cyan),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Our Portfolio Page",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { navController.navigate(ROUTE_ADD) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 8.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Green),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Icon",
                tint = androidx.compose.ui.graphics.Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Portfolio", color = androidx.compose.ui.graphics.Color.White)
        }

        Button(
            onClick = { navController.navigate(ROUTE_VIEW) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 8.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Green),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Add Icon",
                tint = androidx.compose.ui.graphics.Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Portfolio", color = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    HomeScreen(rememberNavController())
}