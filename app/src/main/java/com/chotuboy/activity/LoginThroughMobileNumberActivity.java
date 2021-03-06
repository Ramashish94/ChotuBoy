package com.chotuboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chotuboy.R;
import com.chotuboy.modelClass.login.LoginWithOtpResponse;
import com.chotuboy.retrofit.RestClient;
import com.chotuboy.utils.ChotuBoyPrefs;
import com.chotuboy.utils.Constants;
import com.chotuboy.utils.Utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginThroughMobileNumberActivity extends AppCompatActivity {
    public EditText edtTxtMobNo;
    public Button btnSendOtp;
    private String mobileNo;
    public RadioGroup radioGroup_User;
    public RadioButton rdBtnDelivery_Boy, rdBtnOutLet;
    private String userType;
    public String selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_through_mobile_number);

        edtTxtMobNo = findViewById(R.id.edtTxtMobNo);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        rdBtnDelivery_Boy = findViewById(R.id.RdBtnDeliveryBoy);
        rdBtnOutLet = findViewById(R.id.RdBtnOutLet);
        radioGroup_User = findViewById(R.id.userTypeRdGp);


        userType();

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validationLoginUser();
            }
        });

    }

    public void userType() {
        radioGroup_User.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton genderrg = (RadioButton) group.findViewById(checkedId);
                if (null != genderrg) {
                    userType = genderrg.getText().toString();
                    //selectedId = String.valueOf(radioGroup_User.getCheckedRadioButtonId());
                    Toast.makeText(getApplicationContext(), genderrg.getText().toString(), Toast.LENGTH_SHORT).show();
                    System.out.println("User Type id " + selectedId);
                }
            }
        });

    }

    public boolean inputValidaton() {
        boolean check = true;
        mobileNo = edtTxtMobNo.getText().toString().trim();

        if (mobileNo.isEmpty() || edtTxtMobNo.length() < 10) {
            edtTxtMobNo.setError(" Enter a valid phone number ");
            check = false;
        } else {
            edtTxtMobNo.setError(null);
            check = true;
        }

        return check;
    }

    private void validationLoginUser() {
        if (inputValidaton()) {

            RequestBody phoneno = RequestBody.create(MediaType.parse("text/plain"), mobileNo);
            Utils.showProgressDialog(this);

            RestClient.logInWithOtpNewUser(phoneno, new Callback<LoginWithOtpResponse>() {
                @Override
                public void onResponse(Call<LoginWithOtpResponse> call, Response<LoginWithOtpResponse> response) {
                    Utils.dismissProgressDialog();
                    if (response != null && response.code() == 200 && response.body() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("true")) {
                            Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                            ChotuBoyPrefs.putString(getApplicationContext(), Constants.MOBILE, mobileNo);
                            ChotuBoyPrefs.putString(getApplicationContext(), Constants.USERTYPE, userType);
                           // ChotuBoyPrefs.putString(getApplicationContext(),Constants.UserTypeSelectedID,selectedId);

                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginThroughMobileNumberActivity.this, "Pls verify Otp!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<LoginWithOtpResponse> call, Throwable t) {
                    Toast.makeText(LoginThroughMobileNumberActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    // onBacked pressed registration
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
