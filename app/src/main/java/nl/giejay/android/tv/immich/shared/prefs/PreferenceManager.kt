package nl.giejay.android.tv.immich.shared.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import nl.giejay.android.tv.immich.screensaver.ScreenSaverType
import nl.giejay.mediaslider.transformations.GlideTransformations
import okhttp3.HttpUrl

object PreferenceManager {
    lateinit var sharedPreference: SharedPreferences
    private lateinit var liveSharedPreferences: LiveSharedPreferences
    private val liveContext: MutableMap<String, Any> = mutableMapOf()

    // wakelock
    val KEY_WAKE_LOCK_MINUTES = "wake_lock_minutes"

    // host settings
    val KEY_HOST_NAME = "hostName"
    val KEY_API_KEY = "apiKey"
    val KEY_DISABLE_SSL_VERIFICATION = "disableSSLVerification"

    // screensaver settings
    private val KEY_SCREENSAVER_INTERVAL = "screensaver_interval"
    private val KEY_SCREENSAVER_SHOW_MEDIA_COUNT = "screensaver_show_media_count"
    private val KEY_SCREENSAVER_SHOW_DESCRIPTION = "screensaver_show_description"
    private val KEY_SCREENSAVER_SHOW_ALBUM_NAME = "screensaver_show_album_name"
    private val KEY_SCREENSAVER_SHOW_DATE = "screensaver_show_date"
    private val KEY_SCREENSAVER_SHOW_CLOCK = "screensaver_show_clock"
    private val KEY_SCREENSAVER_ANIMATE_ASSET_SLIDE = "screensaver_animate_asset_slide"
    private val KEY_SCREENSAVER_ALBUMS = "screensaver_albums"
    private val KEY_SCREENSAVER_INCLUDE_VIDEOS = "screensaver_include_videos"
    private val KEY_SCREENSAVER_VIDEO_SOUND = "screensaver_play_sound"
    private val KEY_SCREENSAVER_TYPE = "screensaver_type"

    // slider/view settings
    private val KEY_SLIDER_INTERVAL = "slider_interval"
    private val KEY_SLIDER_ANIMATION_SPEED = "slider_animation_speed"
    private val KEY_SLIDER_SHOW_DESCRIPTION = "slider_show_description"
    private val KEY_SLIDER_SHOW_MEDIA_COUNT = "slider_show_media_count"
    private val KEY_SLIDER_SHOW_DATE = "slider_show_date"
    private val KEY_SLIDER_SHOW_CITY = "slider_show_city"
    private val KEY_SLIDER_ONLY_USE_THUMBNAILS = "slider_only_use_thumbnails"
    private val KEY_SLIDER_MERGE_PORTRAIT_PHOTOS = "slider_merge_portrait_photos"
    private val KEY_MAX_CUT_OFF_WIDTH = "slider_max_cut_off_width"
    private val KEY_MAX_CUT_OFF_HEIGHT = "slider_max_cut_off_height"
    private val KEY_GLIDE_TRANSFORMATION = "slider_glide_transformation"

    // sorting
    val KEY_ALBUMS_SORTING = "albums_sorting"
    private val KEY_PHOTOS_SORTING = "photos_sorting"
    private val KEY_ALL_ASSETS_SORTING = "all_assets_sorting"
//    private val KEY_ALBUMS_SORTING_REVERSE = "albums_sorting_reverse"
//    private val KEY_PHOTOS_SORTING_REVERSE = "photos_sorting_reverse"

    // other
    val KEY_DEBUG_MODE = "debug_mode"
    val KEY_HIDDEN_HOME_ITEMS = "hidden_home_items"
    val KEY_SIMILAR_ASSETS_YEARS_BACK = "similar_assets_years_back"
    val KEY_SIMILAR_ASSETS_PERIOD_DAYS = "similar_assets_period_days"
    val KEY_RECENT_ASSETS_MONTHS_BACK = "recent_assets_months_back"
    private val KEY_USER_ID = "user_id"

