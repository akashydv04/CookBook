package com.akash.cookbook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.cookbook.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecipeView extends Fragment {

    private TextView title_tool, dishNames, prepareTime, cookTime, totalTime, ingredients;
    private ImageView dishImage;
    private DatabaseReference dbRef;
    private String dishName, cuisineName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title_tool = getActivity().findViewById(R.id.title_tool);
        dishImage = view.findViewById(R.id.dish_image);
        dishNames = view.findViewById(R.id.dish_name);
        prepareTime = view.findViewById(R.id.prepare_time_txt);
        cookTime = view.findViewById(R.id.cooking_time_txt);
        totalTime = view.findViewById(R.id.total_time_txt);
        ingredients = view.findViewById(R.id.ingredients_view_txt);

        dishName = RecipeViewArgs.fromBundle(getArguments()).getDishName();
        cuisineName = RecipeViewArgs.fromBundle(getArguments()).getCuisineName();
        title_tool.setText(dishName);

        dbRef = FirebaseDatabase.getInstance().getReference();
        loadData();
    }

    private void loadData() {
        dbRef.child(cuisineName).child(cuisineName+dishName).child(cuisineName+dishName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(dishImage);
                        dishNames.setText(snapshot.child("dishName").getValue().toString());
                        prepareTime.setText(snapshot.child("preparationTime").getValue().toString()+" min");
                        cookTime.setText(snapshot.child("cookingTime").getValue().toString()+" min");
                        totalTime.setText(snapshot.child("totalTime").getValue().toString()+" min");

                        String ingredient = snapshot.child("ingredientName").getValue().toString();
                        String news= ingredient.replaceAll(",","\n");
                        ingredients.setText(news);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}