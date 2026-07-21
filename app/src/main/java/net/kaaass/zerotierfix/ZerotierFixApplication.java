package net.kaaass.zerotierfix;

import android.content.ComponentName;
import android.service.quicksettings.TileService;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import net.kaaass.zerotierfix.model.DaoMaster;
import net.kaaass.zerotierfix.model.DaoSession;
import net.kaaass.zerotierfix.model.ZTOpenHelper;
import net.kaaass.zerotierfix.service.QSTileServ;

/**
 * 主程序入口
 *
 * @author kaaass
 */
public class ZerotierFixApplication extends MultiDexApplication {
    private DaoSession mDaoSession;

    public void onCreate() {
        super.onCreate();
        Log.i("Application", "Starting Application");
        // 创建 DAO 会话
        this.mDaoSession = new DaoMaster(
                new ZTOpenHelper(this, "ztfixdb", null)
                        .getWritableDatabase()
        ).newSession();

        TileService.requestListeningState(this, new ComponentName(this, QSTileServ.class));
    }

    public DaoSession getDaoSession() {
        return this.mDaoSession;
    }
}