    private val propsToWatch = mapOf(
        KEY_WAKE_LOCK_MINUTES to 15,
        KEY_HOST_NAME to "",
        KEY_API_KEY to "",
        KEY_DISABLE_SSL_VERIFICATION to false,
        KEY_SCREENSAVER_INTERVAL to "3",
        KEY_SLIDER_INTERVAL to "3",
        KEY_SLIDER_SHOW_DESCRIPTION to true,
        KEY_SLIDER_SHOW_MEDIA_COUNT to true,
        KEY_SLIDER_SHOW_DATE to false,
        KEY_SLIDER_SHOW_CITY to true,
        KEY_SCREENSAVER_SHOW_DESCRIPTION to true,
        KEY_SCREENSAVER_SHOW_ALBUM_NAME to true,
        KEY_SCREENSAVER_SHOW_DATE to true,
        KEY_SCREENSAVER_SHOW_CLOCK to true,
        KEY_SCREENSAVER_ANIMATE_ASSET_SLIDE to true,
        KEY_SCREENSAVER_SHOW_MEDIA_COUNT to true,
        KEY_SCREENSAVER_ALBUMS to mutableSetOf<String>(),
        KEY_SCREENSAVER_INCLUDE_VIDEOS to false,
        KEY_SCREENSAVER_VIDEO_SOUND to false,
        KEY_SCREENSAVER_TYPE to ScreenSaverType.RECENT.toString(),
        KEY_DEBUG_MODE to false,
        KEY_ALBUMS_SORTING to AlbumsOrder.LAST_UPDATED.toString(),
        KEY_PHOTOS_SORTING to PhotosOrder.OLDEST_NEWEST.toString(),
        KEY_ALL_ASSETS_SORTING to PhotosOrder.NEWEST_OLDEST.toString(),
//        KEY_ALBUMS_SORTING_REVERSE to false,
//        KEY_PHOTOS_SORTING_REVERSE to false,
        KEY_SLIDER_ONLY_USE_THUMBNAILS to true,
        KEY_SLIDER_MERGE_PORTRAIT_PHOTOS to true,
        KEY_HIDDEN_HOME_ITEMS to emptySet<String>(),
        KEY_SIMILAR_ASSETS_YEARS_BACK to 10,
        KEY_RECENT_ASSETS_MONTHS_BACK to 5,
        KEY_SIMILAR_ASSETS_PERIOD_DAYS to 30,
        KEY_SLIDER_ANIMATION_SPEED to 0,
        KEY_MAX_CUT_OFF_HEIGHT to 20,
        KEY_MAX_CUT_OFF_WIDTH to 20
    )

    fun init(context: Context) {
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        liveSharedPreferences = LiveSharedPreferences(sharedPreference)
        propsToWatch.forEach { (key, defaultValue) ->
            liveSharedPreferences.subscribe(key, defaultValue) { value ->
                liveContext[key] = value
            }
        }
    }

    fun wakeLockMinutes(): Int {
        val value = liveContext[KEY_WAKE_LOCK_MINUTES].toString()
        return if(value == "always on") {
            0
        } else {
            value.toInt()
        }
    }

    fun apiKey(): String {
        return liveContext[KEY_API_KEY] as String
    }

    fun hostName(): String {
        return liveContext[KEY_HOST_NAME] as String
    }

    fun screensaverInterval(): Int {
        return liveContext[KEY_SCREENSAVER_INTERVAL]?.toString()?.toInt() ?: 3
    }

    fun screensaverShowDescription(): Boolean {
        return liveContext[KEY_SCREENSAVER_SHOW_DESCRIPTION] as Boolean
    }

    fun screensaverShowMediaCount(): Boolean {
        return liveContext[KEY_SCREENSAVER_SHOW_MEDIA_COUNT] as Boolean
    }

    fun screensaverShowAlbumName(): Boolean {
        return liveContext[KEY_SCREENSAVER_SHOW_ALBUM_NAME] as Boolean
    }

