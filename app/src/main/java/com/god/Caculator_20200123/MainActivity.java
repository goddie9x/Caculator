package com.god.Caculator_20200123;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private final String STATE = MainActivity.class.getSimpleName();
    private TextView expressLabel;
    private TextView outputLabel;
    private boolean isHaveToClearInput = false;
    private final Vector<CalState> history = new Vector<>();
    private final CalState currentCalState = new CalState();
    private String crrOperator;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        int amountHisCalSate = sharedPreferences.getInt("amountHisCalState",0);

        setContentView(R.layout.activity_main);
        expressLabel = (TextView) findViewById(R.id.cal_express_label);
        outputLabel = (TextView) findViewById(R.id.output_label);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim,R.anim.zoom_in);

        if(amountHisCalSate>0){
            for(int i = 0;i< amountHisCalSate;i++){
                double crrValue = sharedPreferences.getFloat("value"+i,0);
                String crrExpress = sharedPreferences.getString("express"+i, "");
                CalState crrCal = new CalState(crrExpress,crrValue);
                history.add(crrCal);
            }
        }
        initHistoryFragment();

        Button historyBtn = (Button) findViewById(R.id.history);
        historyBtn.setOnClickListener(v -> HistoryDialogFragment
                .showHistoryDialogFragment(getSupportFragmentManager(),
                        history.toArray(new CalState[history.size()])));
        initEvent();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.e(STATE, "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int amountCalHis = history.size();
        editor.putInt("amountHisCalState",amountCalHis);
        for(int i = 0;i<amountCalHis;i++){
            CalState crrCal = history.elementAt(i);
            editor.putFloat("value"+i, (float) crrCal.value);
            editor.putString("express"+i,crrCal.express);
        }
        editor.commit();
    }

    private void initHistoryFragment() {
        HistoryDialogFragment.setOnClickItemListener(result -> {
            if (result != null&&result.selectedIndex > -1 && result.selectedIndex < history.size()) {
                CalState selectedCalState = history.elementAt(result.selectedIndex);
                setExpress(selectedCalState.express);
                setValue(selectedCalState.value);
            }
        });
        HistoryDialogFragment.setOnClearHistoryListener(result -> {
            if (result != null&&result.isClearHistory) {
                history.clear();
            }
        });
    }

    private void initEvent() {
        initEventForClearAllBtn();
        initEventForClearResultBtn();
        initEventForClearSingleBtn();
        initEventForOneHandOperatorsBtn();
        initEventForEqualBtn();
        initEventForNumbersBtn();
        initEventForTwoHandOperatorsBtn();
        initEventForDotBtn();
    }

    private void initEventForNumbersBtn() {
        handleNumberEventClick((Button) findViewById(R.id.zero));
        handleNumberEventClick((Button) findViewById(R.id.one));
        handleNumberEventClick((Button) findViewById(R.id.two));
        handleNumberEventClick((Button) findViewById(R.id.three));
        handleNumberEventClick((Button) findViewById(R.id.four));
        handleNumberEventClick((Button) findViewById(R.id.five));
        handleNumberEventClick((Button) findViewById(R.id.six));
        handleNumberEventClick((Button) findViewById(R.id.seven));
        handleNumberEventClick((Button) findViewById(R.id.eight));
        handleNumberEventClick((Button) findViewById(R.id.nine));
    }

    private void initEventForDotBtn() {
        ((Button) findViewById(R.id.dot)).setOnClickListener(v -> {
            String crrInputString = getInputText();
            if (!crrInputString.contains(".")) {
                outputLabel.setText(crrInputString + ".");
            }
        });
    }


    private void initEventForClearAllBtn() {
        ((Button) findViewById(R.id.clear_all)).setOnClickListener(v -> {
            crrOperator = null;
            setNewCalState("", 0);
        });
    }

    private void initEventForClearResultBtn() {
        ((Button) findViewById(R.id.clear_cal)).setOnClickListener(v->setValue(0));
    }

    private void initEventForClearSingleBtn() {
        ((Button) findViewById(R.id.clear_single)).setOnClickListener(v -> {
            String strNum = getInputText();
            double newValue = Double.parseDouble(strNum);

            if (strNum.contains("E")) {
                newValue /= 10;
            } else {
                int lastIndex = strNum.length() - 1;
                if (lastIndex != 0) {
                    strNum = strNum.substring(0, (strNum.charAt(lastIndex) == '.' ? lastIndex - 1 : lastIndex));
                    newValue = Double.parseDouble(strNum);
                } else {
                    newValue = 0;
                }
            }
            setValue(newValue);
        });
    }
    private void initEventForOneHandOperatorsBtn(){
        initEventForReverseBtn();
        initEventForOppositeBtn();
        initEventForSqrtBtn();
    }
    private void initEventForTwoHandOperatorsBtn() {
        handleOperatorEventClick((Button) findViewById(R.id.div));
        handleOperatorEventClick((Button) findViewById(R.id.minus));
        handleOperatorEventClick((Button) findViewById(R.id.plus));
        handleOperatorEventClick((Button) findViewById(R.id.multiple));
        handleOperatorEventClick((Button) findViewById(R.id.mod));
        handleOperatorEventClick((Button) findViewById(R.id.exponentiation));
    }

    private void initEventForReverseBtn() {
        ((Button) findViewById(R.id.reverse)).setOnClickListener(v -> handleReverseValue());
    }

    private void initEventForOppositeBtn() {
        ((Button) findViewById(R.id.opposite)).setOnClickListener(v -> handleOppositeValue());
    }

    private void initEventForSqrtBtn() {
        ((Button) findViewById(R.id.sqrt)).setOnClickListener(v -> handleSqrtValue());
    }

    private void initEventForEqualBtn() {
        ((Button) findViewById(R.id.equal)).setOnClickListener(v -> startCal());
    }

    private void startCal() {
        if (crrOperator == null) {
            return;
        }
        String crrInputString = getInputText();
        double crrInputNumber;
        try {
            crrInputNumber = Double.parseDouble(crrInputString);
        } catch (NumberFormatException e) {
            return;
        }
        switch (crrOperator) {
            case "+": {
                handleAddValue(crrInputNumber);
                break;
            }
            case "-": {
                handleMinusValue(crrInputNumber);
                break;
            }
            case "/": {
                handleDivValue(crrInputNumber);
                break;
            }
            case "x": {
                handleMultipleValue(crrInputNumber);
                break;
            }
            case "%": {
                handleModuleValue(crrInputNumber);
                break;
            }
            case "^": {
                handleExponentiationValue(crrInputNumber);
                break;
            }
        }
        crrOperator = null;
    }

    private void handleNumberEventClick(Button button) {
        button.setOnClickListener(v -> {
            String selectedNumberString = button.getText().toString();
            String valueString = getInputText();
            if (isHaveToClearInput || valueString.equals("0")) {
                isHaveToClearInput = false;
                outputLabel.setText(selectedNumberString);
            } else {
                outputLabel.setText(valueString + selectedNumberString);
            }
        });
    }

    private void handleOperatorEventClick(Button button) {
        button.setOnClickListener(v -> {
            if (crrOperator != null) {
                startCal();
            }
            String crrValString = getInputText();
            double crrVal;
            try {
                crrVal = Double.parseDouble(crrValString);
            } catch (NumberFormatException e) {
                return;
            }
            String selectedOperator = button.getText().toString();
            String newExpress = crrValString + selectedOperator;
            setValue(crrVal);
            setExpress(newExpress);
            crrOperator = selectedOperator;
            isHaveToClearInput = true;
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleDivValue(double crrInputNumber) {
        if (crrInputNumber == 0) {
            if (currentCalState.value == 0) {
                outputLabel.setText("Result is undefined");
            } else {
                outputLabel.setText("Cannot divide by zero");
            }
        } else {
            String express = currentCalState.getNormalizeValue() + "/"
                    + CalState.normalizeDoubleToString(crrInputNumber) + "=";

            setNewCalState(
                    express
                    ,
                    currentCalState.value / crrInputNumber
            );
        }
    }

    private void handleAddValue(double crrInputNumber) {
        String exp = currentCalState.getNormalizeValue() + "+"
                + CalState.normalizeDoubleToString(crrInputNumber) + "=";
        setNewCalState(
                exp,
                currentCalState.value + crrInputNumber
        );
    }

    private void handleMultipleValue(double crrInputNumber) {
        setNewCalState(
                currentCalState.getNormalizeValue() + "x"
                        + CalState.normalizeDoubleToString(crrInputNumber) + "=",
                currentCalState.value * crrInputNumber
        );
    }

    private void handleModuleValue(double crrInputNumber) {
        setNewCalState(
                currentCalState.value + "%"
                        + CalState.normalizeDoubleToString(crrInputNumber) + "=",
                currentCalState.value % crrInputNumber
        );
    }

    private void handleExponentiationValue(double crrInputNumber) {
        try {
            double newVal = Math.pow(currentCalState.value, crrInputNumber);
            setNewCalState(
                    currentCalState.value + "^" +
                            CalState.normalizeDoubleToString(crrInputNumber) + "=",
                    newVal
            );
        } catch (ArithmeticException e) {
            outputLabel.setText("Invalid input");
        }
    }

    private void handleMinusValue(double crrInputNumber) {
        setNewCalState(
                currentCalState.getNormalizeValue() + "-"
                        + CalState.normalizeDoubleToString(crrInputNumber) + "=",
                currentCalState.value - crrInputNumber
        );
    }

    private void handleOppositeValue() {
        String lastResultString = getInputText();
        double lastResult = Double.parseDouble(lastResultString);
        setNewCalState(
                "-(" + lastResultString + ")=",
                -lastResult
        );
    }

    private void handleSqrtValue() {
        String lastResultString = getInputText();
        double lastResult = Double.parseDouble(lastResultString);
        if (lastResult < 0) {
            outputLabel.setText("Invalid input");
        } else {
            setNewCalState(
                    "v(" + lastResultString + ")=",
                    Math.sqrt(lastResult)
            );
        }
    }

    private void handleReverseValue() {
        String lastResultString = getInputText();
        double lastResult = Double.parseDouble(lastResultString);
        setNewCalState(
                "1/(" + lastResultString + ")=",
                1 / lastResult
        );
    }

    private String getInputText() {
        return CalState.normalizeStringTypeDoubleToString(outputLabel.getText().toString());
    }

    private void setNewCalState(String express, double value) {
        currentCalState.express = express;
        currentCalState.value = value;
        if (!currentCalState.isWaitingForOperand()&&!currentCalState.isDefault()) {
            history.add(currentCalState.clone());
        }
        expressLabel.setText(express);
        outputLabel.setText(CalState.normalizeDoubleToString(value));
    }

    private void setValue(double value) {
        currentCalState.value = value;
        outputLabel.setText(CalState.normalizeDoubleToString(value));
    }

    private void setExpress(String express) {
        currentCalState.express = express;
        expressLabel.setText(express);
    }
}