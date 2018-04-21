package com.example.vibhor.imageshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * Created by Vibhor on 21-04-2018.
 */

public class DBAdapter extends ArrayAdapter
{

    ArrayList<PicDetails> picURLS ;
    Context context;

    public DBAdapter(Context context,ArrayList<PicDetails> arrayList)
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
        ViewHoldr holder;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_image, parent, false);

            holder = new ViewHoldr();
            holder.imageView = row.findViewById(R.id.one_image);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHoldr) row.getTag();
        }

        holder.imageView.setImageBitmap(picURLS.get(position).getStorepic());

        return row;
    }
}

class ViewHoldr
{
    ImageView imageView;
}
