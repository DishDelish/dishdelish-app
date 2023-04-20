package com.github.siela1915.bootcamp.Recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siela1915.bootcamp.R;

import java.util.ArrayList;

public class DropdownMenuAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mItems;

    public DropdownMenuAdapter(Context context, ArrayList<String> items) {
        mContext = context;
        mItems = items;
    }

    public void addItem(String item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.dropdown_menu_item, parent, false);
        }

        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(mItems.get(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
