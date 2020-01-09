package com.rohankharel.twitterapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rohankharel.twitterapp.R;
import com.rohankharel.twitterapp.api.TwitterApi;
import com.rohankharel.twitterapp.model.Users;
import com.rohankharel.twitterapp.serverresponse.ImageResponse;
import com.rohankharel.twitterapp.serverresponse.SignUpResponse;
import com.rohankharel.twitterapp.strictmode.StrictModeClass;
import com.rohankharel.twitterapp.url.URL;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText etfirstname, etlastname, etusername, etpassword;
    private Button btnSignUp;
    private ImageView imgView;
    String imagePath;
    private String imageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etfirstname = findViewById(R.id.etfirstname);
        etlastname = findViewById(R.id.etlastname);
        etusername = findViewById(R.id.etusername);
        etpassword = findViewById(R.id.etpassword);
        imgView = findViewById(R.id.imgView);

        btnSignUp = findViewById(R.id.btnSignUp);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    public void Register(){
        String firstname = etfirstname.getText().toString();
        String lastname = etlastname.getText().toString();
        String username = etusername.getText().toString();
        String password = etpassword.getText().toString();

        Users users = new Users(firstname,lastname,username,password, imageName);

        TwitterApi twitterApi = URL.getInstance().create(TwitterApi.class);
        Call<SignUpResponse> signUpResponseCall = twitterApi.registerUser(users);

        signUpResponseCall.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                Toast.makeText(SignUpActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void BrowseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }

        Uri uri = data.getData();
        imgView.setImageURI(uri);
//        imagePath = getRealPathFromUri(uri);
//        preViewImage(imagePath);


    }

    private String getRealPathFromUri(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader  = new CursorLoader(getApplicationContext(),uri,projection,null,
                null, null);

        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        return result;
    }

    private void saveImageOnly(){
        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile",
                file.getName(), requestBody);

        TwitterApi twitterApi = URL.getInstance().create(TwitterApi.class);
        Call<ImageResponse> responseBodyCall = twitterApi.uploadImage(body);

        StrictModeClass.StrictMode();

        try {
            Response<ImageResponse> imageResponseResponse = responseBodyCall.execute();
            imageName = imageResponseResponse.body().getFilename();
        }
        catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
