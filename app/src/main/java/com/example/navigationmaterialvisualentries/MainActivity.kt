package com.example.navigationmaterialvisualentries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import com.example.navigationmaterialvisualentries.ui.theme.NavigationMaterialVisualEntriesTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationMaterialVisualEntriesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Navigation() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    ModalBottomSheetLayout(bottomSheetNavigator) {
        AnimatedNavHost(navController, startDestination = "home") {
            composable("home") {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Visible entries:")
                    navController.visibleEntries.collectAsState().value
                        .forEach {
                            var state by remember { mutableStateOf(it.lifecycle.currentState) }
                            DisposableEffect(it) {
                                val observer =
                                    LifecycleEventObserver { _, event -> state = event.targetState }
                                it.lifecycle.addObserver(observer)
                                onDispose { it.lifecycle.removeObserver(observer) }
                            }
                            Text("${it.destination.route}: $state")
                        }
                    Button(onClick = { navController.navigate("sheet") }) {
                        Text("Open bottom sheet")
                    }
                }
            }

            bottomSheet("sheet") {
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Bottom sheet")
                    Button(
                        onClick = {
                            navController.navigate("other") {
                                popUpTo("home")
                            }
                        },
                    ) {
                        Text("Navigate to other page")
                    }
                }
            }

            composable("other") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Other page")
                }
            }
        }
    }
}
