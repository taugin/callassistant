package com.android.callassistant.black;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.callassistant.R;
import com.android.callassistant.info.BlackInfo;
import com.android.callassistant.provider.DBConstant;
import com.android.callassistant.util.Log;
import com.android.callassistant.util.RecordFileManager;
import com.android.callassistant.util.SelectBlackList;

public class BlackListFragment extends ListFragment implements OnClickListener {

    private static final int VIEW_STATE_NORMAL = 0;
    private static final int VIEW_STATE_DELETE = 1;
    private BlackListAdapter mListAdapter;
    private ArrayList<BlackInfo> mBlackList;
    private int mViewState;
    private AlertDialog mAlertDialog;
    private PopupWindow mPopupWindow;
    private CheckBox mCheckBox;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mViewState = VIEW_STATE_NORMAL;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBlackList = new ArrayList<BlackInfo>();
        mListAdapter = new BlackListAdapter(getActivity(), mBlackList);
        getListView().setAdapter(mListAdapter);
        setListShown(true);
        getActivity().getContentResolver().registerContentObserver(DBConstant.BLOCK_URI, true, mBlockObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHandler.hasMessages(UPDATE_LIST)) {
            mHandler.removeMessages(UPDATE_LIST);
        }
        mHandler.sendEmptyMessageDelayed(UPDATE_LIST, 500);
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
        getActivity().getContentResolver().unregisterContentObserver(mBlockObserver);
        super.onDestroy();
    }

    private void updateUI() {
        Log.d("taugin", "updateUI");
        mBlackList = RecordFileManager.getInstance(getActivity()).getBlackListFromDB(mBlackList);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.black_menu, menu);
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
        case R.id.action_add: {
            Intent intent = new Intent(getActivity(), SelectBlackList.class);
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
                    RecordFileManager.getInstance(getActivity()).deleteBlackInfoFromDB(mBlackList);
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
        TextView blockCount;
        TextView blockDate;
        LinearLayout checkBoxContainer;
        CheckBox checkBox;
        View deleteBlack;
    }
    private class BlackListAdapter extends ArrayAdapter<BlackInfo>{

        private Context mContext;
        public BlackListAdapter(Context context, ArrayList<BlackInfo> listInfos) {
            super(context, 0, listInfos);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.black_item_layout, null);
                viewHolder.itemContainer = (LinearLayout) convertView.findViewById(R.id.item_container);
                viewHolder.itemContainer.setOnClickListener(BlackListFragment.this);
                viewHolder.displayName = (TextView) convertView.findViewById(R.id.display_name);
                viewHolder.blockCount = (TextView) convertView.findViewById(R.id.block_count);
                viewHolder.blockDate = (TextView) convertView.findViewById(R.id.block_date);
                viewHolder.deleteBlack = convertView.findViewById(R.id.delete_black);
                viewHolder.deleteBlack.setOnClickListener(BlackListFragment.this);
                viewHolder.checkBoxContainer = (LinearLayout) convertView.findViewById(R.id.check_box_container);
                viewHolder.checkBoxContainer.setOnClickListener(BlackListFragment.this);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.itemContainer.setTag(position);
            viewHolder.deleteBlack.setTag(position);
            viewHolder.checkBoxContainer.setTag(position);

            BlackInfo info = getItem(position);
            if (info != null) {
                String displayName = info.blackNumber;
                if (!TextUtils.isEmpty(info.blackName)) {
                    displayName += "-" + info.blackName;
                }
                viewHolder.displayName.setText(displayName);
                viewHolder.blockCount.setText(getResources().getString(R.string.block_times_args, info.blockCount));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (info.blockTime != null) {
                    String dates[] = info.blockTime.split(",");
                    if (dates != null) {
                        String lastDate = dates[dates.length - 1];
                        long lastTime = 0;
                        try {
                            lastTime = Long.parseLong(lastDate);
                        } catch(NumberFormatException e) {
                            lastTime = System.currentTimeMillis();
                        }
                        viewHolder.blockDate.setText(sdf.format(new Date(lastTime)));
                    }
                }
                viewHolder.checkBox.setChecked(info.checked);
            }
            if (mViewState == VIEW_STATE_NORMAL) {
                viewHolder.deleteBlack.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            } else if (mViewState == VIEW_STATE_DELETE) {
                viewHolder.deleteBlack.setVisibility(View.INVISIBLE);
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_container) {
            int position = (Integer) v.getTag();
            BlackInfo info = mListAdapter.getItem(position);
            //Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
            //intent.putExtra(DBConstant._ID, info._id);
            //startActivity(intent);
            Log.d("taugin", "position = " + position);
            Log.d("taugin", "info = " + info.blackNumber);
        } else if (v.getId() == R.id.check_box_container) {
            int position = (Integer) v.getTag();
            BlackInfo info = mListAdapter.getItem(position);
            info.checked = !info.checked;
        } else if (v.getId() == R.id.delete_black) {
            synchronized(mListAdapter) {
                int position = (Integer) v.getTag();
                BlackInfo info = mListAdapter.getItem(position);
                Uri uri = ContentUris.withAppendedId(DBConstant.BLOCK_URI, info._id);
                int ret = getActivity().getContentResolver().delete(uri, null, null);
                if (ret > 0) {
                    mListAdapter.remove(info);
                }
            }
        }
    }

    private int getCheckedCount() {
        int count = 0;
        for (BlackInfo info : mBlackList) {
            if (info.checked) {
                count ++;
            }
        }
        return count;
    }
    
    private RecordObserver mBlockObserver = new RecordObserver();
    private class RecordObserver extends ContentObserver {
        public RecordObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(Log.TAG, "onChange selfChange = " + selfChange + " , uri = " + uri);
            if (mHandler.hasMessages(UPDATE_LIST)) {
                mHandler.removeMessages(UPDATE_LIST);
            }
            mHandler.sendEmptyMessageDelayed(UPDATE_LIST, 500);
        }
    }
    
    private static final int UPDATE_LIST = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case UPDATE_LIST:
                updateUI();
                break;
            }
        }
    };
}
