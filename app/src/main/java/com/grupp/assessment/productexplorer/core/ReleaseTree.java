package com.grupp.assessment.productexplorer.core;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * A Timber tree that sends only Log.ERROR logs using the ACRA errorReporter
 */
public class ReleaseTree extends Timber.Tree {

    @Override
    protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {
        if(message.isBlank() && t == null) return;
    }

    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        return priority == Log.ERROR;
    }
}