package co.za.kasi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import co.za.kasi.databinding.ActivityFinancialDashBoardBinding
import co.za.kasi.enums.Financial
import co.za.kasi.R


@Suppress("DEPRECATION")
class FinancialDashBoardActivity : AppCompatActivity() {
    val source = "source"
    internal lateinit var binding: ActivityFinancialDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFinancialDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleNavigation(savedInstanceState)
    }

    fun navigateBack() {
        finish()
        overridePendingTransition(0, 0)
    }

    fun handleNavigation(savedInstanceState: Bundle?) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.financial_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val startFragment = intent.getStringExtra(source)

        val destination = when (startFragment) {
            Financial.RATES.name -> R.id.skynetRatesFragment
            Financial.REPORTS.name -> R.id.skyReportsVolumeFragment
            else -> null
        }

        if (savedInstanceState == null && destination != null) {
            navController.navigate(destination)
        }

        binding.imgVolume.setOnClickListener {
            navController.navigate(R.id.skyReportsVolumeFragment)
        }

        binding.imgRevenue.setOnClickListener {
            navController.navigate(R.id.skyReportsRevenueFragment)
        }

        binding.imgExpenses.setOnClickListener {
            navController.navigate(R.id.skyReportsExpensesFragment)
        }

        binding.backButton.setOnClickListener {
            navigateBack()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })
    }

    companion object {
        fun launchFinancialDashBoard(context: Context, source: String) {
            val options = ActivityOptionsCompat.makeCustomAnimation(context, 0, 0)
            val intent = Intent(context, FinancialDashBoardActivity::class.java)
            intent.putExtra("source", source)
            context.startActivity(intent, options.toBundle())
        }
    }
}