package com.example.andoidkerdoiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private  static final String PREF_KEY=RegisterActivity.class.getPackage().toString();
    private static final String LOG_TAG=RegisterActivity.class.getName();
    private  static final int SECRET_KEY=34;


    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    EditText phoneEditText;
    Spinner spinner;
    EditText addresEditText;
    RadioGroup genderGroup;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key= getIntent().getIntExtra("SECRET_KEY",0);
        if (secret_key!=34){
            finish();
        }
        userNameEditText=findViewById(R.id.userNameEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        passwordAgainEditText=findViewById(R.id.passwordAgainEditText);
        userEmailEditText=findViewById(R.id.userEmailEditText);
        spinner=findViewById(R.id.phoneSpinner);
        phoneEditText=findViewById(R.id.phoneEditText);
        addresEditText=findViewById(R.id.addresEditText);
        genderGroup=findViewById(R.id.gender);
        genderGroup.check(R.id.maleRadioButton);

        preferences= getSharedPreferences(PREF_KEY,MODE_PRIVATE);

        String password =preferences.getString("password","");
        String userName =preferences.getString("username","");

        userEmailEditText.setText(userName);
        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.phone_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mAuth=FirebaseAuth.getInstance();

    }

    public void register(View view) {

        String userNamestr=userNameEditText.getText().toString();
        String Passwordstr=passwordEditText.getText().toString();
        String PasswordstrAgainstr=passwordAgainEditText.getText().toString();
        String userEmailstr=userEmailEditText.getText().toString();
        if (!Passwordstr.equals(PasswordstrAgainstr)){
            Log.e(LOG_TAG,"Jelszavak nem egyeznek");
            return;
        }

        String phoneNumber= phoneEditText.getText().toString();
        String phoneType=spinner.getSelectedItem().toString();
        String addres=addresEditText.getText().toString();

        int chosenId=genderGroup.getCheckedRadioButtonId();
       RadioButton radoiButton=genderGroup.findViewById(chosenId);
       String gender=radoiButton.getText().toString();

        mAuth.createUserWithEmailAndPassword(userEmailstr,Passwordstr).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"Sikeres regiszráció");
                    startQuestions();
                }else{
                    Log.i(LOG_TAG,"Sikertelen regiszráció");
                }
            }
        });

    }

    private void startQuestions(){
        Intent intent=new Intent(this,questionnaireActiviti.class);

        startActivity(intent);
    }

    public void cencel(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selecteditem=adapterView.getItemAtPosition(i).toString();
        Log.i(LOG_TAG,selecteditem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}