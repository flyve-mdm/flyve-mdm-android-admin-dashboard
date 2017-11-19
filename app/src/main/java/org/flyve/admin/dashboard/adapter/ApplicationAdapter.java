package org.flyve.admin.dashboard.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.flyve.admin.dashboard.R;

import java.util.ArrayList;
import java.util.HashMap;


public class ApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<HashMap<String, String>> data;

    private static final int ITEM_TYPE_DATA = 0;
    private static final int ITEM_TYPE_HEADER = 1;

    public ApplicationAdapter(ArrayList<HashMap<String, String>> data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if((data.get(position)).get("type").equals("header")) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_DATA;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if(viewType == ITEM_TYPE_DATA) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_application_item, viewGroup, false);
            return new DataViewHolder(v);
        }

        else if (viewType == ITEM_TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_application_header, viewGroup, false);
            return new HeaderViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> response = data.get(position);

        final int itemType = getItemViewType(position);

        if(itemType == ITEM_TYPE_DATA) {
            ((DataViewHolder)holder).bindData( response );
        }

        if(itemType == ITEM_TYPE_HEADER) {
            ((HeaderViewHolder)holder).bindData( response );
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        TextView packages;
        TextView version;

        DataViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.img);
            name = (TextView)itemView.findViewById(R.id.name);
            packages = (TextView)itemView.findViewById(R.id.packages);
            version = (TextView)itemView.findViewById(R.id.version);
        }

        public void bindData(HashMap<String, String> model) {

            byte[] decodedString = Base64.decode( model.get("icon").getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img.setImageBitmap(decodedByte);

            name.setText( Html.fromHtml( model.get("name")) );
            packages.setText( Html.fromHtml( model.get("package") ));
            version.setText( Html.fromHtml( "v." +  model.get("version") ));
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
        }

        public void bindData(HashMap<String, String> model) {
            title.setText( Html.fromHtml( model.get("title") ));
        }
    }


}