package sampleapp.com;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*import java.util.ArrayList;
import java.util.List;
import java.util.Map;*/
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText lemail;
    private EditText lpassword;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button lbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lemail = (EditText) findViewById(R.id.login_email_input);
        lpassword = (EditText) findViewById(R.id.login_password_input);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        lbutton = (Button) findViewById(R.id.login);
        //type=(EditText)findViewById(R.id.type);
        lbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == lbutton) {
                    LoginUser(lemail.getText().toString(), lpassword.getText().toString());
                }
            }
        });
    }
    public void LoginUser(String userLoginEmail,String userLoginPassword){
       userLoginEmail = lemail.getText().toString().trim();
       userLoginPassword = lpassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(userLoginEmail, userLoginPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = currentUser.getUid();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            final DatabaseReference uidRef = rootRef.child("Users").child(uid);
                            ValueEventListener valueEventListener =new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    if (Objects.equals(dataSnapshot.child("type").getValue(String.class), "Valet")) {
                                        startActivity(new Intent(LoginActivity.this, ValetDashboardActivity.class));
                                    } else if (Objects.equals(dataSnapshot.child("type").getValue(String.class), "Customer")) {
                                        startActivity(new Intent(LoginActivity.this, CustDashboardActivity.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                                    }

                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //Log.d(TAG, databaseError.getMessage());
                                }

                            };
                            uidRef.addListenerForSingleValueEvent(valueEventListener);
                        }
                    }
                });
        }
    }


