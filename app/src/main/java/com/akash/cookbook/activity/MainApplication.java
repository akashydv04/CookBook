package com.akash.cookbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.akash.cookbook.R;
import com.akash.cookbook.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainApplication extends AppCompatActivity {

    private FloatingActionButton fab_button, sign_up_button, login_button, add_recipe;
    private boolean isRotate = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_application);

        init();
        ViewAnimation.init(sign_up_button);
        ViewAnimation.init(login_button);
        ViewAnimation.init(add_recipe);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainApplication.this, LoginActivity.class));
            }
        });
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainApplication.this, RegisterUser.class));
            }
        });
        add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainApplication.this, AddRecipe.class));
            }
        });


        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sign_up_button.getVisibility() == View.GONE && login_button.getVisibility() == View.GONE && add_recipe.getVisibility() == View.GONE) {
                    isRotate=true;
                    sign_up_button.setVisibility(View.VISIBLE);
                    login_button.setVisibility(View.VISIBLE);
                    add_recipe.setVisibility(View.VISIBLE);
                    isRotate = ViewAnimation.rotateFab(view,isRotate);
                    ViewAnimation.showIn(sign_up_button);
                    ViewAnimation.showIn(login_button);
                    ViewAnimation.showIn(add_recipe);
                }
                else {
                    sign_up_button.setVisibility(View.GONE);
                    login_button.setVisibility(View.GONE);
                    add_recipe.setVisibility(View.GONE);
                    isRotate = false;
                    isRotate = ViewAnimation.rotateFab(view, isRotate);
                    ViewAnimation.showOut(sign_up_button);
                    ViewAnimation.showOut(login_button);
                    ViewAnimation.showOut(add_recipe);
                }
            }
        });

    }


    private void init() {

        fab_button = findViewById(R.id.more_options);
        sign_up_button = findViewById(R.id.signup_button);
        login_button = findViewById(R.id.login_button);
        add_recipe = findViewById(R.id.add_recipe);
    }
}