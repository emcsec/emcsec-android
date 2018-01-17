package com.aspanta.emcsec.ui.fragment.receiveCoinFragment.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.EmcAddress;

import java.util.List;

public class EmercoinAdapterForSpinner extends ArrayAdapter<EmcAddress> {

    LayoutInflater mLayoutInflater;
    int resourceViewId;

    public EmercoinAdapterForSpinner(Context context, @LayoutRes int resourceId, List<EmcAddress> list) {
        super(context, resourceId, list);
        resourceViewId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        EmcAddress emcAddress = getItem(position);

        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {

            holder = new ViewHolder();
            mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = mLayoutInflater.inflate(R.layout.item_spinner, null, false);

            holder.tvAddress = rowView.findViewById(R.id.tv_address_spinner);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.tvAddress.setText(emcAddress.getAddress());

        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return itemView(convertView, position);
    }

    private View itemView(View convertView, int position) {

        EmcAddress emcAddress = getItem(position);

        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {

            holder = new ViewHolder();
            mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = mLayoutInflater.inflate(R.layout.item_spinner_menu_emc, null, false);

            holder.tvLabel = rowView.findViewById(R.id.tv_label_spinner);
            holder.tvAddress = rowView.findViewById(R.id.tv_address_spinner);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.tvLabel.setText(emcAddress.getLabel());
        holder.tvAddress.setText(emcAddress.getAddress());

        return rowView;
    }

    private class ViewHolder {
        TextView tvLabel;
        TextView tvAddress;
    }
}
