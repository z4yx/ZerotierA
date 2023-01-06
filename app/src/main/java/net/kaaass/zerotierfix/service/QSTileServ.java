package net.kaaass.zerotierfix.service;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.zerotier.sdk.NodeStatus;
import com.zerotier.sdk.Version;

import net.kaaass.zerotierfix.R;
import net.kaaass.zerotierfix.events.NodeDestroyedEvent;
import net.kaaass.zerotierfix.events.NodeStatusEvent;
import net.kaaass.zerotierfix.events.RequestNodeStatusEvent;
import net.kaaass.zerotierfix.ui.NetworkListActivity;
import net.kaaass.zerotierfix.ui.NetworkListFragment;
import net.kaaass.zerotierfix.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
public class QSTileServ extends TileService {
    public static final String TAG = "QSTileServ";
    private final EventBus eventBus;

    public QSTileServ() {
        this.eventBus = EventBus.getDefault();
//        this.eventBus.register(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onStartListening");
        this.eventBus.register(this);
        this.eventBus.post(new RequestNodeStatusEvent());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onStopListening");
        this.eventBus.unregister(this);
    }

    @Override
    public void onClick() {
        super.onClick();

        Log.i(TAG, "Clicking the QS tile");

        Intent it = new Intent(this, NetworkListActivity.class);
        it.putExtra(NetworkListFragment.START_FOR_REASON, 1);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
//        startActivityAndCollapse(it);
//        Tile tile = getQsTile();
//        int st = tile.getState();
//        tile.setState(st==Tile.STATE_INACTIVE ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
//        tile.updateTile();
    }

    private  void setQsTileState(boolean isActive) {
        Tile tile = getQsTile();
        if (isActive) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }
        tile.updateTile();

    }

    /**
     * 节点状态事件回调
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNodeStatus(NodeStatusEvent event) {
        NodeStatus status = event.getStatus();
        Log.i(TAG, "called onNodeStatus, isOnline="+status.isOnline());
        // 更新在线状态
        setQsTileState(status.isOnline());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNodeDestroyed(NodeDestroyedEvent nodeDestroyedEvent) {
        Log.i(TAG, "called onNodeDestroyed");

        setQsTileState(false);
    }
}
