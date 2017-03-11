package com.iit.sramaraj.safewalks;

/**
 * Created by Nivash on 6/19/2016.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private String[] summary=null;
    private String [] distance=null;
    private String[] duration=null;
    public CustomList(Activity context,
                      String[] summary, String[] distance,String[] duration) {
        super(context, R.layout.list_single, summary);
        this.context = context;

        this.summary = summary;

        this.distance= distance;

        this.duration = duration;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView txtMiles = (TextView) rowView.findViewById(R.id.txt1);
        TextView txtMin=(TextView)rowView.findViewById(R.id.txt2);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(summary[position]);
        txtMiles.setText(distance[position]);
        txtMin.setText(duration[position]);
        imageView.setImageResource(R.drawable.pedestrian);
        return rowView;
    }
}