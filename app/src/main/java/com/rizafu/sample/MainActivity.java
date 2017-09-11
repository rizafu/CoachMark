package com.rizafu.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.rizafu.coachmark.CoachMark;
import com.rizafu.sample.databinding.AboutTooltipBinding;
import com.rizafu.sample.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,SimpleFragment.newInstance(),"Simple")
                .commit();
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
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
                        .setTarget(R.id.menu_custom)
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

    private void wellcomeTutorial(){

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_simple:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,SimpleFragment.newInstance(),"Simple")
                        .disallowAddToBackStack()
                        .commit();
                break;
            case R.id.menu_custom:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,CustomFragment.newInstance(),"Custom")
                        .disallowAddToBackStack()
                        .commit();
                break;
            case R.id.menu_sequence:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, SequenceFragment.newInstance(),"Sequence")
                        .disallowAddToBackStack()
                        .commit();
                break;
        }
        return true;
    }
}
