package io.github.rizafu.sample;

import android.animation.Animator;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import io.github.rizafu.coachmark.CoachMark;
import io.github.rizafu.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                showCircularAnim();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showCircularAnim(){
        // previously invisible view
        final View myView = binding.view;

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        anim.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_linear_in));
        anim.setDuration(400);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
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
                showCoachMarck();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCoachMarck(){
        new CoachMark.Builder(this)
                .setTarget(binding.cardView)
                .setOnClickTarget(new CoachMark.Builder.OnClick() {
                    @Override
                    public void onClick(CoachMark coachMark) {
                        coachMark.destroy();
                        Toast.makeText(MainActivity.this, "test click target", Toast.LENGTH_SHORT).show();
                    }
                })
                .setTooltipBackgroundColor(R.color.colorAccent)
                .setTextColor(android.R.color.white)
                .setTitle("title")
                .setAction("ok", new CoachMark.Builder.OnClick() {
                    @Override
                    public void onClick(CoachMark coachMark) {
                        coachMark.destroy();
                    }
                })
                .setDescription("description of hello world")
                .setTooltipAlignment(CoachMark.TARGET_BOTTOM)
                .show();
    }
}