    fun screensaverShowDate(): Boolean {
        return liveContext[KEY_SCREENSAVER_SHOW_DATE] as Boolean
    }

    fun screensaverShowClock(): Boolean {
        return liveContext[KEY_SCREENSAVER_SHOW_CLOCK] as Boolean
    }

    fun enableSlideAnimation(): Boolean {
        return liveContext[KEY_SCREENSAVER_ANIMATE_ASSET_SLIDE] as Boolean
    }

    fun screensaverIncludeVideos(): Boolean {
        return liveContext[KEY_SCREENSAVER_INCLUDE_VIDEOS] as Boolean
    }

    fun screensaverVideoSound(): Boolean {
        return liveContext[KEY_SCREENSAVER_VIDEO_SOUND] as Boolean
    }

    fun sliderInterval(): Int {
        return liveContext[KEY_SLIDER_INTERVAL]?.toString()?.toInt() ?: 3
    }

    fun sliderShowDescription(): Boolean {
        return liveContext[KEY_SLIDER_SHOW_DESCRIPTION] as Boolean
    }

    fun sliderShowMediaCount(): Boolean {
        return liveContext[KEY_SLIDER_SHOW_MEDIA_COUNT] as Boolean
    }

    fun sliderShowDate(): Boolean {
        return liveContext[KEY_SLIDER_SHOW_DATE] as Boolean
    }

    fun sliderOnlyUseThumbnails(): Boolean {
        return liveContext[KEY_SLIDER_ONLY_USE_THUMBNAILS] as Boolean
    }

    fun sliderMergePortraitPhotos(): Boolean {
        return liveContext[KEY_SLIDER_MERGE_PORTRAIT_PHOTOS] as Boolean
    }

    fun saveApiKey(value: String) {
        saveString(KEY_API_KEY, value)
    }

    fun saveHostName(value: String) {
        saveString(KEY_HOST_NAME, value.replace(Regex("/$"), ""))
    }

    fun saveSslVerification(value: Boolean) {
        saveBoolean(KEY_DISABLE_SSL_VERIFICATION, value)
    }

    fun isLoggedId(): Boolean {
        return isValid(hostName(), apiKey())
    }

    fun isValid(hostName: String?, apiKey: String?): Boolean {
        return hostName?.isNotBlank() == true && apiKey?.isNotBlank() == true && HttpUrl.parse(hostName) != null
    }

    fun removeApiSettings() {
        saveString(KEY_HOST_NAME, "")
        saveString(KEY_API_KEY, "")
    }

    fun getScreenSaverAlbums(): Set<String> {
        return liveContext[KEY_SCREENSAVER_ALBUMS] as Set<String>
    }

    fun getScreenSaverType(): ScreenSaverType {
        return ScreenSaverType.valueOf(liveContext[KEY_SCREENSAVER_TYPE] as String)
    }

    fun saveScreenSaverAlbums(strings: Set<String>) {
        saveStringSet(KEY_SCREENSAVER_ALBUMS, strings)
        liveContext[KEY_SCREENSAVER_ALBUMS] = strings
    }

    fun disableSslVerification(): Boolean {
        return liveContext[KEY_DISABLE_SSL_VERIFICATION] as Boolean
    }

    fun debugEnabled(): Boolean {
        return liveContext[KEY_DEBUG_MODE] as Boolean
    }

    fun saveDebugMode(debugMode: Boolean) {
        liveContext[KEY_DEBUG_MODE] = debugMode
        saveBoolean(KEY_DEBUG_MODE, debugMode)
    }

    fun hiddenHomeItems(): Set<String> {
        return liveContext[KEY_HIDDEN_HOME_ITEMS] as Set<String>
    }

    fun removeHiddenHomeItem(item: String) {
        saveStringSet(KEY_HIDDEN_HOME_ITEMS, hiddenHomeItems().filter { it != item }.toSet())
    }

    fun addHiddenHomeItem(item: String) {
        saveStringSet(KEY_HIDDEN_HOME_ITEMS, hiddenHomeItems() + item)
    }

