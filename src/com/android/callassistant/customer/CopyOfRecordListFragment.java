package com.android.callassistant.customer;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.callassistant.R;
import com.android.callassistant.info.RecordInfo;
import com.android.callassistant.manager.RecordPlayerManager;
import com.android.callassistant.manager.RecordPlayerManager.OnCompletionListener;
import com.android.callassistant.settings.CallAssistantSettings;
import com.android.callassistant.util.RecordFileManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CopyOfRecordListFragment extends ListFragment implements OnCheckedChangeListener, OnClickListener, OnCompletionListener {

    private static final int VIEW_STATE_NORMAL = 0;
    private static final int VIEW_STATE_DELETE = 1;
    private RecordListAdapter mListAdapter;
    private ArrayList<RecordInfo> mRecordList;
    private RecordPlayerManager mRecordPlayer;
    private int mViewState;
    private AlertDialog mAlertDialog;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRecordPlayer = RecordPlayerManager.getInstance(getActivity());
        mRecordPlayer.setOnCompletionListener(this);
        mViewState = VIEW_STATE_NORMAL;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecordList = new ArrayList<RecordInfo>();
        mListAdapter = new RecordListAdapter(getActivity(), mRecordList);
        getListView().setAdapter(mListAdapter);
        setListShown(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRecordPlayer.stopPlay();
    }

    public boolean onBackPressed() {
        if (mViewState == VIEW_STATE_DELETE) {
            mViewState = VIEW_STATE_NORMAL;
            mListAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mRecordPlayer.release();
        super.onDestroy();
    }

    private void updateUI() {
        mRecordList = RecordFileManager.getInstance(getActivity()).getRecordsFromDB(mRecordList, -1);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_delete:
            if (mViewState == VIEW_STATE_NORMAL) {
                mViewState = VIEW_STATE_DELETE;
                mListAdapter.notifyDataSetChanged();
            } else if (mViewState == VIEW_STATE_DELETE) {
                if (RecordInfo.checkedNumber > 0) {
                    showConfirmDialog();
                } else {
                    mViewState = VIEW_STATE_NORMAL;
                    mListAdapter.notifyDataSetChanged();
                }
            }
            break;
        case R.id.action_settings: {
            Intent intent = new Intent(getActivity(), CallAssistantSettings.class);
            getActivity().startActivity(intent);;
        }
            break;
        }
        return true;
    }

    private void stopPlayWhenDeleting() {
        RecordInfo info = mRecordPlayer.getCurRecord();
        if (info != null && info.checked) {
            mRecordPlayer.stopPlay();
        }
    }
    
    private void showConfirmDialog() {
        if (mAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.confirm_message);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopPlayWhenDeleting();
                    RecordFileManager.getInstance(getActivity()).deleteRecordFiles(mRecordList);
                    mViewState = VIEW_STATE_NORMAL;
                    mListAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
        }
        mAlertDialog.show();
    }

    class ViewHolder {
        ImageView mediaControl;
        TextView fileName;
        TextView fileSize;
        TextView fileTime;
        TextView timeDuration;
        CheckBox checkBox;
    }
    private class RecordListAdapter extends ArrayAdapter<RecordInfo>{

        private Context mContext;
        public RecordListAdapter(Context context, ArrayList<RecordInfo> listInfos) {
            super(context, 0, listInfos);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.record_item_layout, null);
                viewHolder.mediaControl = (ImageView) convertView.findViewById(R.id.media_control);
                viewHolder.mediaControl.setOnClickListener(CopyOfRecordListFragment.this);
                viewHolder.mediaControl.setTag(position);
                viewHolder.fileName = (TextView) convertView.findViewById(R.id.filename);
                viewHolder.fileSize = (TextView) convertView.findViewById(R.id.filesize);
                viewHolder.fileTime = (TextView) convertView.findViewById(R.id.filetime);
                viewHolder.timeDuration = (TextView) convertView.findViewById(R.id.timeduration);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.checkBox.setOnCheckedChangeListener(CopyOfRecordListFragment.this);
                viewHolder.checkBox.setTag(position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            RecordInfo info = getItem(position);
            viewHolder.mediaControl.getDrawable().setLevel(!info.play ? 0 : 1);
            viewHolder.fileName.setText(TextUtils.isEmpty(info.recordName) ? info.recordFile : info.recordName);
            viewHolder.fileSize.setText(byteToString(info.recordSize));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            viewHolder.fileTime.setText(sdf.format(new Date(info.recordStart)));
            int resId = info.incoming ? R.drawable.ic_incoming : R.drawable.ic_outgoing;
            viewHolder.timeDuration.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
            viewHolder.timeDuration.setText(getTimeExperence(info.recordEnd - info.recordStart));
            viewHolder.checkBox.setChecked(info.checked);
            if (mViewState == VIEW_STATE_NORMAL) {
                viewHolder.checkBox.setVisibility(View.GONE);
            } else if (mViewState == VIEW_STATE_DELETE) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        private String byteToString(long size) {
            DecimalFormat df = new DecimalFormat("###.##");
            float f;
            if (size < 1024 * 1024) {
                f = (float) ((float) size / (float) 1024);
                return (df.format(new Float(f).doubleValue()) + "KB");
            } else {
                f = (float) ((float) size / (float) (1024 * 1024));
                return (df.format(new Float(f).doubleValue()) + "MB");
            }
        }
    }

    private String getTimeExperence(long timeExperence) {
        int allsec = Math.round(timeExperence / (float)1000);
        int min = allsec / 60;
        int sec = allsec % 60;
        int hour = min / 60;
        if (hour > 0) {
            min = min % 60;
        }
        String sHour = hour > 0 ? hour + "h" : "";
        String sMin = min > 0 ? min + "m" : "";
        String sSec = sec > 0 ? sec + "s" : "";
        return String.valueOf(sHour + sMin + sSec);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
        int position = (Integer) buttonView.getTag();
        RecordInfo info = mListAdapter.getItem(position);
        info.checked = isChecked;
        if (isChecked) {
            RecordInfo.checkedNumber ++;
        } else {
            RecordInfo.checkedNumber --;
        }
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        RecordInfo info = mListAdapter.getItem(position);
        if (mRecordPlayer.isPlaying()) {
            mRecordPlayer.stopPlay();
            if (mRecordPlayer.getCurRecord() != info) {
                mRecordPlayer.setCurRecord(info);
                mRecordPlayer.startPlay();
            }
        } else {
            mRecordPlayer.setCurRecord(info);
            mRecordPlayer.startPlay();
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCompletion() {
        mListAdapter.notifyDataSetChanged();
    }
}
