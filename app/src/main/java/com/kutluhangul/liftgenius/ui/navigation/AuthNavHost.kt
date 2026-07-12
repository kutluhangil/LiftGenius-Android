package com.kutluhangul.liftgenius.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kutluhangul.liftgenius.ui.auth.LoginScreen
import com.kutluhangul.liftgenius.ui.auth.RegisterScreen
import com.kutluhangul.liftgenius.ui.auth.WelcomeScreen

object AuthRoutes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

/** Unauthenticated flow. Switching to the main app happens reactively via sessionStatus. */
@Composable
fun AuthNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = AuthRoutes.WELCOME) {
        composable(AuthRoutes.WELCOME) {
            WelcomeScreen(
                onStart = { navController.navigate(AuthRoutes.REGISTER) },
                onHaveAccount = { navController.navigate(AuthRoutes.LOGIN) },
            )
        }
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(AuthRoutes.WELCOME)
                    }
                },
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate(AuthRoutes.LOGIN) {
                        popUpTo(AuthRoutes.WELCOME)
                    }
                },
            )
        }
    }
}
