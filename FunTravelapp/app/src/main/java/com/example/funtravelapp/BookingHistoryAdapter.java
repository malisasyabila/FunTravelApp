package com.example.funtravelapp;

import android.content.Intent;
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

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onCancel(int position);
        void onView(int position);
        void onDelete(int position);
    }

    private List<BookingItem> bookingList;
    private OnItemClickListener listener;

    public BookingHistoryAdapter(List<BookingItem> list, OnItemClickListener l) {
        bookingList = list;
        listener = l;
    }

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

    @NonNull
    @Override
    public BookingHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryAdapter.ViewHolder holder, int pos) {
        BookingItem i = bookingList.get(pos);

        Log.d("DEBUG_IMAGE", "ImageResId: " + i.getImageResId());

        holder.txtUser.setText("Name: " + i.getUserName());
        holder.txtPeople.setText("People: " + i.getPeople());
        holder.txtTotal.setText(String.format("Total Price: RM%.2f", i.getTotalPrice()));
        holder.txtDate.setText("Date: " + i.getVisitDate());
        holder.txtNotes.setText(i.getNotes().isEmpty() ? "Notes: -" : "Notes: " + i.getNotes());

        holder.bookingImage.setImageResource(i.getImageResId()); //

        holder.btnView.setOnClickListener(v -> listener.onView(pos));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(pos));
        holder.btnRate.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            Intent intent = new Intent(context, RateBookingActivity.class);
            intent.putExtra("destination", i.getDestination());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }
}
