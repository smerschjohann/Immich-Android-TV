/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.giejay.android.tv.immich

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import nl.giejay.android.tv.immich.shared.prefs.PreferenceManager
import nl.giejay.android.tv.immich.shared.viewmodel.KeyEventsViewModel
import timber.log.Timber


/**
 * FragmentActivity that displays the various fragments
 */
class MainActivity : FragmentActivity() {
    private lateinit var keyEventsModel: KeyEventsViewModel
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("Booting main activity")
        Timber.i("External files dir: ${getExternalFilesDir(null)}")

        setContentView(R.layout.activity_main)

        keyEventsModel = ViewModelProvider(this)[KeyEventsViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        loadDeepLinkOrStartingPage(intent.data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        keyEventsModel.postKeyEvent(event)
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Checks if the app was started with a deep link, loading it if it was
     *
     * If not (or the deep link is invalid), it triggers the normal starting process
     */
    private fun loadDeepLinkOrStartingPage(
        uri: Uri?
    ) {
        if (uri == null) {
            loadStartingPage()
            return
        }
    }

    /**
     * Chooses whether to show the browse screen or the "no Firebase" notice
     */
    private fun loadStartingPage() {
        if (!PreferenceManager.isLoggedId()) {
            Timber.i("Start page is authentication")
            navGraph.setStartDestination(R.id.authFragment)
        } else {
            Timber.i("Start page is home")
            navGraph.setStartDestination(R.id.homeFragment)
        }

        // Set the graph to trigger loading the start destination
        navController.graph = navGraph
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("Main activity got destroyed")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent == null) {
            return
        }

        // Include logic to process other types of intents here, if any.
    }

//    private fun loadPlaybackFragment(video: Video) {
//        // Set the default graph and go to playback for the loaded Video
//        navController.graph = navGraph
//        navController.navigate(
//            BrowseFragmentDirections.actionBrowseFragmentToPlaybackFragment(
//                video
//            )
//        )
//    }
}
