package com.example.chriscorscadden1.canadaapptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    private Context context;
    private LayoutInflater inflater;
    private CanadaFacts data;
    private ImageLoader imageLoader;
    private Fact fact = new Fact();

    public ListViewAdapter(Context context, CanadaFacts data) {
        this.context = context;
        this.data = data;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.getRows().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Gets the view for each fact item
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView title;
        TextView description;
        ImageView img;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Sets the itemView to listview_item.xml
        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        fact = data.getRows().get(position);

        // Locate the TextViews in listview_item.xml
        title = (TextView) itemView.findViewById(R.id.fact_title);
        description = (TextView) itemView.findViewById(R.id.fact_description);

        // Locate the ImageView in listview_item.xml
        img = (ImageView) itemView.findViewById(R.id.fact_img);

        // Capture position and set results to the TextViews
        title.setText(fact.getTitle());
        description.setText(fact.getDescription());
        // Capture position and set results to the ImageView
        // Passes images URL into ImageLoader.class
        if(fact.getImageHref() != null)
            imageLoader.DisplayImage(fact.getImageHref(), img);
        return itemView;
    }
}
