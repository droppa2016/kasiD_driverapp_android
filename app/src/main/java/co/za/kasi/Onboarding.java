package co.za.kasi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import co.za.kasi.model.systemDTO.ViewPagerDTO;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.viewpagers.ViewPagerAdapter;
import co.za.kasi.R;

public class Onboarding extends Activity {

    AppCompatButton skip, next;
    LinearLayout active1, active2, active3;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private ArrayList<ViewPagerDTO> pagerList;
    private int currentPosition = -1;

    Intent intentFallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        init();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (currentPosition == pagerList.size() - 1) {
                    updateFistTimeVisit();
                    AccountAccessActivity.launchAccountAccessActivity(Onboarding.this);
                    finish();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFistTimeVisit();
                AccountAccessActivity.launchAccountAccessActivity(Onboarding.this);
                finish();
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                if (position == pagerList.size() - 1) {
                    next.setText("Get started");
                    skip.setVisibility(View.GONE);
                } else {
                    next.setText("Next");
                    skip.setVisibility(View.VISIBLE);
                }
                initiateActivators(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initiateActivators(int position) {
        switch (position) {
            case 0:
                active1.setBackground(getDrawable(R.drawable.active_dot));
                active2.setBackground(getDrawable(R.drawable.inactive_dot));
                active3.setBackground(getDrawable(R.drawable.inactive_dot));
                break;
            case 1:
                active2.setBackground(getDrawable(R.drawable.active_dot));
                active1.setBackground(getDrawable(R.drawable.inactive_dot));
                active3.setBackground(getDrawable(R.drawable.inactive_dot));
                break;
            case 2:
                active3.setBackground(getDrawable(R.drawable.active_dot));
                active1.setBackground(getDrawable(R.drawable.inactive_dot));
                active2.setBackground(getDrawable(R.drawable.inactive_dot));
                break;
        }
    }


    private void updateFistTimeVisit() {
        LocalStorage.storeFirstVisit(true);
    }

    private void setUpPager() {
        pagerList = new ArrayList<>();
        pagerList.add(new ViewPagerDTO(getString(R.string.welcome_to_the_droppa_driver_app), getString(R.string.thank_you_for_choosing_to_partner_with_us_let_s_get_started), getDrawable(R.drawable.image_0)));
        pagerList.add(new ViewPagerDTO(getString(R.string.decide_where_when_and_how_you_want_to_earn), getString(R.string.with_the_driver_app_you_can_see_all_future_and_past_bookings_manage_your_income_and_select_the_preferred_jobs_you_would_like), getDrawable(R.drawable.image_1)));
        pagerList.add(new ViewPagerDTO(getString(R.string.before_you_register_make_sure_that_you_meet_the_below_requirements), getString(R.string._1_you_have_a_valid_police_clearance) + "\n" + getString(R.string._2_you_posses_a_vehicle_that_was_manufactured_after_2012)
                + "\n" + getString(R.string._3_your_vehicle_has_a_mileage_of_160_000km_or_less)
                + "\n" + getString(R.string._4_you_have_a_valid_driver_s_license_and_insurance_for_your_vehicle), getDrawable(R.drawable.image_2)));

        viewPagerAdapter = new ViewPagerAdapter(pagerList, getApplicationContext());

        viewPager.setAdapter(viewPagerAdapter);

    }

    private void init() {
        intentFallBack = new Intent(getApplicationContext(), FallBackPage.class);

        intentFallBack.putExtra("class", "SignIn");
        skip = findViewById(R.id.btnSkipIllustrations);
        next = findViewById(R.id.btnNextIllustration);

        active1 = findViewById(R.id.lytActive1);
        active2 = findViewById(R.id.lytActive2);
        active3 = findViewById(R.id.lytActive3);

        viewPager = findViewById(R.id.viewPager);
        setUpPager();

    }
}