package com.rohankharel.twitterapp.loginbll;

import com.rohankharel.twitterapp.api.TwitterApi;
import com.rohankharel.twitterapp.serverresponse.SignUpResponse;
import com.rohankharel.twitterapp.url.URL;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class LoginBLL {

    private boolean isSuccess = false;
    public boolean checkUser(String username,String password){
        TwitterApi twitterApi = URL.getInstance().create(TwitterApi.class);

        Call<SignUpResponse> signUpResponseCall = twitterApi.checkUser(username, password);

        try {
            Response<SignUpResponse> loginResponse = signUpResponseCall.execute();
            if (loginResponse.isSuccessful()) {
                loginResponse.body().getStatus().equals("Login Success");
                URL.token = loginResponse.body().getToken();

                isSuccess = true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }
}
