package com.example.funtravelapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying booking history loaded from Firebase.
 */
public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onCancel(int position);
        void onView(int position);
        void onDelete(int position);
    }

    private final List<BookingItem> bookingList;
    private final OnItemClickListener listener;

    // Optional highlight support
    private String highlightId = null;
    private int highlightColor = 0x30FFEB3B; // Semi-transparent yellow

    public BookingHistoryAdapter(List<BookingItem> list, OnItemClickListener l) {
        this.bookingList = list;
        this.listener = l;
        setHasStableIds(false);
    }

    /**
     * Call this to highlight a specific booking by its bookingId.
     */
    public void setHighlightId(String highlightId) {
        this.highlightId = highlightId;
        notifyDataSetChanged();
    }

    /**
     * (Optional) Change highlight color dynamically.
     */
    public void setHighlightColor(int argb) {
        this.highlightColor = argb;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryAdapter.ViewHolder holder, int position) {
        BookingItem item = bookingList.get(position);

        Log.d("BOOKING_ADAPTER", "Bind position=" + position
                + " bookingId=" + item.getBookingId()
                + " dest=" + item.getDestinationName());

        // Build main info
        String topLine = "Destination: " + safe(item.getDestinationName()) +
                "\nName: " + safe(item.getUserName());
        holder.txtUser.setText(topLine);

        holder.txtPeople.setText("People: " + item.getPeople());
        holder.txtTotal.setText(String.format("Total Price: RM%.2f", item.getTotalPrice()));
        holder.txtDate.setText("Date: " + safe(item.getVisitDate()));
        holder.txtNotes.setText(
                (item.getNotes() == null || item.getNotes().trim().isEmpty())
                        ? "Notes: -" : "Notes: " + item.getNotes()
        );

        // Resolve image from resource name
        int resolvedResId = 0;
        if (item.getImageResName() != null && !item.getImageResName().isEmpty()) {
            Context ctx = holder.itemView.getContext();
            resolvedResId = ctx.getResources()
                    .getIdentifier(item.getImageResName(), "drawable", ctx.getPackageName());
        }
        if (resolvedResId != 0) {
            holder.bookingImage.setImageResource(resolvedResId);
        } else {
            holder.bookingImage.setImageResource(R.drawable.bgapp); // fallback image
        }

        // Highlight logic
        if (highlightId != null && highlightId.equals(item.getBookingId())) {
            holder.itemView.setBackgroundColor(highlightColor);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Button actions
        holder.btnView.setOnClickListener(v -> listener.onView(holder.getAdapterPosition()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(holder.getAdapterPosition()));
        holder.btnRate.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, RateBookingActivity.class);
            // Keep key name "destination" for backward compatibility
            intent.putExtra("destination", item.getDestinationName());
            ctx.startActivity(intent);
        });

        // Entire row click -> view details
        holder.itemView.setOnClickListener(v -> listener.onView(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtPeople, txtTotal, txtDate, txtNotes;
        Button btnView, btnRate;
        ImageButton btnDelete;
        ImageView bookingImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.history_user);
            txtPeople = itemView.findViewById(R.id.history_people);
            txtTotal = itemView.findViewById(R.id.history_total);
            txtDate = itemView.findViewById(R.id.history_date);
            txtNotes = itemView.findViewById(R.id.history_notes);
            btnView = itemView.findViewById(R.id.history_view_button);
            btnDelete = itemView.findViewById(R.id.history_delete_button);
            btnRate = itemView.findViewById(R.id.history_rate_button);
            bookingImage = itemView.findViewById(R.id.booking_image);
        }
    }

    private String safe(String s) {
        return s == null ? "-" : s;
    }
}
