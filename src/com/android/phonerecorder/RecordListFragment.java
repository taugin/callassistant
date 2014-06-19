package com.android.phonerecorder;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.TextView;

import com.android.phonerecorder.RecordPlayer.OnCompletionListener;
import com.android.phonerecorder.service.RecordFileManager;

public class RecordListFragment extends ListFragment implements OnCheckedChangeListener, OnClickListener, OnCompletionListener {

    private RecordListAdapter mListAdapter;
    private ArrayList<RecordInfo> mRecordList;
    private RecordInfo mCurRecPlaying;
    private RecordPlayer mRecordPlayer;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRecordPlayer = RecordPlayer.getInstance(getActivity());
        mRecordPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    
    private void updateUI() {
        mRecordList = RecordFileManager.getInstance(getActivity()).listRecordFiles();
        if (mRecordList == null) {
            return ;
        }
        mListAdapter = new RecordListAdapter(getActivity(), mRecordList);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setAdapter(mListAdapter);
        setListShown(true);
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
            RecordFileManager.getInstance(getActivity()).deleteRecordFiles(mRecordList);
            mListAdapter.notifyDataSetChanged();
            break;
        }
        return true;
    }


    class ViewHolder {
        ImageView mediaControl;
        TextView fileName;
        TextView fileSize;
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
                viewHolder.mediaControl.setOnClickListener(RecordListFragment.this);
                viewHolder.mediaControl.setTag(position);
                viewHolder.fileName = (TextView) convertView.findViewById(R.id.filename);
                viewHolder.fileSize = (TextView) convertView.findViewById(R.id.filesize);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.checkBox.setOnCheckedChangeListener(RecordListFragment.this);
                viewHolder.checkBox.setTag(position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            RecordInfo info = getItem(position);
            viewHolder.mediaControl.getDrawable().setLevel(!info.play ? 0 : 1);
            viewHolder.fileName.setText(info.fileName);
            viewHolder.fileSize.setText(byteToString(info.fileSize));
            viewHolder.checkBox.setChecked(info.checked);
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
    @Override
    public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
        int position = (Integer) buttonView.getTag();
        RecordInfo info = mListAdapter.getItem(position);
        info.checked = isChecked;
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