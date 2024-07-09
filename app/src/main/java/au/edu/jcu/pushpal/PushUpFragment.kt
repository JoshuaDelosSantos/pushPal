package au.edu.jcu.pushpal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import au.edu.jcu.pushpal.databinding.FragmentPushUpBinding
import timber.log.Timber

class PushUpFragment : Fragment() {
    private lateinit var binding: FragmentPushUpBinding
    private var pushUpCount = 0
    private lateinit var pushUpCounter: TextView

    // Initialise shared preference from other fragments.
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    companion object {
        private const val PUSH_UP_COUNT = "pushUpCount"
        private const val PROGRESS_BAR_ENABLED = "progress_bar_enabled"
        private const val DARK_THEME_ENABLED = "dark_theme_enabled"
        private const val GOAL_PUSH_UP_VALUE = "goalPushUpValue"
        private const val LOW_PROG_THRESHOLD = 0.25
        private const val MID_PROG_THRESHOLD = 0.75
        private const val HIGH_PROG_THRESHOLD = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("PushUpFragment onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("PushUpFragment onCreateView called")
        binding = FragmentPushUpBinding.inflate(inflater, container, false)

        initialiseViews()
        setupButtonListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Timber.i("PushUpFragment onResume called")
        applyThemeSettings()
        restoreSavedInstanceState()
        updateProgressBar()
    }

    override fun onPause() {
        super.onPause()
        Timber.i("PushUpFragment onPause called")
        savePushUpCount()
    }

    private fun restoreSavedInstanceState() {
        pushUpCount = sharedPreferences.getInt(PUSH_UP_COUNT, 0)
        updatePushUpCounterText()
    }

    private fun initialiseViews() {
        pushUpCounter = binding.pushUpCounter
        setupProgressBar()
    }

    private fun setupButtonListeners() {
        binding.pushUpButton.setOnClickListener {
            incrementPushUpCounter()
            updateProgressBar()
        }

        binding.restartPushUpCounterButton.setOnClickListener {
            handleRestartButtonPress()
            updateProgressBar()
        }

        binding.toSettingsButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_pushUpFragment_to_settingsFragment)
        }
    }

    private fun setupProgressBar() {
        val isProgressBarEnabled = sharedPreferences.getBoolean(PROGRESS_BAR_ENABLED, false)

        if (isProgressBarEnabled) {
            binding.progressBar.visibility = View.VISIBLE
            binding.progressBar.max = sharedPreferences.getInt(GOAL_PUSH_UP_VALUE, 0)

            updateProgressBar()
        } else {
            binding.progressBar.visibility = View.GONE
        }

    }

    private fun updateProgressBar() {
        if (binding.progressBar.visibility == View.VISIBLE) {
            binding.progressBar.progress = pushUpCount

            val proBarMaxValue = binding.progressBar.max
            val progRatio = pushUpCount.toDouble() / proBarMaxValue

            // Change progress bar colour according to progress ratio compared to max value.
            when {
                progRatio < LOW_PROG_THRESHOLD -> {
                    binding.progressBar.progressTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.red_progress)
                }

                progRatio < MID_PROG_THRESHOLD -> {
                    binding.progressBar.progressTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.orange_progress)
                }

                progRatio < HIGH_PROG_THRESHOLD -> {
                    binding.progressBar.progressTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.green)
                }

                else -> {
                    binding.progressBar.progressTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.blue_progress)
                }
            }

        }
    }

    private fun applyThemeSettings() {
        val isDarkThemeEnabled = sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun incrementPushUpCounter() {
        Timber.i("Push up button pressed")
        pushUpCount++
        updatePushUpCounterText()
    }

    private fun handleRestartButtonPress() {
        Timber.i("Restart button pressed")
        pushUpCount = 0
        updatePushUpCounterText()
    }

    private fun updatePushUpCounterText() {
        Timber.i("PushUpCounterText updated")
        if (pushUpCount == 0) {
            pushUpCounter.text = getString(R.string.push_up_prompt)
        } else {
            pushUpCounter.text = pushUpCount.toString()
        }
    }

    private fun savePushUpCount() {
        Timber.i("PushUpCount saved")
        sharedPreferences.edit().putInt(PUSH_UP_COUNT, pushUpCount).apply()
    }
}
