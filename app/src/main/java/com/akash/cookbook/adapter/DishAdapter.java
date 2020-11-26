package com.akash.cookbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.akash.cookbook.R;
import com.akash.cookbook.fragment.DishFragmentArgs;
import com.akash.cookbook.fragment.DishFragmentDirections;
import com.akash.cookbook.model.RecipesModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DishAdapter extends BaseAdapter {

    private Context context;
    private int res;
    private ArrayList<RecipesModel> models;

    public DishAdapter(Context context, int res, ArrayList<RecipesModel> models) {
        this.context = context;
        this.res = res;
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
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

        View view = LayoutInflater.from(context).inflate(res, viewGroup,false);

        RecipesModel model = models.get(i);

        CardView dishTile = view.findViewById(R.id.dish_tile);
        ImageView dishImage = view.findViewById(R.id.dish_image);
        TextView dishName = view.findViewById(R.id.dish_name);

        dishName.setText(model.getDishName());
        Picasso.get().load(model.getImageUrl()).fit().into(dishImage);

        dishTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                DishFragmentDirections.ActionDishFragmentToRecipeView action =
                        DishFragmentDirections.actionDishFragmentToRecipeView();
                action.setDishName(dishName.getText().toString());
                action.setCuisineName(model.getCuisineName());
                controller.navigate(action);
            }
        });
        return view;
    }
}
