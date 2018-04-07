package com.micronet.mcontroltestapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.micronet.mcontroltestapp.R;
import com.micronet.mcontroltestapp.BuildConfig;

/**
 * Created by brigham.diaz on 10/26/2016.
 */

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_section_about, container, false);
        TextView txtAbout = (TextView)rootView.findViewById(R.id.txtAbout);
        txtAbout.setText(String.format("MCTL Demo App v %s\nCopyright © 2017 Micronet Inc.\n", BuildConfig.VERSION_NAME));
        rootView.findViewById(R.id.update_settings).setOnClickListener(new View.OnClickListener() {
            private int refreshRate() {
                return Integer.parseInt(((EditText)rootView.findViewById(R.id.refresh_rate)).getText().toString());
            }

            @Override
            public void onClick(View view) {
                MControlFragment.LOG_INTERVAL_MS = refreshRate();
            }
        });
        return rootView;
    }
}
