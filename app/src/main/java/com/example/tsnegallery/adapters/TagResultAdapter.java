package com.example.tsnegallery.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsnegallery.R;


public class TagResultAdapter extends RecyclerView.Adapter<TagResultAdapter.ResultHolder> {

    private String[] fakeData = {"person", "dog", "cat", "elephant", "banana", "apple", "mamam"};
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
        holder.valueView.setText(fakeData[position]);
    }

    @Override
    public int getItemCount() {
        return fakeData.length;
    }
}
