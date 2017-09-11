package com.rizafu.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rizafu.coachmark.CoachMark;
import com.rizafu.sample.databinding.RecyclerLayoutBinding;

/**
 * Created by RizaFu on 2/27/17.
 */

public class SimpleFragment extends Fragment implements SimpleAdapter.OnItemClick {
    private RecyclerLayoutBinding binding;

    public static SimpleFragment newInstance() {

        Bundle args = new Bundle();

        SimpleFragment fragment = new SimpleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.recycler_layout,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SimpleAdapter adapter = new SimpleAdapter();
        adapter.addItem("Simple Coach Mark 1","no tooltip, click target for dismiss");
        adapter.addItem("Simple Coach Mark 2","no tooltip, dismissible");
        adapter.addItem("Simple Coach Mark 3","no tooltip, with target custom click listener");
        adapter.addItem("Coach Mark 1","simple tooltip message at alignment bottom");
        adapter.addItem("Coach Mark 2","simple tooltip message at alignment top");
        adapter.addItem("Coach Mark 3","simple tooltip pointer at alignment left");
        adapter.addItem("Coach Mark 4","simple tooltip pointer at alignment right");
        adapter.addItem("Coach Mark 5","simple tooltip no pointer");

        adapter.addItem("Simple Coach Mark 1","no tooltip, click target for dismiss");
        adapter.addItem("Simple Coach Mark 2","no tooltip, dismissible");
        adapter.addItem("Simple Coach Mark 3","no tooltip, with target custom click listener");
        adapter.addItem("Coach Mark 1","simple tooltip message at alignment bottom");
        adapter.addItem("Coach Mark 2","simple tooltip message at alignment top");
        adapter.addItem("Coach Mark 3","simple tooltip pointer at alignment left");
        adapter.addItem("Coach Mark 4","simple tooltip pointer at alignment right");
        adapter.addItem("Coach Mark 5","simple tooltip no pointer");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClick(this);
    }

    @Override
    public void onClick(final View view, int position) {
        switch (position) {
            case 0:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .show();
                break;
            case 1:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .setDismissible()
                        .show();
                break;
            case 2:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .setOnClickTarget(new CoachMark.Builder.OnClick() {
                            @Override
                            public void onClick(CoachMark coachMark) {
                                coachMark.destroy();
                                Snackbar.make(view.getRootView(),"Action click on target mark", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        })
                        .show();
                break;
            case 3:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .addTooltipChildText(getContext(),"this is message tooltip",android.R.color.primary_text_dark)
                        .setTooltipAlignment(CoachMark.TARGET_BOTTOM)
                        .setTooltipBackgroundColor(R.color.colorPrimary)
                        .show();
                break;
            case 4:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .addTooltipChildText(getContext(),"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show();
                break;
            case 5:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .addTooltipChildText(getContext(),"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP_LEFT)
                        .setTooltipPointer(CoachMark.POINTER_LEFT)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show();
                break;
            case 6:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .addTooltipChildText(getContext(),"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP_RIGHT)
                        .setTooltipPointer(CoachMark.POINTER_RIGHT)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show();
                break;
            case 7:
                new CoachMark.Builder(getActivity())
                        .setTarget(view)
                        .addTooltipChildText(getContext(),"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipPointer(CoachMark.POINTER_GONE)
                        .show();
                break;
        }
    }
}
