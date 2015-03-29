package com.example.chriscorscadden1.canadaapptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

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
        TextView title;
        TextView description;
        ImageView img;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        fact = data.getRows().get(position);

        title = (TextView) itemView.findViewById(R.id.fact_title);
        description = (TextView) itemView.findViewById(R.id.fact_description);
        img = (ImageView) itemView.findViewById(R.id.fact_img);

        title.setText(fact.getTitle());
        description.setText(fact.getDescription());
        if(fact.getImageHref() != null)
            imageLoader.DisplayImage(fact.getImageHref(), img);

        return itemView;
    }
}
