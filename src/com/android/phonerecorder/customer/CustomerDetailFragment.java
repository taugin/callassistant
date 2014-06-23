package com.android.phonerecorder.customer;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.phonerecorder.R;
import com.android.phonerecorder.info.BaseInfo;
import com.android.phonerecorder.info.RecordInfo;
import com.android.phonerecorder.provider.DBConstant;
import com.android.phonerecorder.util.RecordFileManager;

public class CustomerDetailFragment extends Fragment implements OnClickListener, TextWatcher {

    private int mBaseId;
    private BaseInfo mBaseInfo;
    private ArrayList<RecordInfo> mRecordList;
    private TextView mPhoneNumberView;
    private EditText mCustomerNameView;
    private CallLogListView mCallLogListView;
    private View mEditSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_detail_layout, null);
        mPhoneNumberView = (TextView) view.findViewById(R.id.customer_number);
        mCustomerNameView = (EditText) view.findViewById(R.id.customer_name);
        mCustomerNameView.addTextChangedListener(this);
        mCallLogListView = (CallLogListView) view.findViewById(R.id.call_log_listview);
        View dialNumber = view.findViewById(R.id.dial_number);
        dialNumber.setOnClickListener(this);
        mEditSave = view.findViewById(R.id.edit_save);
        mEditSave.setOnClickListener(this);
        return view;
    }

    public void setBaseInfoId(int id) {
        mBaseId = id;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("taugin", "CustomerDetailFragment onActivityCreated mBaseId = " + mBaseId);
        mRecordList = new ArrayList<RecordInfo>();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecordList = RecordFileManager.getInstance(getActivity()).getRecordsFromDB(mRecordList, mBaseId);
        mBaseInfo = RecordFileManager.getInstance(getActivity()).getSingleBaseInfo(mBaseId);
        mPhoneNumberView.setText(mBaseInfo.phoneNumber);
        mCustomerNameView.setText(mBaseInfo.baseInfoName);
        int len = 0;
        if (!TextUtils.isEmpty(mBaseInfo.baseInfoName)) {
            len = mBaseInfo.baseInfoName.length();
        }
        mCustomerNameView.setSelection(len);
        mCallLogListView.setCallLogList(mRecordList);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCallLogListView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallLogListView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_save) {
            String newName = mCustomerNameView.getText().toString();
            Log.d("taugin", "newName = " + newName);
            if (!TextUtils.isEmpty(newName)) {
                ContentValues values = new ContentValues();
                values.put(DBConstant.BASEINFO_NAME, newName);
                Uri uri = ContentUris.withAppendedId(DBConstant.BASEINFO_URI, mBaseId);
                int ret = getActivity().getContentResolver().update(uri, values, null, null);
                String message = null;
                if (ret > 0) {
                    message = getActivity().getResources().getString(R.string.save_success);
                } else {
                    message = getActivity().getResources().getString(R.string.save_failure);
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.dial_number) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mBaseInfo.phoneNumber));
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        
    }

    @Override
    public void afterTextChanged(Editable s) {
        String newName = mCustomerNameView.getText().toString();
        if (TextUtils.isEmpty(newName)) {
            mEditSave.setEnabled(false);
        } else {
            mEditSave.setEnabled(true);
        }
    }
    
    
}
