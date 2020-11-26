package com.akash.cookbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.cookbook.R;
import com.akash.cookbook.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    private EditText edt_name, edt_email, edi_pass;
    private Button login_btn;
    private Toolbar toolbar;
    private TextView title_tool;
    private String name, email, pass;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;
    private UserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        init();
        title_tool.setText("Register User");
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        model = new UserModel();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromUser();
                if (TextUtils.isEmpty(edt_name.getText().toString())){
                    edt_name.setError("required");
                    return;
                }
                if (TextUtils.isEmpty(edt_email.getText().toString())){
                    edt_email.setError("required");
                    return;
                }
                if (TextUtils.isEmpty(edi_pass.getText().toString())){
                    edi_pass.setError("required");
                    return;
                }else {
                    mAuth.createUserWithEmailAndPassword(edt_email.getText().toString().trim(), edi_pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                model.setUserId(uid);
                                dbRef.child(uid).setValue(model);
                                Toast.makeText(RegisterUser.this, "User Registered Successfully!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterUser.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    private void getDataFromUser() {
        model.setName(edt_name.getText().toString());
        model.setEmail(edt_email.getText().toString());
    }

    private void init() {
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edi_pass = findViewById(R.id.edt_pass);
        login_btn = findViewById(R.id.login_btn);
        toolbar = findViewById(R.id.my_tool);
        title_tool = findViewById(R.id.title_tool);
    }
}