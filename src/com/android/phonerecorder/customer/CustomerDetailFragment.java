package com.android.phonerecorder.customer;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.phonerecorder.BaseInfo;
import com.android.phonerecorder.R;
import com.android.phonerecorder.RecordInfo;
import com.android.phonerecorder.util.RecordFileManager;

public class CustomerDetailFragment extends Fragment {

    private int mBaseId;
    private BaseInfo mBaseInfo;
    private ArrayList<RecordInfo> mRecordList;
    private TextView mPhoneNumberView;
    private TextView mCustomerNameView;
    private ImageView mDialView;
    private CallLogListView mCallLogListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_detail_layout, null);
        mPhoneNumberView = (TextView) view.findViewById(R.id.customer_number);
        mCustomerNameView = (TextView) view.findViewById(R.id.customer_name);
        mDialView = (ImageView) view.findViewById(R.id.dial_number);
        mCallLogListView = (CallLogListView) view.findViewById(R.id.call_log_listview);
        
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
        mRecordList = RecordFileManager.getInstance(getActivity()).getRecordsFromDB(mRecordList, mBaseId);
        mBaseInfo = RecordFileManager.getInstance(getActivity()).getSingleBaseInfo(mBaseId);
        mPhoneNumberView.setText(mBaseInfo.phoneNumber);
        mCustomerNameView.setText(mBaseInfo.baseInfoName);
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
    
    
}
