package com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsServersFragmentForGenerateActivity;

import static com.aspanta.emcsec.tools.Config.SEED;
import static com.aspanta.emcsec.tools.Config.SERVER_HOST_BTC;
import static com.aspanta.emcsec.tools.Config.SERVER_HOST_EMC;
import static com.aspanta.emcsec.tools.Config.SERVER_PORT_BTC;
import static com.aspanta.emcsec.tools.Config.SERVER_PORT_EMC;

public class GenerateRestoreSeedActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_restore_seed);

        if (!SharedPreferencesHelper.getInstance().getStringValue(SEED).isEmpty() &&
                !SharedPreferencesHelper.getInstance().getStringValue(SEED).equals("?")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        SharedPreferencesHelper.getInstance().putStringValue(SERVER_HOST_BTC, "btcx.emercoin.com");
        SharedPreferencesHelper.getInstance().putIntValue(SERVER_PORT_BTC, 50001);
        SharedPreferencesHelper.getInstance().putStringValue(SERVER_HOST_EMC, "emcx.emercoin.com");
        SharedPreferencesHelper.getInstance().putIntValue(SERVER_PORT_EMC, 9110);

        findViewById(R.id.btn_generate_seed).setOnClickListener(c ->
                startActivity(new Intent(this, AttentionActivity.class)));

        findViewById(R.id.btn_restore_from_seed).setOnClickListener(c ->
                startActivity(new Intent(this, SeedRestoringActivity.class)));

        findViewById(R.id.tv_advanced_server_settings).setOnClickListener(c -> {
                    SettingsServersFragmentForGenerateActivity fragment = SettingsServersFragmentForGenerateActivity.newInstance();
                    navigator(fragment, fragment.getCurrentTag());
                }
        );
    }

    public void navigator(Fragment fragment, String TAG) {

        Fragment f = getSupportFragmentManager().findFragmentByTag(TAG);

        if (!(f != null && f.isVisible())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_animation, R.anim.exit_animation,
                    R.anim.pop_enter_animation, R.anim.pop_exit_animation);
            fragmentTransaction
                    .add(fragment, ((IBaseFragment) fragment).getCurrentTag())
                    .replace(R.id.container_generate_restore, fragment)
                    .commitAllowingStateLoss();
        }
    }

    public void navigatorBackPressed(Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.pop_enter_animation, R.anim.pop_exit_animation,
                R.anim.enter_animation, R.anim.exit_animation);
        fragmentTransaction
                .add(fragment, ((IBaseFragment) fragment).getCurrentTag())
                .replace(R.id.container_generate_restore, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {

        IBaseFragment iBaseFragment = null;
        for (Fragment f : fragmentManager.getFragments()) {
            if (f instanceof IBaseFragment) {
                iBaseFragment = (IBaseFragment) f;
                break;
            }
        }
        if (iBaseFragment != null) {
            iBaseFragment.onBackPressed();
        } else {
            finishAffinity();
        }
    }
}