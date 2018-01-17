package com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.aspanta.emcsec.R;


public class DialogFragmentPleaseWaitWithProgress extends DialogFragment {

    private TextView mTvDownloadProgress;
    private TextView mTvTotalProgress;

    public static DialogFragmentPleaseWaitWithProgress newInstance() {
        return new DialogFragmentPleaseWaitWithProgress();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_please_wait_with_progress, container, false);
        setCancelable(false);

        init(view);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        return view;
    }

    public void init(View v) {
        mTvDownloadProgress = v.findViewById(R.id.tv_download_progress);
        mTvTotalProgress = v.findViewById(R.id.tv_total_progress);
    }

    public void setDownloadProgress(int count) {
        if (mTvDownloadProgress != null) {
            mTvDownloadProgress.setText(String.valueOf(count));
        }
    }

    public void setTotalProgress(int count) {
        if (mTvTotalProgress != null) {
            mTvTotalProgress.setText(String.valueOf(count));
        }
    }
}
