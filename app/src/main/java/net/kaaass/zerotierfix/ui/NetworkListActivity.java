package net.kaaass.zerotierfix.ui;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

/**
 * 网络列表 fragment 的容器 activity
 */
public class NetworkListActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new NetworkListFragment();
    }


    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        Log.d("NetworkListActivity", "NetworkListActivity.onNewIntent");
        setIntent(intent);
    }
}
