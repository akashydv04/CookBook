package com.akash.cookbook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.cookbook.R;
import com.akash.cookbook.adapter.DishAdapter;
import com.akash.cookbook.model.RecipesModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DishFragment extends Fragment {

    private TextView title_tool;
    private DishAdapter adapter;
    private ArrayList list;
    private String cuisineName;
    private GridView gridView;
    private RecipesModel model;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title_tool = getActivity().findViewById(R.id.title_tool);
        gridView = view.findViewById(R.id.grid_view);
        cuisineName = DishFragmentArgs.fromBundle(getArguments()).getCuisineName();
        title_tool.setText(cuisineName);

        dbRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList();
    }

    private void loadData() {
        dbRef.child(cuisineName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    for (DataSnapshot postSnapshot: snapshot.getChildren()){
                        model = postSnapshot.getValue(RecipesModel.class);
                        list.add(model);
                        adapter = new DishAdapter(getActivity(), R.layout.dish_tile_view, list);
                        gridView.setAdapter(adapter);
                    }
                }else {
                    Toast.makeText(getActivity(), "no data found", Toast.LENGTH_SHORT).show();
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

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }
}