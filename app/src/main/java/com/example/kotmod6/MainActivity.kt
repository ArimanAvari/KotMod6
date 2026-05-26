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
import com.example.kotmod6.presentation.NobelViewModel
import com.example.kotmod6.presentation.detail.LaureateDetailScreen
import com.example.kotmod6.presentation.list.LaureateListScreen
import com.example.kotmod6.ui.theme.KotMod6Theme

class MainActivity : ComponentActivity() {
    private val viewModel: NobelViewModel by viewModels {
        NobelViewModel.factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KotMod6Theme {
                val navController = rememberNavController()
                val listState by viewModel.listState.collectAsState()
                val detailState by viewModel.detailState.collectAsState()
                val filterState by viewModel.filterState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = Routes.List
                ) {
                    composable(Routes.List) {
                        LaureateListScreen(
                            state = listState,
                            filterState = filterState,
                            onYearChange = viewModel::changeYear,
                            onCategoryChange = viewModel::changeCategory,
                            onApplyFilters = viewModel::loadLaureates,
                            onRetry = viewModel::loadLaureates,
                            onLaureateClick = { laureate ->
                                navController.navigate(
                                    "${Routes.Detail}/${laureate.laureateId}/${laureate.awardYear}/${laureate.categoryCode}"
                                )
                            }
                        )
                    }

                    composable(
                        route = "${Routes.Detail}/{laureateId}/{year}/{categoryCode}",
                        arguments = listOf(
                            navArgument("laureateId") { type = NavType.StringType },
                            navArgument("year") { type = NavType.StringType },
                            navArgument("categoryCode") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val id = entry.arguments?.getString("laureateId").orEmpty()
                        val year = entry.arguments?.getString("year").orEmpty()
                        val categoryCode = entry.arguments?.getString("categoryCode").orEmpty()

                        LaureateDetailScreen(
                            laureateId = id,
                            year = year,
                            categoryCode = categoryCode,
                            state = detailState,
                            onLoad = viewModel::loadLaureateDetail,
                            onRetry = { viewModel.loadLaureateDetail(id, year, categoryCode) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

private object Routes {
    const val List = "laureates"
    const val Detail = "laureate"
}
