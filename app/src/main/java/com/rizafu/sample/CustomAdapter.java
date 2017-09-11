package com.rizafu.sample;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rizafu.sample.databinding.ItemCustomBinding;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by RizaFu on 1/2/17.
 */

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MainViewHolder> {
    private static final String TITLE = "title";
    private static final String SUBTITLE = "subTitle";
    private ArrayList<HashMap<String,String>> items;
    private Context context;

    public CustomAdapter() {
        this.items = new ArrayList<>();
    }

    public void addItem(String title, @Nullable String subTitle){
        HashMap<String,String> map = new HashMap<>();
        map.put(TITLE,title);
        map.put(SUBTITLE, subTitle);
        items.add(map);
        notifyDataSetChanged();
    }

    private OnItemClick onItemClick;

    public interface OnItemClick{
        void onClick(View view, int position);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemCustomBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_custom,parent,false);
        return new MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClick!=null) onItemClick.onClick(view,position);
            }
        });

        holder.getBinding().title.setText(getItem(position).get(TITLE));

        if (!TextUtils.isEmpty(getItem(position).get(SUBTITLE))){
            holder.getBinding().subTitle.setText(getItem(position).get(SUBTITLE));
            holder.getBinding().subTitle.setVisibility(View.VISIBLE);
        } else {
            holder.getBinding().subTitle.setVisibility(View.GONE);
        }
        if (position == getItemCount()-1){
            int margin = (int) context.getResources().getDimension(R.dimen.margin_item);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin,margin,margin,margin);
            holder.getBinding().container.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private HashMap<String, String> getItem(int position) {
        return items.get(position);
    }

    static class MainViewHolder extends RecyclerView.ViewHolder{
        private ItemCustomBinding binding;

        public MainViewHolder(ItemCustomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemCustomBinding getBinding() {
            return binding;
        }
    }
}
