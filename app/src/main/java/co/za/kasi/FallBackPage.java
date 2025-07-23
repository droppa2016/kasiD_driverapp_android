package co.za.kasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textview.MaterialTextView;

import co.za.kasi.R;


public class FallBackPage extends AppCompatActivity {

    private AppCompatButton retry;
    private MaterialTextView message,title;
    private LottieAnimationView lottieAnimationViewp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_back_page);
        try {
            init();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void init() throws ClassNotFoundException {
        retry = findViewById(R.id.btnRefresh);
        message = findViewById(R.id.txtErrorMessage);
        title = findViewById(R.id.txtErrorTitle);
        lottieAnimationViewp = findViewById(R.id.lottie_file);

        if(getIntent().getStringExtra("server") != null){
            lottieAnimationViewp.setAnimation("server_error.json");
            title.setText("Technical error!!");
        } else{
            title.setText("Connection Lost!!");
        }

        message.setText(getIntent().getStringExtra("error"));
        retry.setOnClickListener(v -> {
            navigateToActivity(getIntent().getStringExtra("class"));
        });
    }

    private void navigateToActivity(String className) {
        try {
            // Create a new Intent with the specified class name
            Intent intent = new Intent();
            intent.setClassName(getString(R.string.app_package),  getString(R.string.app_package) + "." + className);

            // Start the activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where the activity is not found

            Log.e("TAG_class_name", "init: " + e.getMessage(), e);
            Toast.makeText(this, "Activity not found", Toast.LENGTH_SHORT).show();
        }
    }
}