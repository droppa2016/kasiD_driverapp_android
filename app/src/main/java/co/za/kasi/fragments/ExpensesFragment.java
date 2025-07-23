package co.za.kasi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.za.kasi.R;


public class ExpensesFragment extends Fragment {

    private String token, vehicleId;

    public ExpensesFragment(String token, String vehicleId) {
        this.token = token;
        this.vehicleId = vehicleId;
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_expenses_fragment, container, false);

        return view;
    }
}
