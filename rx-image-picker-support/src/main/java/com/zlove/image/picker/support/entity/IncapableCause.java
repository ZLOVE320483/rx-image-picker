package com.zlove.image.picker.support.entity;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.zlove.image.picker.support.ui.widget.IncapableDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class IncapableCause {

    public static final int TOAST = 0x00;
    public static final int DIALOG = 0x01;
    public static final int NONE = 0x02;

    private int mForm = TOAST;
    private String mTitle;
    private String mMessage;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {TOAST, DIALOG, NONE})
    @interface Form {
    }

    public IncapableCause(String message) {
        mMessage = message;
    }

    public IncapableCause(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public IncapableCause(@Form int form, String message) {
        mForm = form;
        mMessage = message;
    }

    public IncapableCause(@Form int form, String title, String message) {
        mForm = form;
        mTitle = title;
        mMessage = message;
    }

    public static void handleCause(Context context, IncapableCause cause) {
        if (cause == null) {
            return;
        }

        switch (cause.mForm) {
            case NONE:
                break;
            case DIALOG:
                IncapableDialog incapableDialog = IncapableDialog.newInstance(cause.mTitle, cause.mMessage);
                incapableDialog.show(((FragmentActivity) context).getSupportFragmentManager(), IncapableDialog.class.getName());
                break;
            case TOAST:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
