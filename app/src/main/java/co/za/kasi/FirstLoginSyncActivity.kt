package co.za.kasi

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.za.kasi.databinding.ActivityFirstLoginSyncBinding
import co.za.kasi.fragments.SkyNetNewSyncFragment
import co.za.kasi.R

class FirstLoginSyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstLoginSyncBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstLoginSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            val systemBarsInsets = insets.getInsets(android.view.WindowInsets.Type.systemBars())
            view.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            insets
        }

        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.skynet_color))
        window.statusBarColor = ContextCompat.getColor(this, R.color.skynet_color)

        if (savedInstanceState == null) {
            val source = "FIRST_LOGIN"
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SkyNetNewSyncFragment.newInstance(source))
                .commit()
        }
    }

    companion object {
        fun launchSyncActivity(context: Context) {
            val intent = Intent(context, FirstLoginSyncActivity::class.java)
            context.startActivity(intent)
        }
    }
}