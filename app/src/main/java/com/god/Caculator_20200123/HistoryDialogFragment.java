package com.god.Caculator_20200123;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;
import android.widget.Button;

public class HistoryDialogFragment extends DialogFragment {
    private CalState[] history;
    private final HistoryCalDialogResult historyCalDialogResult = new HistoryCalDialogResult();
    private HistoryCalAdapter adapter;

    public interface OnClickItemListener {
        void onClickItem(HistoryCalDialogResult result);
    }
    private static OnClickItemListener onClickItemListener;
    public static void setOnClickItemListener(OnClickItemListener listener) {
        onClickItemListener = listener;
    }
    public interface OnClearHistoryListener {
        void onClickClearHistoryBtn(HistoryCalDialogResult result);
    }
    private static OnClearHistoryListener onClearHistoryListener;
    public static void setOnClearHistoryListener(OnClearHistoryListener listener) {
        onClearHistoryListener = listener;
    }
    private  static  HistoryDialogFragment historyDialogFragment;
    public  static void showHistoryDialogFragment(FragmentManager fragmentManager, CalState[] history){
        if(historyDialogFragment==null){
            historyDialogFragment = new HistoryDialogFragment();
        }
        historyDialogFragment.history = history;
        historyDialogFragment.show(fragmentManager,"show");
    }
    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dlg_fragment_history);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        Button clearHistoryBtn = (Button) dialog.findViewById(R.id.clear_history);
        dialog.getWindow().getAttributes().windowAnimations = R.anim.zoom_in;

        clearHistoryBtn.setOnClickListener(v -> {
            historyCalDialogResult.isClearHistory=true;
            if(adapter!=null){
                adapter.clear();
            }
            onClearHistoryListener.onClickClearHistoryBtn(historyCalDialogResult);
        });
        if(history!=null){
            adapter = new HistoryCalAdapter(history);
            adapter.setOnItemClickListener((view, position) -> {
                historyCalDialogResult.selectedIndex = position;
                onClickItemListener.onClickItem(historyCalDialogResult);
                dialog.dismiss();
            });
            recyclerView.setAdapter(adapter);
        }
        return dialog;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            // Set the width of the dialog to 80% of the screen width
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}