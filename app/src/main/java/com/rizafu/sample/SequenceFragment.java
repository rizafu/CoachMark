package com.rizafu.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by RizaFu on 2/27/17.
 */

public class SequenceFragment extends Fragment {

    public static SequenceFragment newInstance() {

        Bundle args = new Bundle();

        SequenceFragment fragment = new SequenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
