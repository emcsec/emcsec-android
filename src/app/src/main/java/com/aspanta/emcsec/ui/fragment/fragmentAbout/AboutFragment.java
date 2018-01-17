package com.aspanta.emcsec.ui.fragment.fragmentAbout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;

public class AboutFragment extends Fragment implements IBaseFragment {

    private TextView mTvCopyrightLink;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToggle().getDrawerArrowDrawable().setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        mTvCopyrightLink = view.findViewById(R.id.tv_copyright_aspanta_ltd);
        mTvCopyrightLink.setOnClickListener(c -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aspanta.com"));
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No application can handle this request."
                        + " Please install a WebBrowser", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
        return view;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).getToggle().getDrawerArrowDrawable().setColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    @Override
    public String getCurrentTag() {
        return getClass().getName();
    }
}
