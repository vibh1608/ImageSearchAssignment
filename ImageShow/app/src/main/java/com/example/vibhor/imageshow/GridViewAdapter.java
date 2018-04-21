package com.example.vibhor.imageshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;

/**
 * Created by Vibhor on 18-04-2018.
 */

public class GridViewAdapter extends ArrayAdapter
{

    ArrayList<String> picURLS ;
    Context context;

    public GridViewAdapter(Context context,ArrayList<String> arrayList)
    {
        super(context, 0);
        this.context = context;
        picURLS = arrayList;
    }

    public int getCount() {
        return picURLS.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_image, parent, false);

            holder = new ViewHolder();
            holder.imageView = row.findViewById(R.id.one_image);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        Glide.with(context)
                .load(picURLS.get(position))
                .override(175,175)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fitCenter()
                .error(R.drawable.default_pic)
                .into(holder.imageView);

        return row;
    }
}

class ViewHolder
{
    ImageView imageView;
}
