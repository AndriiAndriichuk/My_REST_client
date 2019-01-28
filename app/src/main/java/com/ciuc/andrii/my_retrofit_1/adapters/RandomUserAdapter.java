package com.ciuc.andrii.my_retrofit_1.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ciuc.andrii.my_retrofit_1.R;
import com.ciuc.andrii.my_retrofit_1.pojo.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hari on 20/11/17.
 */

public class RandomUserAdapter extends RecyclerView.Adapter<RandomUserAdapter.RandomUserViewHolder> {

    public List<Result> resultList = new ArrayList<>();


    public RandomUserAdapter() {
    }

    @Override
    public RandomUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_random_user,parent, false);
        return new RandomUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RandomUserViewHolder holder, int position) {
        Result result = resultList.get(position);
        holder.textName.setText(result.getName().getFirst() + ' ' + result.getName().getLast());
        holder.textNumber.setText(result.getCell());
        holder.textEmail.setText(result.getEmail());
        Picasso.with(holder.imageView.getContext())
                .load(result.getPicture().getLarge())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void setItems(List<Result> results) {
        resultList = results;
        notifyDataSetChanged();
    }

    public class RandomUserViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public ImageView imageView;
        public TextView textNumber;
        public TextView textEmail;

        public RandomUserViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.image);
            textNumber = itemView.findViewById(R.id.user_number);
            textEmail = itemView.findViewById(R.id.user_email);
        }
    }
}