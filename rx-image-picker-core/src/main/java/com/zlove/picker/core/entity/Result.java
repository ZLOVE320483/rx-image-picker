package com.zlove.picker.core.entity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

public class Result {

    public final Uri uri;
    public final Bundle extraData;

    private Result(Builder builder) {
        this.uri = builder.uri;
        this.extraData = builder.extraData;
    }

    public static class Builder {

        final Bundle extraData = new Bundle();
        final Uri uri;

        public Builder(Uri uri) {
            this.uri = uri;
        }

        public Builder putIntExtra(String key, int value) {
            this.extraData.putInt(key, value);
            return this;
        }

        public Builder putBooleanExtra(String key, boolean value) {
            this.extraData.putBoolean(key, value);
            return this;
        }

        public Builder putLongExtra(String key, long value) {
            this.extraData.putLong(key, value);
            return this;
        }

        public Builder putDoubleExtra(String key, double value) {
            this.extraData.putDouble(key, value);
            return this;
        }

        public Builder putFloatExtra(String key, float value) {
            this.extraData.putFloat(key, value);
            return this;
        }

        public Builder putStringExtra(String key, String value) {
            this.extraData.putString(key, value);
            return this;
        }

        public Builder putSerializableExtra(String key, Serializable value) {
            this.extraData.putSerializable(key, value);
            return this;
        }

        public Builder putParcelableExtra(String key, Parcelable value) {
            this.extraData.putParcelable(key, value);
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }
}
