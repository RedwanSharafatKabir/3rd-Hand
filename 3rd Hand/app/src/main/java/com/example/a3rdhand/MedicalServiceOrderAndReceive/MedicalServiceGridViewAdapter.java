package com.example.a3rdhand.MedicalServiceOrderAndReceive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a3rdhand.R;

public class MedicalServiceGridViewAdapter extends BaseAdapter {
    int [] diseaseImages;
    String diseaseNames[];
    Context context;
    LayoutInflater layoutInflater;

    public MedicalServiceGridViewAdapter(Context context, int[] diseaseImages, String[] diseaseNames) {
        this.context = context;
        this.diseaseImages = diseaseImages;
        this.diseaseNames = diseaseNames;
    }

    @Override
    public int getCount() {
        return diseaseNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_medical_grid_view_adapter, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.diseaseImageID);
        TextView textView = convertView.findViewById(R.id.diseaseNameID);
        imageView.setImageResource(diseaseImages[position]);
        textView.setText(diseaseNames[position]);

        return convertView;
    }
}
