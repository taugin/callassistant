package com.android.callassistant.customer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.callassistant.R;
import com.android.callassistant.info.ContactInfo;
import com.android.callassistant.manager.BlackNameManager;
import com.android.callassistant.provider.DBConstant;
import com.android.callassistant.settings.CallAssistantSettings;
import com.android.callassistant.util.Log;
import com.android.callassistant.util.RecordFileManager;

public class RecordListFragment extends ListFragment implements OnCheckedChangeListener, OnClickListener {

    private static final int VIEW_STATE_NORMAL = 0;
    private static final int VIEW_STATE_DELETE = 1;
    private RecordListAdapter mListAdapter;
    private ArrayList<ContactInfo> mRecordList;
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
        mRecordList = new ArrayList<ContactInfo>();
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
        mRecordList = RecordFileManager.getInstance(getActivity()).getContactFromDB(mRecordList);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.record_menu, menu);
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
            Intent intent = new Intent(getActivity(), CallAssistantSettings.class);
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
                    RecordFileManager.getInstance(getActivity()).deleteContactFromDB(mRecordList);
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
        LinearLayout dialNumber;
        LinearLayout itemContainer;
        TextView displayName;
        TextView callState;
        TextView callLogDate;
        CheckBox checkBox;
        LinearLayout checkBoxContainer;
        View functionMenu;
    }
    private class RecordListAdapter extends ArrayAdapter<ContactInfo>{

        private Context mContext;
        public RecordListAdapter(Context context, ArrayList<ContactInfo> listInfos) {
            super(context, 0, listInfos);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item_layout, null);
                viewHolder.dialNumber = (LinearLayout) convertView.findViewById(R.id.dial_number);
                viewHolder.dialNumber.setOnClickListener(RecordListFragment.this);
                viewHolder.itemContainer = (LinearLayout) convertView.findViewById(R.id.item_container);
                viewHolder.itemContainer.setOnClickListener(RecordListFragment.this);
                viewHolder.displayName = (TextView) convertView.findViewById(R.id.display_name);
                viewHolder.callState = (TextView) convertView.findViewById(R.id.call_state);
                viewHolder.callLogDate = (TextView) convertView.findViewById(R.id.call_log_date);
                viewHolder.functionMenu = convertView.findViewById(R.id.function_menu);
                viewHolder.functionMenu.setTag(position);
                viewHolder.functionMenu.setOnClickListener(RecordListFragment.this);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.checkBoxContainer = (LinearLayout) convertView.findViewById(R.id.check_box_container);
                viewHolder.checkBoxContainer.setOnClickListener(RecordListFragment.this);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.itemContainer.setTag(position);
            viewHolder.dialNumber.setTag(position);
            viewHolder.checkBoxContainer.setTag(position);

            ContactInfo info = getItem(position);
            if (info != null) {
                String displayName = info.contactNumber;
                if (!TextUtils.isEmpty(info.contactName)) {
                    displayName += "-" + info.contactName;
                }
                viewHolder.displayName.setText(displayName);
                // String callLog = String.format("%d%s", info.contactLogCount, RecordListFragment.this.getResources().getString(R.string.call_log_count));
                viewHolder.callState.setText(null);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                viewHolder.callLogDate.setText(sdf.format(new Date(info.contactUpdate)));
                viewHolder.checkBox.setChecked(info.checked);
            }
            if (mViewState == VIEW_STATE_NORMAL) {
                viewHolder.functionMenu.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            } else if (mViewState == VIEW_STATE_DELETE) {
                viewHolder.functionMenu.setVisibility(View.INVISIBLE);
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
        if (buttonView.getId() == R.id.check_box) {
            int position = (Integer) buttonView.getTag();
            ContactInfo info = mListAdapter.getItem(position);
            info.checked = isChecked;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_container) {
            int position = (Integer) v.getTag();
            ContactInfo info = mListAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
            intent.putExtra(DBConstant._ID, info._id);
            startActivity(intent);
        } if (v.getId() == R.id.dial_number) {
            int position = (Integer) v.getTag();
            ContactInfo info = mListAdapter.getItem(position);
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + info.contactNumber));
            getActivity().startActivity(intent);
        } else if (v.getId() == R.id.function_menu) {
            if (mPopupWindow == null) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu, null);
                mCheckBox = (CheckBox) view.findViewById(R.id.add_black_name);
                mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mPopupWindow.setContentView(view);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupWindow.setFocusable(true);
            }
            int position = (Integer) v.getTag();
            ContactInfo info = mListAdapter.getItem(position);
            mCheckBox.setTag(position);
            mCheckBox.setOnClickListener(this);
            Log.d(Log.TAG, "function_menu info.blocked = " + info.blocked + " , position = " + position);
            mCheckBox.setChecked(info.blocked);

            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(v);
            }
        } else if (v.getId() == R.id.add_black_name) {
            int position = (Integer) v.getTag();
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }

            ContactInfo info = mListAdapter.getItem(position);
            Log.d(Log.TAG, "info blocked = " + info.blocked);
            if (!info.blocked) {
                ContentValues values = new ContentValues();
                values.put(DBConstant.BLOCK_NAME, info.contactName);
                values.put(DBConstant.BLOCK_NUMBER, info.contactNumber);
                if (getActivity().getContentResolver().insert(DBConstant.BLOCK_URI, values) != null) {
                    info.blocked = true;
                }
            } else {
                if (BlackNameManager.getInstance(getActivity()).deleteBlackName(info.contactNumber)) {
                    info.blocked = false;
                }
            }
        } else if (v.getId() == R.id.check_box_container) {
            int position = (Integer) v.getTag();
            ContactInfo info = mListAdapter.getItem(position);
            info.checked = !info.checked;
        }
    }

    private int getCheckedCount() {
        int count = 0;
        for (ContactInfo info : mRecordList) {
            if (info.checked) {
                count ++;
            }
        }
        return count;
    }
}
