package sheldon.com.android.gohomev2.activities;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sheldon.com.android.gohomev2.R;
import sheldon.com.android.gohomev2.asynctask.LoopJ;
import sheldon.com.android.gohomev2.asynctask.LoopJListener;
import sheldon.com.android.gohomev2.fragments.ControlFragment;
import sheldon.com.android.gohomev2.fragments.MonitorFragment;
import sheldon.com.android.gohomev2.helper.BottomNavigationViewBehavior;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoopJListener {
    private Toolbar toolbar;
    private MenuItem prevMenuItem;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;
    private LoopJ client;
    private Handler mHandler;
    private TextView fullName, email, mArea, mLastUpdate, mLiveTime;
    public static int countUpdateDO = 0;
    public static int countUpdateDI = 0;
    public static int countUpdateAI = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new LoopJ(this);
        mHandler = new Handler();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigation.getMenu().getItem(0).setChecked(false);
                }

                bottomNavigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);

        fullName = (TextView) navHeaderView.findViewById(R.id.nav_full_name);
        email = (TextView) navHeaderView.findViewById(R.id.nav_email);
        mArea = (TextView) findViewById(R.id.tv_area);
        mLastUpdate = (TextView) findViewById(R.id.tv_last_update_value);
        mLiveTime = (TextView) findViewById(R.id.tv_live_time_value);

        fullName.setText(LoopJ.fullName);
        email.setText(LoopJ.email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_monitoring:
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_control:
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void authenticate(String authStatus) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunnable.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MonitorFragment());
        adapter.addFragment(new ControlFragment());
        viewPager.setAdapter(adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    private void updateFragment() {

        client.synchronize(LoopJ.token, LoopJ.uname);
        JSONObject response = LoopJ.syncResponse;

        if (response != null) {

            try {
                mArea.setText(response.getString("area"));
                mLiveTime.setText(response.getString("livetime"));
                mLastUpdate.setText(response.getString("lastupdate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject currData;
            Iterator<?> keys = response.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    // set data for Analog Input and Digital Input
                    if (key.contains("AI")) {
                        currData = new JSONObject(response.get(key).toString());

                        if (currData.getString("status").equals("ACTIVE")) {
                            countUpdateAI++;
                            MonitorFragment.updateDataAI(currData);
                        }
                    } else if (key.contains("DI")) {
                        currData = new JSONObject(response.get(key).toString());

                        if (currData.getString("status").equals("ACTIVE")) {
                            countUpdateDI++;
                            MonitorFragment.updateDataDI(currData);
                        }
                    } else if (key.contains("DO")) {
                        currData = new JSONObject(response.get(key).toString());

                        if (currData.getString("status").equals("ACTIVE")) {
                            countUpdateDO++;
                            ControlFragment.updateDataDO(currData);
                        }
                    } else {
                        Log.d("DATA", "updateFragment: Not Widget Attributes");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            MonitorFragment.removeUnusedAIWidgets();
            MonitorFragment.removeUnusedDIWidgets();
            ControlFragment.removeUnusedD0Widgets();
            MonitorFragment.resetPosition();
            ControlFragment.resetPosition();
            countUpdateAI = 0;
            countUpdateDI = 0;
            countUpdateDO = 0;
        }
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (!LoopJ.isBusy) {
                Log.d("MAIN_ACTIVITY", "run: synced");
                updateFragment();
            } else Log.d("MAIN_ACTIVITY", "run: syncing");
            mHandler.postDelayed(mRunnable, 1000);
        }
    };


}