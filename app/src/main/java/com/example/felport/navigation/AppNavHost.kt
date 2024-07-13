package com.example.felport.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.felport.ui.theme.portfolio.AddPortfolio
import com.example.felport.ui.theme.portfolio.AddScreen
//import com.example.felport.ui.theme.portfolio.HomePage
import com.example.felport.ui.theme.portfolio.HomeScreen
import com.example.felport.ui.theme.portfolio.Portfolio

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_HOME


) {


    BackHandler {
        navController.popBackStack()

    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
composable(ROUTE_ADD){
    AddScreen(rememberNavController())
}
       // composable(ROUTE_VIEW) {
          //  Portfolio(rememberNavController())
     //   }
        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }

    }
}

