package nl.giejay.android.tv.immich.settings

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.navigation.fragment.findNavController
import nl.giejay.android.tv.immich.home.HomeFragmentDirections
import nl.giejay.android.tv.immich.shared.donate.DonateService
import timber.log.Timber


class SettingsFragment : RowsSupportFragment() {
    private val mRowsAdapter: ArrayObjectAdapter
    private lateinit var donateService: DonateService

    private val settingsResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // This block is executed when the launched activity (Settings) returns.
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.tag("SettingsFragment").d("Settings activity returned RESULT_OK")
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Timber.tag("SettingsFragment").d("Settings activity returned cancelled")
            }
        }

    init {
        val selector = ListRowPresenter()
        selector.setNumRows(1)
        mRowsAdapter = ArrayObjectAdapter(selector)
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            val card = item as SettingsCard
            card.onClick()
        }
        adapter = mRowsAdapter
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        donateService = DonateService(activity)
        loadData()
    }

    private fun loadData() {
        if (isAdded) {
            mRowsAdapter.add(
                createCardRow(
                    listOf(
                        SettingsCard(
                            "Server",
                            null,
                            "server",
                            "ic_settings_settings",
                            "ic_settings_settings"
                        ) {
                            findNavController().navigate(
                                HomeFragmentDirections.actionGlobalSignInFragment()
                            )
                        },
                        SettingsCard(
                            "View settings",
                            null,
                            "view_settings",
                            "icon_view",
                            "icon_view"
                        ) {
                            findNavController().navigate(
                                HomeFragmentDirections.actionGlobalToSettingsDialog("view")
                            )
                        },
                        SettingsCard(
                            "Screensaver",
                            null,
                            "screensaver",
                            "screensaver",
                            "ic_settings_settings"
                        ) {
                            findNavController().navigate(
                                HomeFragmentDirections.actionGlobalToSettingsDialog("screensaver")
                            )
                        },
                        SettingsCard(
                            "Debug",
                            null,
                            "debug",
                            "bug",
                            "bug"
                        ) {
                            findNavController().navigate(
                                HomeFragmentDirections.actionGlobalToSettingsDialog("debug")
                            )
                        },
                        SettingsCard(
                            "Android Settings",
                            null,
                            "android_settings",
                            "ic_settings_settings",
                            "ic_settings_settings"
                        ) {
                            settingsResultLauncher.launch(Intent(android.provider.Settings.ACTION_SETTINGS))
                        },
                        SettingsCard(
                            "Donate",
                            null,
                            "donate",
                            "donate",
                            "donate",
//                            donateService.isInitialized()
                        ) {
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeToDonate()
                            )
                        }
                    )
                )
            )
            mainFragmentAdapter.fragmentHost?.notifyDataReady(
                mainFragmentAdapter
            )
        }
    }

    private fun createCardRow(cards: List<SettingsCard>): ListRow {
        val iconCardPresenter = SettingsIconPresenter(requireContext())
        val adapter = ArrayObjectAdapter(iconCardPresenter)
        adapter.addAll(0, cards.filter { it.visible })
        val headerItem = HeaderItem("Settings")
        return ListRow(headerItem, adapter)
    }
}