package tj.constitution.book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import tj.constitution.book.ui.screens.AboutScreen
import tj.constitution.book.ui.screens.BookmarksScreen
import tj.constitution.book.ui.screens.HomeScreen
import tj.constitution.book.ui.screens.ReaderScreen
import tj.constitution.book.ui.screens.SearchScreen
import tj.constitution.book.ui.screens.SettingsScreen
import tj.constitution.book.ui.screens.TableOfContentsScreen
import tj.constitution.book.ui.theme.KonstitutsiyaTheme

object Routes {
    const val HOME = "home"
    const val READER = "reader/{startPage}"
    const val TOC = "toc"
    const val SEARCH = "search"
    const val BOOKMARKS = "bookmarks"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    fun reader(startPage: Int) = "reader/$startPage"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()
            KonstitutsiyaTheme(readerTheme = settings.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavGraph(viewModel: AppViewModel) {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onOpenReader = { start -> navController.navigate(Routes.reader(start)) },
                onOpenToc = { navController.navigate(Routes.TOC) },
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenBookmarks = { navController.navigate(Routes.BOOKMARKS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenAbout = { navController.navigate(Routes.ABOUT) }
            )
        }
        composable(
            Routes.READER,
            arguments = listOf(navArgument("startPage") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val start = backStackEntry.arguments?.getInt("startPage") ?: 0
            ReaderScreen(
                viewModel = viewModel,
                startPage = start,
                onBack = { navController.popBackStack() },
                onOpenToc = { navController.navigate(Routes.TOC) },
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenBookmarks = { navController.navigate(Routes.BOOKMARKS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.TOC) {
            TableOfContentsScreen(
                viewModel = viewModel,
                onArticleSelected = { page ->
                    navController.navigate(Routes.reader(page)) { launchSingleTop = true }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel = viewModel,
                onResultSelected = { page ->
                    navController.navigate(Routes.reader(page)) { launchSingleTop = true }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.BOOKMARKS) {
            BookmarksScreen(
                viewModel = viewModel,
                onPageSelected = { page ->
                    navController.navigate(Routes.reader(page)) { launchSingleTop = true }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
