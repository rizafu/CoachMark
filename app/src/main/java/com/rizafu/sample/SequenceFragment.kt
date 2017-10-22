package com.rizafu.sample

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Created by RizaFu on 2/27/17.
 */

class SequenceFragment : Fragment() {
    companion object {

        fun newInstance(): SequenceFragment {

            val args = Bundle()

            val fragment = SequenceFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
