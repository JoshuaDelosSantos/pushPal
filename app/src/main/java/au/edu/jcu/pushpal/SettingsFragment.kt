package au.edu.jcu.pushpal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import au.edu.jcu.pushpal.databinding.FragmentSettingsBinding
import timber.log.Timber

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    // Initialise shared preference from other fragments.
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    companion object {
        private const val PROGRESS_BAR_ENABLED = "progress_bar_enabled"
        private const val DARK_THEME_ENABLED = "dark_theme_enabled"
        private const val GOAL_PUSH_UP_VALUE = "goalPushUpValue"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Settings onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("Settings onCreateView called")

        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupGoalSetter()
        setupSwitchListeners()
        setupHomeButtonListener()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        Timber.i("Settings onPause called")
        saveSettingsState()
    }

    override fun onResume() {
        super.onResume()
        Timber.i("Settings onResume called")
        restoreSettingsState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("Settings onSaveInstanceState called")
        outState.putInt(GOAL_PUSH_UP_VALUE, binding.goalSetterNumberPicker.value)
    }

    private fun setupGoalSetter() {
        binding.goalSetterNumberPicker.minValue = 0
        binding.goalSetterNumberPicker.maxValue = 100
        binding.goalSetterNumberPicker.wrapSelectorWheel = true
    }

    private fun setupSwitchListeners() {
        binding.progressSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showGoalSetter()
            } else {
                hideGoalSetter()
            }
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setTheme(isChecked)
        }
    }

    private fun setupHomeButtonListener() {
        binding.homeButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_settingsFragment_to_pushUpFragment)
        }
    }

    private fun showGoalSetter() {
        binding.goalSetter.visibility = View.VISIBLE
        binding.goalSetterNumberPicker.visibility = View.VISIBLE
    }

    private fun hideGoalSetter() {
        binding.goalSetter.visibility = View.GONE
        binding.goalSetterNumberPicker.visibility = View.GONE
    }

    private fun setTheme(isChecked: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun saveSettingsState() {
        with(sharedPreferences.edit()) {
            putBoolean(PROGRESS_BAR_ENABLED, binding.progressSwitch.isChecked)
            putBoolean(DARK_THEME_ENABLED, binding.themeSwitch.isChecked)
            putInt(GOAL_PUSH_UP_VALUE, binding.goalSetterNumberPicker.value)
            apply()
        }
    }

    private fun restoreSettingsState() {
        val isProgressBarEnabled = sharedPreferences.getBoolean(PROGRESS_BAR_ENABLED, false)
        val isDarkThemeEnabled = sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)

        binding.progressSwitch.isChecked = isProgressBarEnabled
        binding.themeSwitch.isChecked = isDarkThemeEnabled

        // Restore push up goal value.
        if (isProgressBarEnabled) {
            showGoalSetter()
            val goalPushUpValue = sharedPreferences.getInt(GOAL_PUSH_UP_VALUE, 0)
            binding.goalSetterNumberPicker.value = goalPushUpValue
        } else {
            hideGoalSetter()
        }

        setTheme(isDarkThemeEnabled)
    }
}
