package com.rise;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.rise.bean.Item;
import com.rise.bean.NotesItem;
import com.rise.component.Sync;

import java.util.List;

/**
 * Created by kai.wang on 5/21/14.
 */
public class SyncService extends Service {

    private int syncCount = 0;
    private List<Item> items;
    private List<NotesItem> notes;

    private final int COUNT_FINISH = 100;
    private final int FIND_ITEMS_FINISH = 101;
    private final int FIND_NOTES_FINISH = 102;
    private final int SYNC_UP_ITEM_FINISH = 103;
    private final int SYNC_UP_NOTE_FINISH = 104;
    private final int SYNC_DOWN_FINISH = 105;





    private Sync sync;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COUNT_FINISH:
                    syncCount = (Integer) msg.obj;
                    if (syncCount != 0) {
                        sync.getItems(handler, FIND_ITEMS_FINISH);
                    } else {
                        // 已经上传完毕
                        // 下载
                        Toast.makeText(SyncService.this,R.string.synced,Toast.LENGTH_SHORT).show();
                        uploadFinishThenDownload();
                    }
                    break;
                case FIND_ITEMS_FINISH:
                    items = (List<Item>) msg.obj;
                    if (items != null && items.size() != 0) {
                        upItem();
                    } else {
                        sync.getNotes(handler, FIND_NOTES_FINISH);
                    }
                    break;
                case FIND_NOTES_FINISH:
                    notes = (List<NotesItem>) msg.obj;
                    if (notes != null && notes.size() != 0) {
                        // 同步notes
                        upNote();
                    } else {
                        // 已经上传完毕
                        // 下载
                        Toast.makeText(SyncService.this,R.string.sync_success,Toast.LENGTH_SHORT).show();
                        uploadFinishThenDownload();
                    }
                    break;
                case SYNC_UP_ITEM_FINISH:
                    if (items.size() != 0) {
                        upItem();
                    } else {
                        sync.getNotes(handler, FIND_NOTES_FINISH);
                    }
                    break;
                case SYNC_UP_NOTE_FINISH:
                    if (notes.size() != 0) {
                        upNote();
                    } else {
                        // 已经上传完毕
                        // 下载
                        Toast.makeText(SyncService.this, R.string.sync_success, Toast.LENGTH_SHORT).show();
                        uploadFinishThenDownload();
                    }
                    break;
                case SYNC_DOWN_FINISH:
                    // todo 更新数据库
                    // todo 同步完毕
                    break;
            }

            return false;
        }
    });


    @Override
    public void onCreate() {
        super.onCreate();
        sync = new Sync(this);
        sync.count(handler,COUNT_FINISH);
    }

    private void upItem(){
        sync.upItem(items.get(0), handler, SYNC_UP_ITEM_FINISH);
        items.remove(0);
    }

    private void upNote(){
        sync.upNote(notes.get(0),handler,SYNC_UP_NOTE_FINISH);
        notes.remove(0);
    }

    /**
     * 已经上传完毕,开始下载
     */
    private void uploadFinishThenDownload(){
        stopSelf();
        // TODO down
//        sync.down(handler,SYNC_DOWN_FINISH);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}