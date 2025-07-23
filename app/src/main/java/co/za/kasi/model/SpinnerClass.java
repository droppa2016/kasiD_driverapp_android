package co.za.kasi.model;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class SpinnerClass implements AdapterView.OnItemSelectedListener {

    private Spinner spinProvince;

    private List<String> provinces;

    private  ArrayAdapter<String> adapter;

    private Context context;

    public SpinnerClass(Context context,Spinner spinProvince, List<String> provinces,ArrayAdapter<String> adapter) {
        this.spinProvince = spinProvince;
        this.provinces = provinces;
        this.adapter =  adapter;
        this.context = context;




    }

    //new ArrayAdapter<String>(, simple_spinner_item, this.provinces);




    public Spinner getSpinProvince() {
        return spinProvince;
    }

    public void setSpinProvince(Spinner spinProvince) {
        this.spinProvince = spinProvince;
    }

    public List<String> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<String> provinces) {
        this.provinces = provinces;
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        this.adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, provinces);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
