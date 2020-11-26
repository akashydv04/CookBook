package com.akash.cookbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.akash.cookbook.R;
import com.akash.cookbook.fragment.ListCuisineDirections;
import com.akash.cookbook.model.CuisineModel;
import com.akash.cookbook.model.RecipesModel;

import java.util.ArrayList;

public class  ListAdapter   extends BaseAdapter {

    Context context;
    int res;
    ArrayList<RecipesModel> list;

    public ListAdapter(Context context, int res, ArrayList<RecipesModel> list) {
        this.context = context;
        this.res = res;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(res,viewGroup,false);

        RecipesModel cuisineModel = list.get(i);

        TextView cuisineName = view.findViewById(R.id.cousine_name);
        ConstraintLayout list_item = view.findViewById(R.id.list_items);

        cuisineName.setText(cuisineModel.getCuisineName());

        list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                ListCuisineDirections.ActionListCuisineToDishFragment action = ListCuisineDirections.actionListCuisineToDishFragment();
                action.setCuisineName(cuisineName.getText().toString());
                navController.navigate(action);
            }
        });

        return view;
    }
}
