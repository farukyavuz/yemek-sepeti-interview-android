package com.yemeksepeti.interview.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yemeksepeti.interview.R;
import com.yemeksepeti.interview.YSApplication;
import com.yemeksepeti.interview.model.common.User;
import com.yemeksepeti.interview.model.request.UserUpdateRequest;
import com.yemeksepeti.interview.model.response.DefaultResponse;
import com.yemeksepeti.interview.model.response.UserResponse;
import com.yemeksepeti.interview.rest.util.RestCall;
import com.yemeksepeti.interview.rest.util.RestCallbackImpl;
import com.yemeksepeti.interview.util.Fonty;
import com.yemeksepeti.interview.util.YSHelpers;
import com.yemeksepeti.interview.util.YSharedPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

public class UserDetailActivity extends BaseActivity {

    @BindView(R.id.user_detail_root_view)
    ScrollView rootView;
    @BindView(R.id.user_detail_profile_image)
    CircleImageView profileImage;
    @BindView(R.id.user_detail_tv_first_name)
    TextView firstName;
    @BindView(R.id.user_detail_tv_last_name)
    TextView lastName;
    @BindView(R.id.user_detail_tv_birthday)
    TextView birthDay;
    @BindView(R.id.user_detail_et_phone)
    EditText phone;
    @BindView(R.id.user_detail_et_email)
    EditText email;
    @BindView(R.id.user_detail_et_address)
    EditText address;
    @BindView(R.id.user_detail_btn_save)
    Button btnSave;

    private String id;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        Fonty.setFontAllView(rootView);

        if(getSupportActionBar() != null){
            Log.e("getSupportActionBar","null değil.");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(getIntent() != null){
            id = getIntent().getExtras().getString("id","empty");
        }


        RestCall<UserResponse> call = YSApplication.getRestService()
                .user(YSharedPreferences.getToken(this), YSharedPreferences.getUsername(this), id);

        call.enqueue(new RestCallbackImpl<UserResponse>(call, UserDetailActivity.this, true) {
            @Override
            public void onResponse(final Response response) {
                super.onResponse(response);
                new Handler(Looper.getMainLooper()).post(() -> {});
            }
            @Override
            public void success(Response<UserResponse> response) {
                final UserResponse userResponse = response.body();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if(userResponse.isSuccess()){
                        user = userResponse.getPayload().getUser();
                        if(user.getUserImage() != null){
                            Picasso.with(getApplicationContext()).load(user.getUserImage().getUrl()).into(profileImage);
                        }

                        firstName.setText(user.getName().getFirst());
                        lastName.setText(user.getName().getLast());

                        if(user.getBirthday() !=null){
                            birthDay.setText(YSHelpers.ConvertDateToString("dd/MM/yyyy",user.getBirthday()));
                        }
                        phone.setText(user.getPhone());
                        email.setText(user.getEmail());
                        address.setText(user.getAddress());
                        phone.setSelection(phone.getText().length());
                        email.setSelection(email.getText().length());
                        address.setSelection(address.getText().length());

                    }
                });
            }
        });


        btnSave.setOnClickListener(view -> {
            if(validate()){
                UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
                userUpdateRequest.setUserId(id);
                userUpdateRequest.setPhone(phone.getText().toString());
                userUpdateRequest.setEmail(email.getText().toString());
                userUpdateRequest.setAddress(address.getText().toString());

                RestCall<DefaultResponse> restCall = YSApplication.getRestService()
                        .userUpdate(YSharedPreferences.getToken(this), YSharedPreferences.getUsername(this), userUpdateRequest);
                restCall.enqueue(new RestCallbackImpl<DefaultResponse>(restCall, UserDetailActivity.this, true) {
                    @Override
                    public void onResponse(final Response response) {
                        super.onResponse(response);
                        new Handler(Looper.getMainLooper()).post(() -> {});
                    }
                    @Override
                    public void success(Response<DefaultResponse> response) {
                        final DefaultResponse defaultResponse = response.body();
                        new Handler(Looper.getMainLooper()).post(() -> {

                            if(defaultResponse.isSuccess()){
                                showSuccessDialog();
                            }else{
                                Toast.makeText(UserDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean validate() {
        if (!YSHelpers.isConnected(getApplicationContext())) {
            Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.getText().toString().isEmpty() && !Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.phone_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showSuccessDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.completed));
        builder.setMessage(getString(R.string.user_updated));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
