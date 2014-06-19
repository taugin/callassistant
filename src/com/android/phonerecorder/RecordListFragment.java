package com.android.phonerecorder;

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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.phonerecorder.service.RecordFileManager;

public class RecordListFragment extends ListFragment {

    private RecordListAdapter mListAdapter;
    ArrayList<RecordInfo> mRecordList;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    private class RecordListAdapter extends ArrayAdapter<RecordInfo> implements OnCheckedChangeListener {

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
                viewHolder.fileName = (TextView) convertView.findViewById(R.id.filename);
                viewHolder.fileSize = (TextView) convertView.findViewById(R.id.filesize);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.checkBox.setOnCheckedChangeListener(this);
                viewHolder.checkBox.setTag(position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            RecordInfo info = getItem(position);
            viewHolder.mediaControl.getDrawable().setLevel(info.play ? 0 : 1);
            viewHolder.fileName.setText(info.fileName);
            viewHolder.fileSize.setText("" + info.fileSize);
            viewHolder.checkBox.setChecked(info.checked);
            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            int position = (Integer) buttonView.getTag();
            RecordInfo info = getItem(position);
            info.checked = isChecked;
        }
        
    }
}
