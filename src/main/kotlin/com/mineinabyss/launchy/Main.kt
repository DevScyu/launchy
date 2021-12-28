package com.mineinabyss.launchy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mineinabyss.launchy.data.Config
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Versions
import com.mineinabyss.launchy.logic.LaunchyState
import com.mineinabyss.launchy.ui.Browser
import com.mineinabyss.launchy.ui.screens.MainScreen
import edu.stanford.ejalbert.BrowserLauncher

//private val LocalConfigProvider = compositionLocalOf<Config> { error("No local config provided") }
//val LocalConfig: Config
//    @Composable
//    get() = LocalConfigProvider.current
//
//private val LocalVersionsProvider = compositionLocalOf<Versions> { error("No local versions provided") }
//val LocalVersions: Versions
//    @Composable
//    get() = LocalVersionsProvider.current
private val LaunchyStateProvider = compositionLocalOf<LaunchyState> { error("No local versions provided") }
val LocalLaunchyState: LaunchyState
    @Composable
    get() = LaunchyStateProvider.current


fun main() {
    application {
        val scaffoldState = rememberScaffoldState()
        val launchyState by produceState<LaunchyState?>(null) {
            val config = Config.read()
            val versions = Versions.readLatest(config.downloadUpdates)
            value = LaunchyState(config, versions, scaffoldState)
        }
        Window(onCloseRequest = {
            exitApplication()
            launchyState?.save()
        }) {
            val ready = launchyState != null
            MaterialTheme(colors = darkColors()) {
                Scaffold(scaffoldState = scaffoldState) {
                    AnimatedVisibility(!ready, exit = fadeOut()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Getting latest plugin versions...")
                        }
                    }
                    AnimatedVisibility(ready, enter = fadeIn()) {
                        CompositionLocalProvider(
                            LaunchyStateProvider provides launchyState!!,
                        ) {
                            Dirs.createDirs()
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}
