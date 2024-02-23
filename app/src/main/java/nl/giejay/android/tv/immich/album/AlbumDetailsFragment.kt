package nl.giejay.android.tv.immich.album

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.zeuskartik.mediaslider.MediaSliderConfiguration
import nl.giejay.android.tv.immich.api.ApiClient
import nl.giejay.android.tv.immich.api.model.AlbumDetails
import nl.giejay.android.tv.immich.api.model.Asset
import nl.giejay.android.tv.immich.api.util.ApiUtil
import nl.giejay.android.tv.immich.card.Card
import nl.giejay.android.tv.immich.home.HomeFragmentDirections
import nl.giejay.android.tv.immich.shared.db.LocalStorage
import nl.giejay.android.tv.immich.shared.fragment.VerticalCardGridFragment
import nl.giejay.android.tv.immich.shared.prefs.LiveSharedPreferences
import nl.giejay.android.tv.immich.shared.prefs.PreferenceManager
import nl.giejay.android.tv.immich.shared.util.toCard
import nl.giejay.android.tv.immich.shared.util.toCards
import nl.giejay.android.tv.immich.shared.util.toSliderItems


class AlbumDetailsFragment : VerticalCardGridFragment<Asset>() {
    private var currentAlbum: AlbumDetails? = null
    private var livePref = LiveSharedPreferences(PreferenceManager.sharedPreference).getString("photos_sorting", PreferenceManager.photosOrder().toString())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        livePref.observe(viewLifecycleOwner) { _ ->
            assets = sortItems(assets)
            adapter.clear()
            adapter.addAll(0, assets.toCards())
        }
    }

    override fun sortItems(items: List<Asset>): List<Asset> {
        return items.sortedWith(PreferenceManager.photosOrder().sort)
    }

    override suspend fun loadItems(apiClient: ApiClient, page: Int, pageCount: Int): Either<String, List<Asset>> {
        if(page == startPage){
            // no pagination possible yet!
            val albumId = AlbumDetailsFragmentArgs.fromBundle(requireArguments()).albumId
            return apiClient.listAssetsFromAlbum(albumId).map {
                currentAlbum = it
                it.assets
            }
        }
        return Either.Right(emptyList())
    }

    override fun onItemSelected(card: Card, indexOf: Int) {
        // no use case yet
    }

    override fun openPopUpMenu() {
        findNavController().navigate(
            HomeFragmentDirections.actionGlobalToSettingsDialog("album_details")
        )
    }

    override fun onItemClicked(card: Card) {
        // todo find a better way to pass data to other fragment without using the Intent extras (possibly too large)
        LocalStorage.mediaSliderItems = assets.toSliderItems()
        findNavController().navigate(
            AlbumDetailsFragmentDirections.actionDetailsToPhotoSlider(
                MediaSliderConfiguration(
                    PreferenceManager.sliderShowDescription(),
                    PreferenceManager.sliderShowMediaCount(),
                    false,
                    currentAlbum!!.albumName,
                    "",
                    "",
                    adapter.indexOf(card),
                    PreferenceManager.sliderInterval(),
                    PreferenceManager.sliderOnlyUseThumbnails()
                )
            )
        )
    }

    override fun getBackgroundPicture(it: Asset): String? {
        return ApiUtil.getFileUrl(it.id)
    }

    override fun setTitle(response: List<Asset>) {
        title = currentAlbum?.albumName
    }

    override fun createCard(a: Asset): Card {
       return a.toCard()
    }
}