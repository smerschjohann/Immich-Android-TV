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

import android.app.Application
import android.content.Context
import nl.giejay.android.tv.immich.shared.prefs.PreferenceManager
import timber.log.Timber
import java.util.UUID


/**
 * Initializes libraries, such as Timber, and sets up application wide settings.
 */
class ImmichApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this
        PreferenceManager.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        /**
         * This initialises an instance of the CastReceiverContext object which is needed to
         * interact with Cast while in the TV app. This object allows for passing media signals
         * and the data load and so needs to exist while the app is in the foreground so that all
         * cast commands can be picked up by the TV App.
         */
        var userId = PreferenceManager.getUserId()
        if(userId.isBlank()){
            userId = UUID.randomUUID().toString()
            PreferenceManager.setUserId(userId)
        }
    }


    companion object {
        var appContext: Context? = null
    }

}
