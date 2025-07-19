package com.example.funtravelapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private List<Destination> destinationList;
    private Context context;

    public DestinationAdapter(List<Destination> destinationList, Context context) {
        this.destinationList = destinationList;
        this.context = context;
    }

    @NonNull
    @Override
    public DestinationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationAdapter.ViewHolder holder, int position) {
        Destination destination = destinationList.get(position);

        holder.txtName.setText(destination.getName());
        holder.txtLocation.setText(destination.getLocation());
        holder.txtPrice.setText(destination.getPrice());
        holder.ratingBar.setRating(destination.getRating());
        holder.imageView.setImageResource(destination.getImageResId());

        // Apabila tekan butang Book Now
        holder.btnBook.setOnClickListener(v -> {
            Context viewContext = v.getContext(); // Pastikan context ini betul (Activity context)
            Intent intent = new Intent(viewContext, BookingActivity.class);
            intent.putExtra("destination_name", destination.getName());
            intent.putExtra("destination_location", destination.getLocation());
            intent.putExtra("destination_price", destination.getPrice());
            intent.putExtra("destination_rating", destination.getRating());
            intent.putExtra("destination_image", destination.getImageResId());
            viewContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtLocation, txtPrice;
        RatingBar ratingBar;
        Button btnBook;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_destination_name);
            txtLocation = itemView.findViewById(R.id.txt_destination_location);
            txtPrice = itemView.findViewById(R.id.txt_destination_price);
            ratingBar = itemView.findViewById(R.id.destination_rating);
            btnBook = itemView.findViewById(R.id.btn_book);
            imageView = itemView.findViewById(R.id.image_destination);
        }
    }
}
