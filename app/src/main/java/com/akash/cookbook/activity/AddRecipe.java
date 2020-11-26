package com.akash.cookbook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.cookbook.R;
import com.akash.cookbook.adapter.ListAdapter;
import com.akash.cookbook.model.CuisineModel;
import com.akash.cookbook.model.RecipesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;

public class AddRecipe extends AppCompatActivity {

    private static final int GALLERY_PICK = 1 ;
    private Toolbar toolbar;
    private TextView title_tool;
    private EditText edt_cuisine_name, pick_image, edt_dish_name, edt_ingredients, edt_prepare_time, edt_cooking_time, edt_total_time;
    private Button add_cuisine_btn, add_dish_recipe_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;
    private DatabaseReference dbRefPublic;
    private StorageReference storeRef;
    private RecipesModel model;
    private Uri mImageUri;
    private ArrayList list;
    private ArrayAdapter adapter;
    private Spinner cuisineSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        dbRef = FirebaseDatabase.getInstance().getReference().child("public_cuisine");
        dbRefPublic = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storeRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        list = new ArrayList();
        loadSpinner();
        updateUI(currentUser);
        init();
        model = new RecipesModel();

        title_tool.setText("Add Recipe");

        add_cuisine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edt_cuisine_name.getText().toString().trim())){
                    edt_cuisine_name.setError("required");
                }else {
                    model.setCuisineName(edt_cuisine_name.getText().toString());
                    dbRef.push().push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AddRecipe.this, "cuisine added successful!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddRecipe.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);
            }
        });

        add_dish_recipe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllData();
                if (mImageUri != null){
                    StorageReference imageRef = storeRef.child("images/pic_"+System.currentTimeMillis()+".jpg");
                    imageRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    model.setImageUrl(url);
                                    String childName = cuisineSpin.getSelectedItem().toString();
                                    model.setCuisineName(childName);
                                    dbRefPublic.child(childName).child(childName+edt_dish_name.getText().toString())
                                            .child(childName+edt_dish_name.getText().toString()).setValue(model);
                                    Toast.makeText(AddRecipe.this, "Added Successfully!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddRecipe.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(AddRecipe.this, "null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSpinner() {
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()){
                    String names = postSnapshot.child("cuisineName").getValue(String.class);
                    list.add(names);

                    adapter = new ArrayAdapter(AddRecipe.this, android.R.layout.simple_dropdown_item_1line, list);
                    cuisineSpin.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddRecipe.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAllData() {

        model.setDishName(edt_dish_name.getText().toString().trim());
        model.setIngredientName(edt_ingredients.getText().toString().trim());
        model.setPreparationTime(edt_prepare_time.getText().toString().trim());
        model.setCookingTime(edt_cooking_time.getText().toString().trim());
        model.setTotalTime(edt_total_time.getText().toString().trim());
    }

    private void init() {
        toolbar = findViewById(R.id.my_tool);
        title_tool = findViewById(R.id.title_tool);
        edt_cuisine_name = findViewById(R.id.cuisine_name_edt);
        add_cuisine_btn = findViewById(R.id.add_cuisine_name);
        pick_image = findViewById(R.id.pick_image);
        edt_dish_name = findViewById(R.id.edt_dish_name);
        edt_ingredients = findViewById(R.id.edt_ingrediants);
        edt_prepare_time = findViewById(R.id.edt_preparation_time);
        edt_cooking_time = findViewById(R.id.edt_cooking_time);
        edt_total_time = findViewById(R.id.edt_total_time);
        cuisineSpin = findViewById(R.id.cuisine_spin);
        add_dish_recipe_btn = findViewById(R.id.add_dish_btn);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            Toast.makeText(this, "welcome you can add recipe", Toast.LENGTH_SHORT).show();
        }else {
            startActivity(new Intent(AddRecipe.this, LoginActivity.class));
            Toast.makeText(this, "User Unauthorized", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(6, 7)
                    .start(AddRecipe.this);
        }
        CropImage.ActivityResult result = null;
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                mImageUri = result.getUri();

                pick_image.setText(mImageUri.toString());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        loadSpinner();
    }
}