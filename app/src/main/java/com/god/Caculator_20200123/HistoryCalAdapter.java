package com.god.Caculator_20200123;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryCalAdapter extends RecyclerView.Adapter  {
    private CalState[] history;

    public  interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    private  OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mListener){
        this.onItemClickListener = mListener;
    }
    public HistoryCalAdapter(CalState[] history) {
        this.history = history;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.caculate_history_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CalState crrCalState = history[position];
        ((ViewHolder)holder).expressLabel.setText(crrCalState.express);
        ((ViewHolder)holder).valueLabel.setText(crrCalState.getNormalizeValue());
        int crrPosition = position;
        ((ViewHolder)holder).fragmentHistoryItemWrapper.setOnClickListener(v -> onItemClickListener.onItemClick(((ViewHolder) holder).view,crrPosition));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return history.length;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        history = new CalState[0];
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private final TextView expressLabel;
        private final TextView valueLabel;

        private final LinearLayout fragmentHistoryItemWrapper;

        public ViewHolder(View itemView){
            super(itemView);
            fragmentHistoryItemWrapper = (LinearLayout)itemView.findViewById(R.id.history_item_wrapper);
            expressLabel = (TextView) itemView.findViewById(R.id.cal_express_label);
            valueLabel = (TextView) itemView.findViewById(R.id.output_label);
        }
    }
}
