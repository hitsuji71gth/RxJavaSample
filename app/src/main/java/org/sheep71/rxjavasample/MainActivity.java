package org.sheep71.rxjavasample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {

    private Context mContext = null;

    private Button btnScan;
    private ListView lvScanResult;
    private ArrayAdapter<String> adapter;

    private Subscription subscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        btnScan = (Button)findViewById(R.id.button);
        lvScanResult = (ListView)findViewById(R.id.listView);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnScan.setEnabled(false);
                adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
                lvScanResult.setAdapter(adapter);
                subscription = ScanAgent.scan(mContext)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(filterMalware)
                        .filter(filterNotInApprovedApps)
                        .map(getAppName)
                        .subscribe(observer);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) subscription.unsubscribe();
    }

    // Filter only malware
    private Func1<ScanResult, Boolean> filterMalware = new Func1<ScanResult, Boolean>() {
        @Override
        public Boolean call(ScanResult scanResult) {
            return !scanResult.isSafe();
        }
    };

    // Omit apps in approved list
    private Func1<ScanResult, Boolean> filterNotInApprovedApps = new Func1<ScanResult, Boolean>() {

        List<String> approvedApps = Arrays.asList("LINE", "Facebook", "Twitter");

        @Override
        public Boolean call(ScanResult scanResult) {
            return !approvedApps.contains(scanResult.getName());
        }
    };

    // Get app's name
    private Func1<ScanResult, String> getAppName = new Func1<ScanResult, String>() {
        @Override
        public String call(ScanResult scanResult) {
            return scanResult.getName();
        }
    };

    // Display malware
    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onNext(String appName) {
            adapter.add(appName);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCompleted() {
            btnScan.setEnabled(true);
            Toast.makeText(mContext, "Finished scanning", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(Throwable e) {
            btnScan.setEnabled(true);
            Toast.makeText(mContext, "Error in scanning", Toast.LENGTH_LONG).show();
            Log.e("HOGE", "Error in scanning", e);
        }
    };
}
