package com.homeautogroup.med_manager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.models.Medicine;

import java.util.ArrayList;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Medicine> modelList;

    // private OnItemClickListener mItemClickListener;


    public RecyclerViewAdapter(Context context, ArrayList<Medicine> modelList) {
        this.modelList = modelList;
    }

    public void updateList(ArrayList<Medicine> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final Medicine model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.itemTxtTitle.setText(model.getMedicineName());
            genericViewHolder.itemTxtMessage.setText(model.getMedicineDesc());
            genericViewHolder.itemView.setTag(model.uniqueFirebaseId);
            genericViewHolder.imgUser.setImageResource(model.getSelectedIcon());
            genericViewHolder.itemTextStartDate.setText("Start date: " + model.getStartDate());
            genericViewHolder.itemTextEndDate.setText("Finish date: " + model.getEndDate());
        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }

    /*public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }*/

    private Medicine getItem(int position) {
        return modelList.get(position);
    }

/*
    public interface OnItemClickListener {
        void onItemClick(View view, int position, Medicine model);
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUser;
        private TextView itemTxtTitle;
        private TextView itemTxtMessage;
        private TextView itemTextStartDate, itemTextEndDate;


        ViewHolder(final View itemView) {
            super(itemView);

            this.imgUser = itemView.findViewById(R.id.img_user);
            this.itemTxtTitle = itemView.findViewById(R.id.item_txt_title);
            this.itemTxtMessage = itemView.findViewById(R.id.item_txt_message);
            this.itemTextStartDate = itemView.findViewById(R.id.item_txt_start_date);
            this.itemTextEndDate = itemView.findViewById(R.id.item_txt_end_date);


           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));


                }
            });*/

        }
    }

}

