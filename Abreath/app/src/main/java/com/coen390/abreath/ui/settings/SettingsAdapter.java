package com.coen390.abreath.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coen390.abreath.R;

import java.util.ArrayList;

/**
 * List adapter used for display the settings
 * The code is adapted to this project but solely belongs to the owner.
 * This class has been implemented using the following video https://www.youtube.com/watch?v=zS8jYzLKirM&ab_channel=PhucVR
 */
public class SettingsAdapter extends ArrayAdapter<Category> {

    private Context cContext;
    private int random;


    public SettingsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Category> objects) {
        super(context, resource, objects);
        this.cContext = context;
        this.random = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(cContext);
        convertView = li.inflate(random,parent,false);

        ImageView image = convertView.findViewById(R.id.image);
        TextView category = convertView.findViewById(R.id.category);

        image.setImageResource(getItem(position).getImage());
        category.setText(getItem(position).getCategory());
        return convertView;
    }
}
