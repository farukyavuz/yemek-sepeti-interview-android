package com.yemeksepeti.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yemeksepeti.interview.R;
import com.yemeksepeti.interview.YSApplication;
import com.yemeksepeti.interview.adapter.UserListAdapter;
import com.yemeksepeti.interview.model.common.User;
import com.yemeksepeti.interview.model.adapter.UserListItem;
import com.yemeksepeti.interview.model.response.UserListResponse;
import com.yemeksepeti.interview.rest.util.RestCall;
import com.yemeksepeti.interview.rest.util.RestCallbackImpl;
import com.yemeksepeti.interview.util.YSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class UserListActivity extends BaseActivity implements UserListAdapter.OnItemClickListener {

    private RecyclerView usersRecyclerView;
    private UserListAdapter mUserListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserListItem> userListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.user_list_actionbar_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        usersRecyclerView = (RecyclerView) findViewById(R.id.user_list_recyclerview);
        usersRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(mLayoutManager);
        userListItems = new ArrayList<>();
        mUserListAdapter = new UserListAdapter(this, userListItems);
        usersRecyclerView.setAdapter(mUserListAdapter);
        mUserListAdapter.setOnItemClickListener(this);
        getUserList();
    }

    private void getUserList() {
        RestCall<UserListResponse> call = YSApplication.getRestService()
                .users(YSharedPreferences.getToken(this), YSharedPreferences.getUsername(this));

        call.enqueue(new RestCallbackImpl<UserListResponse>(call, UserListActivity.this, true) {
            @Override
            public void onResponse(final Response response) {
                super.onResponse(response);
                new Handler(Looper.getMainLooper()).post(() -> Log.e("response : ", response.toString()));
            }
            @Override
            public void success(Response<UserListResponse> response) {

                final UserListResponse userListResponse = response.body();
                new Handler(Looper.getMainLooper()).post(() -> {
                    userListItems.clear();
                    for (User user : userListResponse.getPayload().getUserList()) {
                        if (!user.getEmail().equals(getResources().getString(R.string.autofill_email)))
                            userListItems.add(new UserListItem(user));
                    }
                    mUserListAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    public void onItemClick(View view, User user) {
        String id = user.get_id();
        startActivity(new Intent(getApplicationContext(), UserDetailActivity.class).putExtra("id", id));
    }
}
