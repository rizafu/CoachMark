package com.rizafu.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rizafu.coachmark.CoachMark;
import com.rizafu.sample.databinding.AboutTooltipBinding;
import com.rizafu.sample.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements MainAdapter.OnItemClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MainAdapter adapter = new MainAdapter();
        adapter.addItem("Simple Coach Mark 1","no tooltip, click target for dismiss");
        adapter.addItem("Simple Coach Mark 2","no tooltip, dismissible");
        adapter.addItem("Simple Coach Mark 3","no tooltip, with target custom click listener");
        adapter.addItem("Coach Mark 1","simple tooltip message at alignment bottom");
        adapter.addItem("Coach Mark 2","simple tooltip message at alignment top");
        adapter.addItem("Coach Mark 3","simple tooltip pointer at alignment left");
        adapter.addItem("Coach Mark 4","simple tooltip pointer at alignment right");
        adapter.addItem("Coach Mark 5","simple tooltip no pointer");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClick(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                AboutTooltipBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.about_tooltip,null,false);
                binding.text.setText("Coach Mark v" + BuildConfig.VERSION_NAME+"\nGithub : goo.gl/t2d2JG");
                new CoachMark.Builder(this)
                        .setTarget(R.id.menu1)
                        .addTooltipChild(binding.getRoot())
                        .setTooltipAlignment(CoachMark.ROOT_CENTER)
                        .setTooltipBackgroundColor(android.R.color.transparent)
                        .setBackgroundAlpha(0.5f)
                        .setDismissible()
                        .setTooltipPointer(CoachMark.POINTER_GONE)
                .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view, int position) {
        switch (position) {
            case 0:
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .show();
                break;
            case 1:
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .setDismissible()
                        .show();
                break;
            case 2:
                new CoachMark.Builder(this)
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
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .addTooltipChildText(this,"this is message tooltip",android.R.color.primary_text_dark)
                        .setTooltipAlignment(CoachMark.TARGET_BOTTOM)
                        .setTooltipBackgroundColor(R.color.colorPrimary)
                        .show();
                break;
            case 4:
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .addTooltipChildText(this,"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show();
                break;
            case 5:
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .addTooltipChildText(this,"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipPointer(CoachMark.POINTER_LEFT)
                        .setTooltipMatchWidth()
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show();
                break;
            case 6:
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .addTooltipChildText(this,"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP_RIGHT)
                        .setTooltipPointer(CoachMark.POINTER_RIGHT)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .show();
                break;
            case 7:
                new CoachMark.Builder(this)
                        .setTarget(view)
                        .addTooltipChildText(this,"this is message tooltip",android.R.color.primary_text_light)
                        .setTooltipAlignment(CoachMark.TARGET_TOP)
                        .setTooltipPointer(CoachMark.POINTER_GONE)
                        .setTooltipBackgroundColor(R.color.colorAccent)
                        .setBackgroundColor(R.color.colorPrimaryDark)
                        .show();
                break;
        }
    }
}
