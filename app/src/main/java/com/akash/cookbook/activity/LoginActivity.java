package com.akash.cookbook.activity;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.cookbook.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_name, edt_email, edi_pass;
    private Button login_btn, logout_btn;
    private Toolbar toolbar;
    private LoginButton loginButton;
    private CallbackManager manager;
    private TextView title_tool;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        title_tool.setText("Login");

        manager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);
        loginButton.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookSignIn(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edt_email.getText().toString().trim())){
                    edt_email.setError("required");
                }if (TextUtils.isEmpty(edi_pass.getText().toString().trim())){
                    edi_pass.setError("required");
                }else {
                    mAuth.signInWithEmailAndPassword(edt_email.getText().toString().trim(),
                            edi_pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                currentUser = mAuth.getCurrentUser();
                                updateUi(currentUser);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Logout")
                        .setMessage("Sure You want to Log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                currentUser = mAuth.getCurrentUser();
                                updateUi(currentUser);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

    }

    private void handleFacebookSignIn(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    currentUser = mAuth.getCurrentUser();
                    updateUi(currentUser);
                }
            }
        });
    }

    private void updateUi(FirebaseUser currentUser) {
        if (currentUser != null){
            login_btn.setEnabled(false);
            loginButton.setEnabled(false);
            logout_btn.setEnabled(true);
            Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();
        }else {
            login_btn.setEnabled(true);
            loginButton.setEnabled(true);
            logout_btn.setEnabled(false);
            Toast.makeText(this, "User Unauthorized", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edi_pass = findViewById(R.id.edt_pass);
        toolbar = findViewById(R.id.my_tool);
        title_tool = findViewById(R.id.title_tool);
        loginButton = findViewById(R.id.login_button);
        login_btn = findViewById(R.id.login_btn);
        logout_btn = findViewById(R.id.logout_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);
    }
}