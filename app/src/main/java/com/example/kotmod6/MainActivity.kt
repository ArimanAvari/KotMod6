package com.example.kotmod6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotmod6.presentation.PhotoCatalogViewModel
import com.example.kotmod6.presentation.detail.PhotoDetailScreen
import com.example.kotmod6.presentation.list.PhotoListScreen
import com.example.kotmod6.ui.theme.KotMod6Theme

class MainActivity : ComponentActivity() {
    private val viewModel: PhotoCatalogViewModel by viewModels {
        PhotoCatalogViewModel.factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KotMod6Theme {
                val navController = rememberNavController()
                val catalogState by viewModel.catalogState.collectAsState()
                val downloadState by viewModel.downloadState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = Routes.Catalog
                ) {
                    composable(Routes.Catalog) {
                        PhotoListScreen(
                            state = catalogState,
                            onRetry = viewModel::loadPhotos,
                            onPhotoClick = { photo ->
                                navController.navigate("${Routes.Detail}/${photo.id}")
                            }
                        )
                    }

                    composable(
                        route = "${Routes.Detail}/{photoId}",
                        arguments = listOf(navArgument("photoId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val photoId = backStackEntry.arguments?.getString("photoId").orEmpty()
                        PhotoDetailScreen(
                            photo = viewModel.findPhoto(photoId),
                            loadingState = catalogState,
                            downloadState = downloadState,
                            onBack = { navController.popBackStack() },
                            onRetry = viewModel::loadPhotos,
                            onDownload = viewModel::savePhotoBySaf,
                            onMessageShown = viewModel::clearDownloadMessage
                        )
                    }
                }
            }
        }
    }
}

private object Routes {
    const val Catalog = "catalog"
    const val Detail = "detail"
}
