package com.android.gids;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.loadinganimation.LoadingAnimation;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button btnSignIn;

    LinearLayout liError;

    EditText etEmail;
    TextInputEditText etPassword;

    TextView tvError, tvOffline;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    LoadingAnimation lodingAnim;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        btnSignIn = findViewById(R.id.btnSignIn);
        liError = findViewById(R.id.liError);
        tvError = findViewById(R.id.tvError);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        lodingAnim = findViewById(R.id.loadingAnim);
        tvOffline = findViewById(R.id.tvOffline);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("lat", LocationService.getLat());
                Log.v("lat", LocationService.getLong());
                isValidate();
            }
        });

        tvOffline.setText("Offline Application [Version:"+ Utils.getVersionName(this)+"]");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationService.requestLocation(this);
        }




    }

    public void isValidate() {
        if (etEmail.getText().toString().equalsIgnoreCase("")) {
            liError.setVisibility(View.VISIBLE);
            tvError.setText("Please Enter Email Address");
            return;
        }
        if (etPassword.getText().toString().equalsIgnoreCase("")) {
            liError.setVisibility(View.VISIBLE);
            tvError.setText("Please Enter Password");
            return;
        }

        if (Utils.isNetworkAvailable(this)) {
            liError.setVisibility(View.GONE);
            lodingAnim.setVisibility(View.VISIBLE);

            doLogin();
        } else {
            liError.setVisibility(View.VISIBLE);
            tvError.setText("Internet Connectivity is lost!!");
        }

    }

    public void signIn() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    public void doLogin() {
        Log.v("Lat&Long", LocationService.getLat()+"  -  "+LocationService.getLong());
        LoginParam loginParam = new LoginParam();
        loginParam.setUsername(etEmail.getText().toString());
        loginParam.setPassword(etPassword.getText().toString());
        loginParam.setApp_version(Utils.getVersionName(this));
        loginParam.setLatitude(LocationService.getLat());
        loginParam.setLongtitude(LocationService.getLong());
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        Call<LoginModal> call = methods.doLogin(loginParam);
        call.enqueue(new Callback<LoginModal>() {
            @Override
            public void onResponse(Call<LoginModal> call, Response<LoginModal> response) {
                try {
                    setPref(response.body());
                }catch (Exception e){
                    liError.setVisibility(View.VISIBLE);
                    tvError.setText(e.getMessage()+" cause: "+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginModal> call, Throwable t) {
                lodingAnim.setVisibility(View.GONE);
                liError.setVisibility(View.VISIBLE);
                tvError.setText(t.getMessage()+" cause:"+t.getCause()+" "+t.getLocalizedMessage());
            }
        });
    }


    public void setPref(LoginModal loginModal) {

        if (loginModal.getGIDS_SURVEY_APP().getRes_code().equalsIgnoreCase("1")) {
            liError.setVisibility(View.GONE);
            lodingAnim.setVisibility(View.GONE);
            myEdit = sharedPreferences.edit();
            Log.v("safsafsa", loginModal.getGIDS_SURVEY_APP().getId() + "");
            myEdit.putString("id", loginModal.getGIDS_SURVEY_APP().getId());
            myEdit.putString("name", loginModal.getGIDS_SURVEY_APP().getName());
            myEdit.putString("email", loginModal.getGIDS_SURVEY_APP().getEmail());
            myEdit.putString("mobile", loginModal.getGIDS_SURVEY_APP().getMobile());
            myEdit.putString("image", loginModal.getGIDS_SURVEY_APP().getImage());
            myEdit.putString("company_id", loginModal.getGIDS_SURVEY_APP().getCompany_id());
            myEdit.putString("supervisor_id", loginModal.getGIDS_SURVEY_APP().getSupervisor_id());
            myEdit.putString("created_at", loginModal.getGIDS_SURVEY_APP().getCreated_at());
            myEdit.commit();
            signIn();
        } else {
            liError.setVisibility(View.VISIBLE);
            tvError.setText(loginModal.getGIDS_SURVEY_APP().getRes_msg());
            lodingAnim.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationService.requestLocation(this);
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}