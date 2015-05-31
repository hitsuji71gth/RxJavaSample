package org.sheep71.rxjavasample;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;

public class ScanAgent {
    // Scan installed apps
    public static Observable<ScanResult> scan(final Context context) {
        return Observable.create(new Observable.OnSubscribe<ScanResult>() {
            @Override
            public void call(Subscriber<? super ScanResult> subscriber) {
                try {
                    PackageManager pm = context.getPackageManager();
                    List<ApplicationInfo> appList = pm.getInstalledApplications(0);
                    for (ApplicationInfo app : appList) {
                        ScanResult result = new ScanResult(
                                app.loadLabel(pm).toString(),
                                app.packageName,
                                scanApp(app.packageName));
                        subscriber.onNext(result);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    // Scan app (Dummy)
    private static boolean scanApp(String path) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        // Return random value
        return random.nextBoolean();
    }
}