    fun getUserId(): String {
        return getString(KEY_USER_ID, "")
    }

    fun setUserId(userId: String) {
        saveString(KEY_USER_ID, userId)
    }

    fun albumsOrder(): AlbumsOrder {
        return AlbumsOrder.valueOfSafe(
            liveContext[KEY_ALBUMS_SORTING] as String,
            AlbumsOrder.LAST_UPDATED
        )
    }

    fun photosOrder(): PhotosOrder {
        return PhotosOrder.valueOfSafe(
            liveContext[KEY_PHOTOS_SORTING] as String,
            PhotosOrder.OLDEST_NEWEST
        )
    }

    fun allAssetsOrder(): PhotosOrder {
        return PhotosOrder.valueOfSafe(
            liveContext[KEY_ALL_ASSETS_SORTING] as String,
            PhotosOrder.NEWEST_OLDEST
        )
    }

    fun saveSortingForAlbum(albumId: String, value: String) {
        saveString(keyAlbumsSorting(albumId), value)
    }

    fun getSortingForAlbum(albumId: String): PhotosOrder {
        return PhotosOrder.valueOfSafe(
            getString(keyAlbumsSorting(albumId), photosOrder().toString()),
            photosOrder()
        )
    }

    fun keyAlbumsSorting(albumId: String): String {
        return "photos_sorting_${albumId}"
    }

//    fun reversePhotosOrder(): Boolean {
//        return liveContext[KEY_PHOTOS_SORTING_REVERSE] as Boolean
//    }
//
//    fun reverseAlbumsOrder(): Boolean {
//        return liveContext[KEY_ALBUMS_SORTING_REVERSE] as Boolean
//    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return sharedPreference.getBoolean(key, default)
    }

    private fun saveString(key: String, value: String) {
        liveContext[key] = value
        sharedPreference.edit().putString(key, value).apply()
    }

    private fun saveBoolean(key: String, value: Boolean) {
        liveContext[key] = value
        sharedPreference.edit().putBoolean(key, value).apply()
    }

    private fun saveStringSet(key: String, value: Set<String>) {
        liveContext[key] = value
        sharedPreference.edit().putStringSet(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreference.getString(key, defaultValue) ?: defaultValue
    }

    fun toggleHiddenHomeItem(name: String) {
        if(hiddenHomeItems().contains(name)){
            removeHiddenHomeItem(name)
        } else {
            addHiddenHomeItem(name)
        }
    }

    fun isHomeItemHidden(name: String?): Boolean {
        return name?.let { hiddenHomeItems().contains(it) } ?: false
    }

    fun similarAssetsYearsBack(): Int {
        return Integer.parseInt(liveContext[KEY_SIMILAR_ASSETS_YEARS_BACK].toString())
    }

    fun similarAssetsPeriodDays(): Int {
        return Integer.parseInt(liveContext[KEY_SIMILAR_ASSETS_PERIOD_DAYS].toString())
    }

    fun recentAssetsMonthsBack(): Int {
        return Integer.parseInt(liveContext[KEY_RECENT_ASSETS_MONTHS_BACK].toString())
    }

    fun sliderShowCity(): Boolean {
        return liveContext[KEY_SLIDER_SHOW_CITY] as Boolean
    }

    fun animationSpeedMillis(): Int {
        return liveContext[KEY_SLIDER_ANIMATION_SPEED].toString().toInt()
    }

    fun maxCutOffWidth(): Int {
        return liveContext[KEY_MAX_CUT_OFF_WIDTH].toString().toInt()
    }

    fun maxCutOffHeight(): Int {
        return liveContext[KEY_MAX_CUT_OFF_HEIGHT].toString().toInt()
    }

    fun glideTransformation(): GlideTransformations {
        return GlideTransformations.valueOfSafe(
            getString(KEY_GLIDE_TRANSFORMATION, GlideTransformations.CENTER_INSIDE.toString()),
            GlideTransformations.CENTER_INSIDE
        )
    }
}