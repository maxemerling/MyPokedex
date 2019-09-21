package com.example.mypokedex;

import android.content.Context;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class PokeAdapter extends RecyclerView.Adapter<PokeAdapter.MyViewHolder> {
    private Pokemon[] dataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.pokeName);
            imageView = view.findViewById(R.id.pokeImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PokeAdapter(Pokemon[] myDataset, Context context) {
        this.dataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PokeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.poke_view, parent, false);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Pokemon curr = dataset[position];
        holder.textView.setText(curr.getName());

        String url = curr.getURL();
        if (false && Patterns.WEB_URL.matcher(url).matches()) {
            Glide.with(context)
                    .load(url)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.pokeball);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.length;
    }

    public synchronized void updateData(Pokemon[] newDataset) {
        dataset = newDataset;
        notifyDataSetChanged();
    }
}

