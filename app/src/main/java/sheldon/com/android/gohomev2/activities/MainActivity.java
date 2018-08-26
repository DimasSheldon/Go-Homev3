package sheldon.com.android.gohomev2.activities;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sheldon.com.android.gohomev2.R;
import sheldon.com.android.gohomev2.asynctask.LoopJ;
import sheldon.com.android.gohomev2.asynctask.LoopJListener;
import sheldon.com.android.gohomev2.fragments.ControlFragment;
import sheldon.com.android.gohomev2.fragments.MonitorFragment;
import sheldon.com.android.gohomev2.helper.BottomNavigationBehavior;

import static sheldon.com.android.gohomev2.activities.LoginActivity.PREFS_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoopJListener {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;
    private NavigationView navigationView;
    private MenuItem prevMenuItem;
    private LoopJ client;
    private Handler mHandler;
    private SharedPreferences sharedPref;
    private String username, fullname, email, role, token;
    private TextView mFullName, mEmail, mArea, mLastUpdate, mLiveTime;
    private static int navItemIndex;
    public static int countUpdateDO, countUpdateDI, countUpdateAI;
    public static String starText;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        sharedPref = getSharedPreferences(PREFS_NAME, 0);
        username = sharedPref.getString(getString(R.string.saved_user_name), "");
        fullname = sharedPref.getString(getString(R.string.saved_full_name), "");
        email = sharedPref.getString(getString(R.string.saved_email), "");
        role = sharedPref.getString(getString(R.string.saved_role), "");
        token = sharedPref.getString(getString(R.string.saved_token), "");

        client = new LoopJ(this);
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                navItemIndex = position;
                selectNavMenu();
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) prevMenuItem.setChecked(false);
                else bottomNavigation.getMenu().getItem(0).setChecked(false);

                bottomNavigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);

        mFullName = (TextView) navHeaderView.findViewById(R.id.nav_full_name);
        mEmail = (TextView) navHeaderView.findViewById(R.id.nav_email);
        mArea = (TextView) findViewById(R.id.tv_area);
        mLastUpdate = (TextView) findViewById(R.id.tv_last_update_value);
        mLiveTime = (TextView) findViewById(R.id.tv_live_time_value);

        mFullName.setText(username);
        mEmail.setText(email);

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
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
            Toast.makeText(this, R.string.snackbar_settings, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();

                sharedPref.edit().putBoolean(getString(R.string.saved_log_stat), false).apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

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
    protected void onStart() {
        super.onStart();
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
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else moveTaskToBack(true);
    }

    public static Context getActivityContext() {
        return context;
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
                    navItemIndex = 0;
                    selectNavMenu();
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    navItemIndex = 1;
                    selectNavMenu();
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    private void updateFragment() {
        starText = starText + "*";

        if (starText.length() >= 6) starText = "*";

        client.synchronize(token, username);
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
                    if (key.contains("AI")) { // set data for Analog Input
                        currData = new JSONObject(response.get(key).toString());

                        if (currData.getString("status").equals("ACTIVE")) {
                            countUpdateAI++;
                            MonitorFragment.updateDataAI(currData);
                        }
                    } else if (key.contains("DI")) { // set data for Digital Input
                        currData = new JSONObject(response.get(key).toString());

                        if (currData.getString("status").equals("ACTIVE")) {
                            countUpdateDI++;
                            MonitorFragment.updateDataDI(currData);
                        }
                    } else if (key.contains("DO")) { // set data for Digital Output
                        currData = new JSONObject(response.get(key).toString());

                        if (currData.getString("status").equals("ACTIVE")) {
                            countUpdateDO++;
                            ControlFragment.updateDataDO(currData);
                        }
                    } else { // not widgets' data
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
            } else Log.d("MAIN_ACTIVITY", "run: syncing...");
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }
}