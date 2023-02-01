package com.aspanta.emcsec.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.BuildConfig;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring.GenerateRestoreSeedActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentAreYouSureWantToClose.DialogFragmentAreYouSureWantToClose;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.EnterAnAddressBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment.EnterAnAddressEmercoinFragment;
import com.aspanta.emcsec.ui.fragment.fragmentAbout.AboutFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.HistoryFragment;
import com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin.DialogSetUpFingerprint;
import com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin.EnterPin;
import com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin.SetUpPin;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.ReceiveCoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import static com.aspanta.emcsec.R.id.action_get_dashboard;
import static com.aspanta.emcsec.R.id.action_history_dashboard;
import static com.aspanta.emcsec.R.id.action_home_dashboard;
import static com.aspanta.emcsec.R.id.action_send;
import static com.aspanta.emcsec.tools.Config.DISABLE;
import static com.aspanta.emcsec.tools.Config.EMERCOIN;
import static com.aspanta.emcsec.tools.Config.ENABLE;
import static com.aspanta.emcsec.tools.Config.EXTRAS_ADDRESS;
import static com.aspanta.emcsec.tools.Config.EXTRAS_AMOUNT;
import static com.aspanta.emcsec.tools.Config.EXTRAS_TYPE;
import static com.aspanta.emcsec.tools.Config.FROM_DASHBOARD;
import static com.aspanta.emcsec.tools.Config.FROM_SETTINGS;
import static com.aspanta.emcsec.tools.Config.backFromQrOrSharing;
import static com.jakewharton.rxbinding2.view.RxView.clicks;

public class MainActivity extends AppCompatActivity implements SetUpPin.SetPinCallback, EnterPin.EnterPinCallback {

    private final String TAG = this.getClass().getSimpleName();
    FragmentManager fragmentManager = getSupportFragmentManager();
    private NavigationView navigationView;
    private BottomNavigationViewEx bottomNavigationView;
    private RelativeLayout mRlLogout, mRlDashboard, mRlSendCoins, mRlReceiveCoins, mRlTransactionHistory,
            mRlAbout, mRlSettings;
    private TextView mTvVersion;
    DrawerLayout drawer;
    public static boolean isDataDownloaded = false;
    private ActionBarDrawerToggle toggle;
    private static boolean isActivityFinished;
    private SPHelper mSPHelper = SPHelper.getInstance();

