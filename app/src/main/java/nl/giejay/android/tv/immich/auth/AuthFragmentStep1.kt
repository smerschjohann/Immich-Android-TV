package nl.giejay.android.tv.immich.auth

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import nl.giejay.android.tv.immich.BuildConfig
import nl.giejay.android.tv.immich.R
import nl.giejay.android.tv.immich.shared.guidedstep.GuidedStepUtil.addAction
import nl.giejay.android.tv.immich.shared.guidedstep.GuidedStepUtil.addCheckedAction
import nl.giejay.android.tv.immich.shared.prefs.PreferenceManager
import timber.log.Timber



class AuthFragmentStep1 : GuidedStepSupportFragment() {
    private val ACTION_SIGN_IN = 0L
    private val ACTION_CONTINUE = 3L
    private var SELECTED_OPTION: Long = ACTION_SIGN_IN

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        val icon: Drawable =
            requireContext().getDrawable(R.drawable.icon)!!
        return GuidanceStylist.Guidance(
            "Immich TV (${BuildConfig.VERSION_NAME})",
            "Login to your Immich server.",
            "",
            icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        addCheckedAction(
            actions,
            ACTION_SIGN_IN,
            "Sign in by API key",
            "Login to your Immich instance by entering your API key.",
            true,
            1
        )
    }

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        super.onCreateButtonActions(actions, savedInstanceState)
        addAction(actions, ACTION_CONTINUE, "Continue", "")
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        super.onGuidedActionClicked(action)
        Timber.i("Clicked ${action.title} on step 1 of authentication, option: ${SELECTED_OPTION}")
        if (action.id == ACTION_CONTINUE) {
            val navController = findNavController()
            if (SELECTED_OPTION == ACTION_SIGN_IN) {
                if (navController.currentDestination?.id == R.id.authFragment) {
                    Timber.i("Navigating to step 2")
                    //https://stackoverflow.com/questions/51060762/illegalargumentexception-navigation-destination-xxx-is-unknown-to-this-navcontr
                    navController.navigate(AuthFragmentStep1Directions.actionAuthToAuth2())
                }
            }
        } else {
            SELECTED_OPTION = action.id
        }
    }
}