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
import net.kaaass.zerotierfix.events.NodeStatusRequestEvent;
import net.kaaass.zerotierfix.ui.NetworkListActivity;
import net.kaaass.zerotierfix.ui.NetworkListFragment;
import net.kaaass.zerotierfix.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
public class QSTileServ extends TileService {
    public static final String TAG = "QSTileServ";
    private final EventBus eventBus;
    private boolean cachedState = false;

    public QSTileServ() {
        this.eventBus = EventBus.getDefault();
//        this.eventBus.register(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        this.eventBus.register(this);
        this.eventBus.post(new NodeStatusRequestEvent());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        this.eventBus.unregister(this);
    }

    @Override
    public void onClick() {
        super.onClick();

        Log.i(TAG, "Clicking the QS tile");

        Tile tile = getQsTile();
        if(tile != null) {
            // 短暂的不可用状态
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.updateTile();
        }

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
    
    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.i(TAG, "QS start listening");
        setQsTileState(cachedState);
        this.eventBus.post(new NodeStatusRequestEvent());
    }
    
    // @Override
    // public void onStopListening() {
    //     Log.i(TAG, "QS stop listening");
    //     super.onStopListening();
    // }
    
    private  void setQsTileState(boolean isActive) {
        Tile tile = getQsTile();
        if (tile == null) return;
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeStatus(NodeStatusEvent event) {
        NodeStatus status = event.getStatus();
        Log.i(TAG, "called onNodeStatus, isOnline="+status.isOnline());

        if(!status.isOnline() && !cachedState) return;
        // 更新在线状态
        cachedState = status.isOnline();
        setQsTileState(status.isOnline());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeDestroyed(NodeDestroyedEvent nodeDestroyedEvent) {
        Log.i(TAG, "called onNodeDestroyed");

        // 离线状态
        cachedState = false;
        setQsTileState(false);
    }
}
