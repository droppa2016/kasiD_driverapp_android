package co.za.kasi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.za.kasi.FinancialDashBoardActivity
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkyReportsExpensesBinding

class SkyNetReportsExpensesFragment : Fragment() {
    private lateinit var binding: FragmentSkyReportsExpensesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyReportsExpensesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as FinancialDashBoardActivity).binding.let {
            it.volumeArea.setBackgroundResource(R.color.card_background)
            it.expensesArea.setBackgroundResource(R.color.app_background)
            it.revenueArea.setBackgroundResource(R.color.card_background)

        }

    }
}