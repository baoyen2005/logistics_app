package com.example.baseapp.captures;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.baseapp.utils.UtilsBase;

import java.util.regex.Pattern;

public class HipHopCapture implements TextWatcher {
    String beforeText;
    private EditText mEditext;
    private Pattern mPattern;
    private int caretPoint;
    private int maxLength;
    public static final int NO_PATTERN = 0;
    public static final int TEXT_SPACE = 1;
    public static final int NUMBER = 2;
    public static final int TEXT_ONLY = 3;
    public static final int MONEY_PATTERN = 4;
    public static final int NONE_SPACE = 5;
    public static final int NON_NUMBER = 6;
    public static final int NO_VIETNAMESE = 7;
    public static final int ADDRESS_PATTERN = 8;
    public static final int NON_NUMBER_SPACE = 9;
    public static final int NO_VIETNAMESE_SPACE = 10;
    public static final int CONTENT_PATTERN = 11;
    public static final int CONTENT_HB_PATTERN = 12;
    private boolean isNoEnter;
    private EditText nextFocus;
    private InputMethodManager imm;

    public HipHopCapture(EditText mEditext, int maxLength) {
        this.mEditext = mEditext;
        this.maxLength = maxLength;
        this.mPattern = Pattern.compile("^[a-zA-Z0-9\\s\\.,_@+\\-]+$");
    }

    public HipHopCapture(EditText mEditext, int maxLength, int type) {
        this.mEditext = mEditext;
        this.maxLength = maxLength;
        if (type == TEXT_SPACE) {
            this.mPattern = Pattern.compile("^[a-zA-Z0-9\\s]+$");
        } else if (type == NUMBER) {
            this.mPattern = Pattern.compile("^[0-9]+$");
        } else if (type == TEXT_ONLY) {
            this.mPattern = Pattern.compile("^[0-9a-zA-Z]+$");
        } else if (type == MONEY_PATTERN) {
            this.mPattern = Pattern.compile("^[0-9/-\\\\.]+$");
        } else if (type == NONE_SPACE) {
            this.mPattern = Pattern.compile("^[A-Za-z0-9!@#$%^&*().,<>?\\-=+/;:'\"|`~_\\[\\]{}]+$");
        } else if (type == NON_NUMBER) {
            this.mPattern = Pattern.compile("^[\\w\\s]+$");
        } else if (type == NO_VIETNAMESE) {
            this.mPattern = Pattern.compile("^[A-Za-z0-9!@#$%^&*().,<>?\\-=+/;:'\"|`~_\\s]+$");
        } else if (type == NON_NUMBER_SPACE) {
            this.mPattern = Pattern.compile("^[A-Za-z ]+$");
        } else if (type == ADDRESS_PATTERN) {
            this.mPattern = Pattern.compile("^[A-Za-z0-9.,\\-/\\s_]+$");
        } else if (type == NO_VIETNAMESE_SPACE) {
            this.mPattern = Pattern.compile("^[A-Za-z0-9!@#$%^&*().,<>?\\-=+/;:'\"|`~_]+$");
        } else if (type == CONTENT_PATTERN) {
            this.mPattern = Pattern.compile("^[A-Za-z0-9\\-\\s.,-/]+$");
        } else if (type == CONTENT_HB_PATTERN) {
            this.mPattern = Pattern.compile("^[^|]+$");
        }
    }

    public void afterTextChanged(Editable s) {

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        try {
            this.beforeText = s.toString();
            if (mEditext != null)
                caretPoint = mEditext.getSelectionEnd();
        } catch (Exception e) {
            Log.wtf("EXC", e);
        }
    }

    public HipHopCapture setNoEnter(EditText nextFocus) {
        isNoEnter = true;
        this.nextFocus = nextFocus;
        return this;
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String keepText = null;
        int lth = s.length();
        int bth = this.beforeText.length();
        if (lth < bth) {
            return;
        }
        if (lth > 0) {
            keepText = s.toString();
            if (isNoEnter && keepText.endsWith("\n")) {
                if (this.nextFocus != null) {
                    this.nextFocus.requestFocus();
                } else {
                    if (imm == null)
                        imm = (InputMethodManager) mEditext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditext.getWindowToken(), 0);
                    caretPoint = this.beforeText.length();
                }
                onChangeText(this.beforeText);
            } else if ((maxLength > 0 && keepText.length() > maxLength) || keepText.contains("\n")) {
                caretPoint = this.beforeText.length();
                onChangeText(this.beforeText);
            } else if (lth - bth > 1) {//Copy & paste
                //multi words
                String tx = UtilsBase.g().removeAccent(keepText);
                if (mPattern != null) {
                    String pattern = mPattern.pattern().replace("^[", "[^").replace("]+$", "]");
                    tx = tx.replaceAll(pattern, "");
                    caretPoint += tx.length() - bth;
                    if (!checkChar(tx)) {
                        tx = this.beforeText;
                        caretPoint = tx.length();
                    }
                    onChangeText(tx);
                }
            } else if (!checkChar(keepText)) {                //single word
                String tx = "";
                String newText = UtilsBase.g().removeAccentHipHop(keepText);
                if (newText.startsWith(" "))
                    //nochange
                    tx = this.beforeText;
                else {
                    caretPoint += newText.length();
                    tx = this.beforeText + newText;
                }
                if (!checkChar(tx)) {
                    tx = this.beforeText;
                    caretPoint = tx.length();
                }
                onChangeText(tx);
            }
        }
    }

    private void onChangeText(String text) {
        try {
            this.mEditext.removeTextChangedListener(this);
            if (!checkChar(text))
                this.mEditext.setText(beforeText);
            else
                this.mEditext.setText(text);
            if (caretPoint <= 0)
                caretPoint = 1;
            if (caretPoint > text.length())
                caretPoint = text.length();
            this.mEditext.addTextChangedListener(this);
            this.mEditext.setSelection(caretPoint);

        } catch (Exception e) {
            Log.wtf("EXC", e);
        }
    }

    public boolean checkChar(String pass) {
        try {
            if (pass == null || pass.isEmpty()) {
                return true;
            }
            if (mPattern != null)
                return this.mPattern.matcher(pass).find();
        } catch (Exception e) {
            ////
        }
        return true;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