    public static void showAlertDialog(Context context, String error) {

        if (!isActivityFinished) {
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.error))
                    .setMessage(error)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.ok), (DialogInterface dialog, int which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityFinished = false;
        if (SPHelper.getInstance().getEnablePin() == ENABLE) {
            if (!backFromQrOrSharing) {
                EnterPin.newInstance(FROM_DASHBOARD).show(getSupportFragmentManager(), "");
            } else {
                backFromQrOrSharing = false;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityFinished = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBottomNavigationView();
        setDrawer(toolbar);

        init();
        setOnClickListeners();

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        DashboardFragment dashboardFragment;
        if (from.equals("seed")) {
            dashboardFragment = DashboardFragment.newInstance("seed");
        } else {
            dashboardFragment = DashboardFragment.newInstance("");
        }
        navigator(dashboardFragment, dashboardFragment.getCurrentTag());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            if (resultCode == RESULT_OK) {
                Fragment fragment;
                String address = data.getStringExtra(EXTRAS_ADDRESS);
                String amount = data.getStringExtra(EXTRAS_AMOUNT);

                if (data.getStringExtra(EXTRAS_TYPE).equals(EMERCOIN)) {
                    fragment = EnterAnAddressEmercoinFragment.newInstance(address, amount, "");
                } else {
                    fragment = EnterAnAddressBitcoinFragment.newInstance(address, amount, "");
                }
                navigator(fragment, ((IBaseFragment) fragment).getCurrentTag());
            }
        }
    }

    private void setBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bnve);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setIconSize(32, 32);
        bottomNavigationView.setIconSizeAt(0, 24, 24);
        bottomNavigationView.setIconSizeAt(3, 24, 24);
        bottomNavigationView.setSmallTextSize(12);
        bottomNavigationView.setIconMarginTop(0, 20);
        bottomNavigationView.setIconMarginTop(1, 10);
        bottomNavigationView.setIconMarginTop(2, 10);
        bottomNavigationView.setIconMarginTop(3, 24);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case action_home_dashboard:
                    navigator(DashboardFragment.newInstance(""),
                            DashboardFragment.newInstance("").getCurrentTag());
                    break;
                case action_send:
                    navigator(SendCoinFragment.newInstance(0),
                            SendCoinFragment.newInstance(0).getCurrentTag());
                    break;
                case action_get_dashboard:
                    navigator(ReceiveCoinFragment.newInstance(0),
                            ReceiveCoinFragment.newInstance(0).getCurrentTag());
                    break;
                case action_history_dashboard:
                    navigator(HistoryFragment.newInstance(),
                            HistoryFragment.newInstance().getCurrentTag());
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void setDrawer(Toolbar toolbar) {
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    @SuppressLint("CheckResult")
    private void setOnClickListeners() {

        clicks(mRlDashboard)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    bottomNavigationView.setCurrentItem(0);
                });

        clicks(mRlSendCoins)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    bottomNavigationView.setCurrentItem(1);
                });

        clicks(mRlReceiveCoins)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    bottomNavigationView.setCurrentItem(2);
                });

        clicks(mRlTransactionHistory)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    bottomNavigationView.setCurrentItem(3);
                });

        clicks(mRlAbout)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    AboutFragment fragment = AboutFragment.newInstance();
                    navigator(fragment, fragment.getCurrentTag());
                });

        clicks(mRlSettings)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    SettingsFragment fragment = SettingsFragment.newInstance();
                    navigator(fragment, fragment.getCurrentTag());
                });

        clicks(mRlLogout)
                .subscribe(s -> {
                    onCloseNavigationDrawer();
                    DialogFragmentAreYouSureWantToClose.newInstance().show(getSupportFragmentManager(), "");
                });
    }

    public void setCurrentItemNavigationView(int itemPosition) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setCurrentItem(itemPosition);
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        IBaseFragment iBaseFragment = null;
        for (Fragment f : fragmentManager.getFragments()) {
            if (f instanceof IBaseFragment) {
                iBaseFragment = (IBaseFragment) f;
                break;
            }
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (iBaseFragment != null) {
            iBaseFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void loqOut() {
        clearData();
        startActivity(new Intent(this, GenerateRestoreSeedActivity.class));
        finish();
    }

    void init() {
        mRlLogout = findViewById(R.id.rl_logout);
        mRlDashboard = findViewById(R.id.rl_dashboard);
        mRlSendCoins = findViewById(R.id.rl_send_coins);
        mRlReceiveCoins = findViewById(R.id.rl_receive_coins);
        mRlTransactionHistory = findViewById(R.id.rl_transaction_history);
        mRlSettings = findViewById(R.id.rl_settings);
        navigationView = findViewById(R.id.nav_view);
        mTvVersion = navigationView.getHeaderView(0).findViewById(R.id.tv_version);

        mRlAbout = findViewById(R.id.rl_about);

        String version = getResources().getString(R.string.version) + String.valueOf(BuildConfig.VERSION_NAME);
        mTvVersion.setText(version);
    }

    public void navigator(Fragment fragment, String TAG) {

        Fragment f = getSupportFragmentManager().findFragmentByTag(TAG);

        if (!(f != null && f.isVisible())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_animation, R.anim.exit_animation,
                    R.anim.pop_enter_animation, R.anim.pop_exit_animation);
            fragmentTransaction
                    .add(fragment, ((IBaseFragment) fragment).getCurrentTag())
                    .replace(R.id.container_content_main, fragment)
                    .commitAllowingStateLoss();
        }
    }

    public void navigatorBackPressed(Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.pop_enter_animation, R.anim.pop_exit_animation,
                R.anim.enter_animation, R.anim.exit_animation);
        fragmentTransaction
                .add(fragment, ((IBaseFragment) fragment).getCurrentTag())
                .replace(R.id.container_content_main, fragment)
                .commit();
    }

    public void onCloseNavigationDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void clearData() {
        SPHelper.getInstance().clear();
        isDataDownloaded = false;
    }

    @Override
    public void onPinEntered(int from) {
        if (from == FROM_SETTINGS) {
            SettingsFragment sf = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getName());
            if (mSPHelper.getEnablePin() == ENABLE) {
                mSPHelper.enablePin(DISABLE);
                sf.getSwitchFingerprint().setEnabled(false);
            } else {
                mSPHelper.enablePin(ENABLE);
                sf.getSwitchFingerprint().setEnabled(true);
            }
        }
    }

    @Override
    public void onPinCanceled(int from) {
        if (from == FROM_SETTINGS) {
            SettingsFragment sf = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getName());
            sf.getSwitchPinCode().setChecked(!sf.getSwitchPinCode().isChecked());
        } else if (from == FROM_DASHBOARD) {
            finishAffinity();
        }
    }

    @Override
    public void onConfirmSetUp(String pin, int from) {
        mSPHelper.enablePin(ENABLE);
        mSPHelper.savePin(pin);

        if (from == FROM_DASHBOARD) {
            DialogSetUpFingerprint.newInstance().show(getSupportFragmentManager(), "");
        } else if (from == FROM_SETTINGS) {
            SettingsFragment sf = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getName());
            sf.getSwitchPinCode().setChecked(true);
            sf.getSwitchFingerprint().setEnabled(true);
        }
    }

    @Override
    public void onCancelSetUp(int from) {
        if (from == FROM_SETTINGS) {
            SettingsFragment sf = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getName());
            sf.getSwitchPinCode().setChecked(false);
        }
    }
}