package com.ae.apps.lib.sample.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ae.apps.lib.sample.R;
import com.ae.apps.lib.sample.models.FeatureInfo;

import java.util.List;

public class FeaturesRecyclerViewAdapter extends RecyclerView.Adapter<FeaturesRecyclerViewAdapter.ViewHolder> {

    private List<FeatureInfo> mData;
    private ItemClickListener mListener;
    private LayoutInflater mInflater;

    public FeaturesRecyclerViewAdapter(Context context, List<FeatureInfo> data) {
        mInflater = LayoutInflater.from(context);
        mListener = (ItemClickListener) context;
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.row_feature, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FeatureInfo feature = mData.get(i);
        viewHolder.featureName.setText(feature.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView featureName;

        ViewHolder(View item) {
            super(item);
            featureName = item.findViewById(R.id.textFeatureName);
            featureName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}


