package com.android.phonerecorder.customer;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.phonerecorder.R;
import com.android.phonerecorder.R.id;
import com.android.phonerecorder.R.layout;
import com.android.phonerecorder.R.menu;
import com.android.phonerecorder.R.string;
import com.android.phonerecorder.info.BaseInfo;
import com.android.phonerecorder.provider.DBConstant;
import com.android.phonerecorder.settings.AppSettings;
import com.android.phonerecorder.util.RecordFileManager;

public class RecordListFragment extends ListFragment implements OnCheckedChangeListener, OnClickListener {

    private static final int VIEW_STATE_NORMAL = 0;
    private static final int VIEW_STATE_DELETE = 1;
    private RecordListAdapter mListAdapter;
    private ArrayList<BaseInfo> mRecordList;
    private int mViewState;
    private AlertDialog mAlertDialog;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mViewState = VIEW_STATE_NORMAL;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecordList = new ArrayList<BaseInfo>();
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
        super.onDestroy();
    }

    private void updateUI() {
        mRecordList = RecordFileManager.getInstance(getActivity()).getBaseInfoFromDB(mRecordList);
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
                if (getCheckedCount() > 0) {
                    showConfirmDialog();
                } else {
                    mViewState = VIEW_STATE_NORMAL;
                    mListAdapter.notifyDataSetChanged();
                }
            }
            break;
        case R.id.action_settings: {
            Intent intent = new Intent(getActivity(), AppSettings.class);
            getActivity().startActivity(intent);;
        }
            break;
        }
        return true;
    }
    private void showConfirmDialog() {
        if (mAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.confirm_message);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RecordFileManager.getInstance(getActivity()).deleteBaseInfoFromDB(mRecordList);
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
        LinearLayout itemContainer;
        TextView displayName;
        TextView callLogCount;
        CheckBox checkBox;
    }
    private class RecordListAdapter extends ArrayAdapter<BaseInfo>{

        private Context mContext;
        public RecordListAdapter(Context context, ArrayList<BaseInfo> listInfos) {
            super(context, 0, listInfos);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.baseinfo_item_layout, null);
                viewHolder.itemContainer = (LinearLayout) convertView.findViewById(R.id.item_container);
                viewHolder.itemContainer.setOnClickListener(RecordListFragment.this);
                viewHolder.itemContainer.setTag(position);
                viewHolder.displayName = (TextView) convertView.findViewById(R.id.display_name);
                viewHolder.callLogCount = (TextView) convertView.findViewById(R.id.call_log_count);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.checkBox.setOnCheckedChangeListener(RecordListFragment.this);
                viewHolder.checkBox.setTag(position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BaseInfo info = getItem(position);
            if (info != null) {
                String displayName = info.phoneNumber;
                if (!TextUtils.isEmpty(info.baseInfoName)) {
                    displayName += "(" + info.baseInfoName + ")";
                }
                viewHolder.displayName.setText(displayName);
                String callLog = String.format("%d%s", info.callLogCount, RecordListFragment.this.getResources().getString(R.string.call_log_count));
                viewHolder.callLogCount.setText(callLog);
                viewHolder.checkBox.setChecked(info.checked);
            }
            if (mViewState == VIEW_STATE_NORMAL) {
                viewHolder.checkBox.setVisibility(View.GONE);
            } else if (mViewState == VIEW_STATE_DELETE) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
        int position = (Integer) buttonView.getTag();
        BaseInfo info = mListAdapter.getItem(position);
        info.checked = isChecked;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_container) {
            int position = (Integer) v.getTag();
            BaseInfo info = mListAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
            intent.putExtra(DBConstant._ID, info._id);
            startActivity(intent);
        }
    }

    private int getCheckedCount() {
        int count = 0;
        for (BaseInfo info : mRecordList) {
            if (info.checked) {
                count ++;
            }
        }
        return count;
    }
}
