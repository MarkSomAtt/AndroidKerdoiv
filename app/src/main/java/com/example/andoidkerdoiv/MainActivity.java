package com.example.andoidkerdoiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private  static final int SECRET_KEY=34;
    private  static final String PREF_KEY=MainActivity.class.getPackage().toString();

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText usernameET;
    EditText passwordET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameET=findViewById(R.id.EditTextUsername);
        passwordET=findViewById(R.id.EditTextPassword);
        preferences= getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        mAuth=FirebaseAuth.getInstance();
    }

    public void login(View view) {


        String userNamestr=usernameET.getText().toString();
        String Passwordstr=passwordET.getText().toString();
        mAuth.signInWithEmailAndPassword(userNamestr,Passwordstr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i(LOG_TAG,"Sikeres bejelentkezés");
                    startQuestions();

                }else {
                    Log.i(LOG_TAG,"Sikertelen bejelentkezés");
                }
            }
        });
    }

    private void startQuestions(){
        Intent intent=new Intent(this,questionnaireActiviti.class);

        startActivity(intent);
    }

    public void register(View view) {
        Intent intent=new Intent(this,RegisterActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("username",usernameET.getText().toString());
        editor.putString("password",passwordET.getText().toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        finish();
        super.onDestroy();
    }



}