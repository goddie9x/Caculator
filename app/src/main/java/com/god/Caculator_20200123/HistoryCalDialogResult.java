package com.god.Caculator_20200123;

public class HistoryCalDialogResult {
    public boolean isClearHistory;
    public int selectedIndex;

    public HistoryCalDialogResult(boolean isClearHistory,int selectedIndex){
        this.isClearHistory = isClearHistory;
        this.selectedIndex = selectedIndex;
    }
    public  HistoryCalDialogResult(){
        this(false,-1);
    }
    public HistoryCalDialogResult(int selectedIndex){
        this(false,selectedIndex);
    }
    public HistoryCalDialogResult(boolean isClearHistory){
        this(isClearHistory,-1);
    }
}
