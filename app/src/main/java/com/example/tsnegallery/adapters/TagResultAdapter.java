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
import com.example.tsnegallery.tflite.ObjectDetectionModel;

import java.util.ArrayList;
import java.util.List;


public class TagResultAdapter extends RecyclerView.Adapter<TagResultAdapter.ResultHolder> {

    private ArrayList<ObjectDetectionModel.Recognition> mData;
    private Context contxt;

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
        mData = new ArrayList<ObjectDetectionModel.Recognition>();
    }

    public void removeAll(){
        int nOld = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, nOld);
    }


    public void updateResults(List<ObjectDetectionModel.Recognition> results){
        int nOld = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, nOld);
        mData.addAll(results);
        notifyItemRangeChanged(0, mData.size());
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
        ObjectDetectionModel.Recognition r = mData.get(position);
        holder.keyView.setText(r.getTitle());
        holder.valueView.setText(String.format(".2%f", r.getConfidence()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
