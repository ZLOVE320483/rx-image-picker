package com.zlove.image.picker.support.filter;

import android.content.Context;

import com.zlove.image.picker.support.MimeType;
import com.zlove.image.picker.support.entity.IncapableCause;
import com.zlove.image.picker.support.entity.Item;

import java.util.Set;

public abstract class Filter {

    protected abstract Set<MimeType> constraintTypes();

    public abstract IncapableCause filter(Context context, Item item);
}
