package com.example.wifibuild.permissionManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MyPermissionManager {
    private static MyPermissionManager instance;
    private MyPermissionListener listener;

    private MyPermissionManager() {
    }

    public static synchronized MyPermissionManager getInstance() {
        if (instance == null) {
            instance = new MyPermissionManager();
        }
        return instance;
    }

    public void setPermissionListener(MyPermissionListener listener) {
        this.listener = listener;
    }

    public void requestPermission(Activity activity, String[] permissions, int requestCode) {
        List<String> pendingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                pendingPermissions.add(permission);
            } else if (listener != null) {
                listener.onPermissionGranted(permission);
            }
        }


        if (!pendingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, pendingPermissions.toArray(new String[0]), requestCode);
        }
    }

    public void handlePermissionsResult(
            Activity activity,
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (listener != null) {
                    listener.onPermissionGranted(permissions[i]);
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    if (listener != null) {
                        listener.onPermissionPermanentlyDenied(permissions[i]);
                    }
                } else {
                    if (listener != null) {
                        listener.onPermissionDenied(permissions[i]);
                    }
                    requestPermission(activity, permissions, requestCode);
                }
            }
        }
    }

    public void redirectToAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}