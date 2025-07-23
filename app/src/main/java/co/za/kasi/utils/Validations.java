package co.za.kasi.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;



public interface Validations {


    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public static boolean isValidMobile(String target) {
        Pattern pattern = Pattern.compile("^(27|0)\\s[0-9]{2}\\s[0-9]{3}\\s[0-9]{4}");
        return (!TextUtils.isEmpty(target) && pattern.matcher(target).matches());
    }
    public static boolean isValidPass(String target) {
        Pattern pattern = Pattern.compile("^[0-9][a-z][A-Z]{6}");
        return (!TextUtils.isEmpty(target) && pattern.matcher(target).matches());
    }

    public static TextWatcher validateNumber(TextInputEditText phoneNumber, Context context){

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("ON_TEXT_CHANGE",s.length() + " :char" + "    " + phoneNumber.getText().length() + " :TExt");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("ON_TEXT_CHANGE",s.length() + " :char" + "    " + phoneNumber.getText().length() + " :TExt");
                if (s.toString() == "0") {
                    phoneNumber.setText("27");
                }

                if (s.length() == 6 || s.length() == 2) {
                    phoneNumber.setText(phoneNumber.getText().toString() + " ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static TextWatcher validatePassword(TextInputEditText password, Context context){

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("ON_TEXT_CHANGE",s.length() + " :char" + "    " + password.getText().length() + " :TExt");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

}
