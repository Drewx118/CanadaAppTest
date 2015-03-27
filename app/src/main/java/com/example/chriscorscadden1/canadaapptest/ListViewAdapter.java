package com.example.chriscorscadden1.canadaapptest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by chriscorscadden1 on 27/03/2015.
 */
public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    CanadaFacts data;
    //ImageLoader imageLoader;
    Fact fact = new Fact();

    public ListViewAdapter(Context context, CanadaFacts data) {
        this.context = context;
        this.data = data;

        //imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.rows.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView title;
        TextView description;
        //ImageView img;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        fact = data.rows.get(position);

        // Locate the TextViews in listview_item.xml
        title = (TextView) itemView.findViewById(R.id.fact_title);
        description = (TextView) itemView.findViewById(R.id.fact_description);

        // Locate the ImageView in listview_item.xml
        //img = (ImageView) itemView.findViewById(R.id.fact_img);

        // Capture position and set results to the TextViews
        title.setText(fact.title);
        description.setText(fact.description);
        // Capture position and set results to the ImageView
        // Passes images URL into ImageLoader.class
        //imageLoader.DisplayImage(fact.imageHref, img);


        // Capture ListView item click
        /*itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                fact = data.rows[position];
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all data title
                intent.putExtra("title", fact.rows[position]);
                // Pass all data description
                intent.putExtra("description", fact.rows[position]);
                // Pass all data img
                intent.putExtra("img", fact.rows[position]);
                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });*/
        return itemView;
    }

}
