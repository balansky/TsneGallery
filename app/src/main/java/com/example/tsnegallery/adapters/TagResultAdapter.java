package com.example.tsnegallery.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsnegallery.R;

import java.util.ArrayList;


public class TagResultAdapter extends RecyclerView.Adapter<TagResultAdapter.ResultHolder> {

    private ArrayList<String> mData;
    private static Context contxt;

    public static class ResultHolder extends RecyclerView.ViewHolder{

        public TextView keyView;
        public TextView valueView;

        public ResultHolder(LinearLayout v){
            super(v);
            keyView = v.findViewById(R.id.resultKey);
            valueView = v.findViewById(R.id.resultValue);
        }
    }

    public TagResultAdapter(Context ctxt){

        contxt = ctxt;
        mData = new ArrayList<String>();
    }

    public void addItem(String r){
        int len = mData.size();
        mData.add(r);
        notifyItemInserted(len);
    }

    public void removeAll(){
        int len = mData.size();
        mData.clear();
        notifyItemRangeChanged(0, len);
    }


    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_results, parent, false);
        ResultHolder vh = new ResultHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        Resources res = contxt.getResources();
        holder.keyView.setText(String.format(res.getString(R.string.resultKey), position));
        holder.valueView.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
