package com.god.Caculator_20200123;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CalState implements Parcelable {
    public String express;
    public double value;

    public CalState (String express,double value){
        this.express = express;
        this.value = value;
    }
    public CalState(String express){
        this.value = 0;
        this.express = express;
    }
    public CalState(){
        this.value = 0;
        this.express = "";
    }

    protected CalState(Parcel in) {
        express = in.readString();
        value = in.readDouble();
    }

    public static String normalizeStringTypeDoubleToString(String stringVal) {
        int lastIndex = stringVal.length() - 1;
        if (lastIndex < 1) {
            return stringVal;
        }
        double val = Double.parseDouble(stringVal);
        String lastTwoChar = stringVal.substring(lastIndex - 1, lastIndex);
        if (
                lastTwoChar.equals(".0")
                        || (stringVal.charAt(lastIndex) != '.'
                        && val >= Integer.MIN_VALUE
                        && val <= Integer.MAX_VALUE
                        && val == (int) val)
        ) {
            return (int) val + "";
        } else {
            return stringVal;
        }
    }
    public String getNormalizeValue(){
        return CalState.normalizeDoubleToString(value);
    }
    public static String normalizeDoubleToString(double val) {
        if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE && val == (int) val) {
            return "" + (int) val;
        } else {
            return "" + val;
        }
    }
    public static final Creator<CalState> CREATOR = new Creator<CalState>() {
        @Override
        public CalState createFromParcel(Parcel in) {
            return new CalState(in);
        }

        @Override
        public CalState[] newArray(int size) {
            return new CalState[size];
        }
    };

    public boolean isDefault(){
        return value==0&&express.equals("");
    }
    public boolean isWaitingForOperand(){
        int lastIndexOfExpress = express.length()-1;
        return lastIndexOfExpress>-1&&express.charAt(lastIndexOfExpress)!='=';
    }
    public CalState clone(){
        return new CalState(express,value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(express);
        dest.writeDouble(value);
    }
}
