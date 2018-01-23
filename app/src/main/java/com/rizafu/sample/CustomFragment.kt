package com.rizafu.sample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rizafu.coachmark.CoachMark
import com.rizafu.sample.databinding.RecyclerLayoutBinding

/**
 * Created by RizaFu on 2/27/17.
 */

class CustomFragment : Fragment() {
    private lateinit var binding: RecyclerLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.recycler_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CustomAdapter()
        adapter.addItem("Simple Coach Mark 1", "no tooltip, click target for dismiss")
        adapter.addItem("Simple Coach Mark 2", "no tooltip, dismissible")
        adapter.addItem("Simple Coach Mark 3", "no tooltip, with target custom click listener")
        adapter.addItem("Coach Mark 1", "simple tooltip message at alignment bottom")
        adapter.addItem("Coach Mark 2", "simple tooltip message at alignment top")
        adapter.addItem("Coach Mark 3", "simple tooltip pointer at alignment left")
        adapter.addItem("Coach Mark 4", "simple tooltip pointer at alignment right")
        adapter.addItem("Coach Mark 5", "simple tooltip no pointer")

        val gridLayoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        adapter.setOnItemClick({ v: View, p: Int -> onClick(v,p) })
    }

    private fun onClick(view: View, position: Int) {
        activity?.let {
            when (position) {
                0 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .show()
                1 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .setDismissible()
                        .show()
                2 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .setOnClickTarget { coachMark ->
                            coachMark.dismiss()
                            Snackbar.make(view.rootView, "Action click on target mark", BaseTransientBottomBar.LENGTH_LONG).show()
                        }
                        .show()
                3 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .addTooltipChildText(it, "this is message tooltip", android.R.color.primary_text_dark)
                        .setTooltipAlignment(CoachMark.TARGET_BOTTOM)
                        .setTooltipBackgroundColor(R.color.colorPrimary)
                        .show()
                4 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .addTooltipChildText(it, "this is message tooltip", android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show()
                5 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .addTooltipChildText(it, "this is message tooltip", android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipPointer(CoachMark.POINTER_LEFT)
                        .setTooltipMatchWidth()
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show()
                6 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .addTooltipChildText(it, "this is message tooltip", android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP_RIGHT)
                        .setTooltipPointer(CoachMark.POINTER_RIGHT)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show()
                7 -> CoachMark.Builder(it)
                        .setTarget(view)
                        .addTooltipChildText(it, "this is message tooltip", android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipPointer(CoachMark.POINTER_GONE)
                        .setBackgroundColor(R.color.colorPrimaryDark)
                        .show()
                else -> { }
            }
        }
    }

    companion object {

        fun newInstance(): CustomFragment {

            val args = Bundle()

            val fragment = CustomFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
