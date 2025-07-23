package co.za.kasi.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import co.za.kasi.R;
import co.za.kasi.dialogs.Loader;
import co.za.kasi.model.driverOwner.Billing;
import co.za.kasi.model.driverOwner.ParcelBillingDTO;
import co.za.kasi.model.driverOwner.RatePerRouteBilling;
/*import co.za.skynet.services.DriverHttpService;
import co.za.skynet.util.Utils;*/
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolumeFragment extends Fragment {


    private final double mtdPenalties = 0.0;
    private final double mtdRevenue = 0.0;
    //Utils utils;
    View view;
    private String token;
    private String vehicleId;
    private double mtdaAountDue = 0.0;
    private double mtdProfit = 0.0;

    private JSONObject vehicle;
    private double mtdFuelSurcharge = 0.0;
    private int mtdTotalCollections = 0, mtdTotalDeliveries = 0, totalCollections = 0, totalDeliveries = 0;
    private Billing mtdReport;
    private ArrayList<RatePerRouteBilling> ratePerRouteBillings;
    private ArrayList<ParcelBillingDTO> parcelBillingDTOS;
    private DriverHttpService service;

    private Loader loader;
    private AppCompatTextView profit, fuelSurcharge;
    private AppCompatTextView revenueAmnt, penaltiesAmnt, expensesAmnt;
    private AppCompatTextView tvCollectionsToday, tvCollectionsTotal, tvDeliveriesToday, tvDeliveriesTotal, tvVolume;

    public VolumeFragment(String token, String vehicleId) {
        this.token = token;
        this.vehicleId = vehicleId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_volume_fragment, container, false);
        init();


        return view;
    }

    private void init() {

        profit = view.findViewById(R.id.tvProfitAmnt);
        fuelSurcharge = view.findViewById(R.id.tvFuelSurchargePerc);
        expensesAmnt = view.findViewById(R.id.tvExpensesAmnt);
        penaltiesAmnt = view.findViewById(R.id.tvPenaltyAmnt);
        revenueAmnt = view.findViewById(R.id.tvRevenueAmnt);

        tvCollectionsToday = view.findViewById(R.id.tvCollectionsToday);
        tvCollectionsTotal = view.findViewById(R.id.tvCollections);
        tvDeliveriesToday = view.findViewById(R.id.tvDeliveriesToday);
        tvDeliveriesTotal = view.findViewById(R.id.tvDeliveries);
        tvVolume = view.findViewById(R.id.tvTotalVolume);

        // vehicle = new JSONObject()

        service = ReusableFunctions.initiateRetrofit(getContext());

        ratePerRouteBillings = new ArrayList<>();
        parcelBillingDTOS = new ArrayList<>();
        getLocalData();
        // utils = new Utils();
        //getReport(token,vehicleId);

        loader = ReusableFunctions.showLoader(getContext());
        int delayMillis = 3000; // 3 seconds

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataHardCode();
            }
        }, delayMillis);

    }

    private void dataHardCode() {
        ReusableFunctions.hideLoader(loader);
        tvCollectionsToday.setText("Today " + 45);
        tvCollectionsTotal.setText("Total " + 246);
        tvDeliveriesToday.setText("Today " + 65);
        tvDeliveriesTotal.setText("Total " + 410);
        tvVolume.setText("656");

        revenueAmnt.setText("R 14325.65");
        fuelSurcharge.setText("R 1200.25");
        expensesAmnt.setText("R 2385.00");
        penaltiesAmnt.setText("R 0.00");
        profit.setText("R 13140.90");
    }

    private void getLocalData() {
        token = LocalStorage.getToken(getContext());
        vehicleId = LocalStorage.getDriverAccount().getVehicle().getOid();
        Log.e("vehicleID", "------------------------Vehicle ID: " + vehicleId);
        Log.e("vehicleID", "------------------------Token: " + token);

    }

    private void getReport(String token, String vehicleOid) {

        Call<Billing> getSummery = service.getMTDReport(token, vehicleOid);

        getSummery.enqueue(new Callback<Billing>() {
            @Override
            public void onResponse(Call<Billing> call, Response<Billing> response) {

                Log.d("PAYABLEAMT", vehicleOid);
                if (response.code() == 200) {
                    mtdReport = response.body();

                    ratePerRouteBillings = (ArrayList<RatePerRouteBilling>) mtdReport.getBillings();
                    //record2.addAll(ratePerRouteBillings);

                    CalculateProfit();
                } else {

                    try {
                        Log.d("PAYABLEAMT", response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("PAYABLEAMT-", e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Billing> call, Throwable t) {
                //progressDialog.dismiss();
                Log.e("PAYABLEAMT_", t.getMessage(), t);
            }
        });

    }

    private void CalculateProfit() {

        int pos = 0;
        for (int i = 0; i < ratePerRouteBillings.size(); i++) {
            parcelBillingDTOS = (ArrayList<ParcelBillingDTO>) mtdReport.getBillings().get(pos).getParcels();
            for (int k = 0; k < parcelBillingDTOS.size(); k++) {
                if (i == 0 && k == 0) {
                    totalCollections += parcelBillingDTOS.get(k).getCollections();
                    totalDeliveries += parcelBillingDTOS.get(k).getDelivered();
                }
                mtdTotalCollections += parcelBillingDTOS.get(k).getCollections();
                mtdTotalDeliveries += parcelBillingDTOS.get(k).getDelivered();
            }
            mtdFuelSurcharge += ratePerRouteBillings.get(i).getFuelCharge();
            mtdaAountDue += ratePerRouteBillings.get(i).getTotalDue();
        }
        tvCollectionsToday.setText("Today " + totalCollections);
        tvCollectionsTotal.setText("Total " + mtdTotalCollections);
        tvDeliveriesToday.setText("Today " + totalDeliveries);
        tvDeliveriesTotal.setText("Total " + mtdTotalDeliveries);

        // revenueAmnt.setText("R " + utils.decimalFormater(mtdaAountDue));
        // fuelSurcharge.setText("R " + utils.decimalFormater( mtdFuelSurcharge));
        mtdProfit = mtdaAountDue;
        // profit.setText("R " + utils.decimalFormater( mtdProfit));

        int volume = mtdTotalCollections + mtdTotalDeliveries;
        tvVolume.setText(String.valueOf(volume));

    }
}
