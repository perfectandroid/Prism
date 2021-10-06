package com.perfect.prism.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.perfect.prism.Fragment.AgentDatewisereportFragment;
import com.perfect.prism.Fragment.AgentLastmonthreportFragment;
import com.perfect.prism.Fragment.AgentMonthreportFragment;
import com.perfect.prism.Fragment.ClientDatewisereportFragment;
import com.perfect.prism.Fragment.ClientLastmonthreportFragment;
import com.perfect.prism.Fragment.ClientMonthreportFragment;
import com.perfect.prism.R;

import java.util.ArrayList;
import java.util.List;

public class ClientwiseReportActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientwise_report);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ClientwiseReportActivity.ViewPagerAdapter adapter = new ClientwiseReportActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClientDatewisereportFragment(), "Date Sort");
        adapter.addFragment(new ClientMonthreportFragment(), "Month");
        adapter.addFragment(new ClientLastmonthreportFragment(), "Last Month");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item:
                Intent i = new Intent(ClientwiseReportActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
