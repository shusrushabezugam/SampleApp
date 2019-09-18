package sampleapp.com;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText name, email, password,userType,phone;
    private ProgressBar progressBar;
    private Button button_register,button_login;
    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_email);
        password = findViewById(R.id.edit_text_password);
        phone = findViewById(R.id.edit_text_phone);
        userType=findViewById(R.id.type);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_register).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);
    }


    private void registerUser() {
        final String name_ = name.getText().toString().trim();
        final String email_ = email.getText().toString().trim();
        String password_ = password.getText().toString().trim();
        final String phone_ = phone.getText().toString().trim();
        final String type_ = userType.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email_, password_)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user =new User(name_,email_,type_);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "registration successful",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                break;
            case R.id.button_register:
                registerUser();
                break;
        }

    }


}
