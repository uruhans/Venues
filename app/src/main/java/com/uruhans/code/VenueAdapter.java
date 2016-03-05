package com.uruhans.code;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by uruha on 29-02-2016.
 */
public class VenueAdapter extends ArrayAdapter<Venues.Response.Venue>  {

    private ArrayList<Venues.Response.Venue> venueList;
    private VenueAdapterListener mListener;

    public VenueAdapter(Context context, ArrayList<Venues.Response.Venue> venues, VenueAdapterListener listener) {
        super(context, R.layout.venue_row, venues);
        this.mListener = listener;
        this.venueList = venues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VenueHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.venue_row, parent, false);
            holder = new VenueHolder(row);
            row.setTag(holder);

        } else {
            holder = (VenueHolder) row.getTag();
        }
        holder.maps.setTag(position);
        holder.phone.setTag(position);
        holder.google.setTag(position);

        holder.maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                mListener.onMapClicked(venueList.get(position));
            }
        });

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                mListener.onPhoneClicked(venueList.get(position));
            }
        });
        holder.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                mListener.onGoogleClicked(venueList.get(position));
            }
        });

        holder.populateFrom(venueList.get(position));
        return (row);
    }

    static class VenueHolder {
        private TextView name;
        private TextView address;
        private TextView distance;
        private ImageView maps;
        private ImageView phone;
        private ImageView google;
        private View row;

        VenueHolder(View row) {
            this.setRow(row);
            name = (TextView) row.findViewById(R.id.name);
            address = (TextView) row.findViewById(R.id.address);
            distance = (TextView) row.findViewById(R.id.distance);
            maps = (ImageView) row.findViewById(R.id.maps);
            phone = (ImageView) row.findViewById(R.id.phone);
            google = (ImageView) row.findViewById(R.id.google);
        }

        void populateFrom(Venues.Response.Venue r) {
            name.setText(r.name);
            address.setText(TextUtils.isEmpty(r.location.address) ? "na" : r.location.address);
            String dist = "";
            if (r.location.distance > 999) {
                DecimalFormat df = new DecimalFormat("0.00");
                dist = df.format(r.location.distance / 1000.0f) + " km";
            } else {
                dist = r.location.distance + " m";
            }
            distance.setText(dist);
            phone.setVisibility(TextUtils.isEmpty(r.contact.phone) ? View.GONE : View.VISIBLE);
        }

        public void setRow(View row) {
            this.row = row;
        }

        public View getRow() {
            return row;
        }
    }

    public interface VenueAdapterListener {
        void onMapClicked(Venues.Response.Venue venue);
        void onGoogleClicked(Venues.Response.Venue venue);
        void onPhoneClicked(Venues.Response.Venue venue);
    }
}
