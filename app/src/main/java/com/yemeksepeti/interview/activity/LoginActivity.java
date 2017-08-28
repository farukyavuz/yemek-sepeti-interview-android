package com.yemeksepeti.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.yemeksepeti.interview.R;
import com.yemeksepeti.interview.YSApplication;
import com.yemeksepeti.interview.model.request.LoginRequest;
import com.yemeksepeti.interview.model.response.LoginResponse;
import com.yemeksepeti.interview.rest.util.RestCall;
import com.yemeksepeti.interview.rest.util.RestCallbackImpl;
import com.yemeksepeti.interview.util.Fonty;
import com.yemeksepeti.interview.util.YSHelpers;
import com.yemeksepeti.interview.util.YSharedPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_root_view)
    ScrollView rootView;
    @BindView(R.id.activity_login_email)
    EditText email;
    @BindView(R.id.activity_login_password)
    EditText password;
    @BindView(R.id.activity_login_login_button)
    Button loginButton;
    @BindView(R.id.activity_login_autofill_button)
    Button autoFillButton;

    private LoginRequest loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Fonty.setFontAllView(rootView);
        loginButton.setOnClickListener(view -> {
            if (validate()) {
                loginRequest = new LoginRequest(email.getText().toString(), password.getText().toString());
                RestCall<LoginResponse> call = YSApplication.getRestService().login(loginRequest);
                call.enqueue(new RestCallbackImpl<LoginResponse>(call, LoginActivity.this, true) {
                    @Override
                    public void onResponse(final Response response) {
                        super.onResponse(response);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            //Log.e("response",response.toString());
                        });
                    }
                    @Override
                    public void success(Response<LoginResponse> response) {
                        final LoginResponse loginResponse = response.body();
                        new Handler(Looper.getMainLooper()).post(() -> {

                            if(loginResponse.isSuccess()){
                                YSharedPreferences.setToken(getApplicationContext(), loginResponse.getPayload().getUser().getAccessToken());
                                YSharedPreferences.setUsername(getApplicationContext(), loginRequest.getEmail());
                                YSharedPreferences.setPassword(getApplicationContext(), loginRequest.getPassword());
                                startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, R.string.wrong_email_or_password, Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                });
            }
        });
        autoFillButton.setOnClickListener(view -> {
            email.setText(getString(R.string.autofill_email));
            password.setText(getString(R.string.autofill_password));
            email.setSelection(email.getText().length());
            password.setSelection(password.getText().length());
        });
    }

    public boolean validate() {
        if(!YSHelpers.isConnected(getApplicationContext())){
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();

            return false;
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
                Toast.makeText(this, getString(R.string.login_password_invalid), Toast.LENGTH_SHORT).show();
                return false;
        }
        return true;
    }
}
