package com.example.wifibuild.permissionManager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class MyBaseActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyPermissionManager.getInstance().setPermissionListener(new MyPermissionListener() {
            @Override
            public void onPermissionDenied(String permission) {
                System.out.println("Permission denied: " + permission);
                MyPermissionManager.getInstance().requestPermission(MyBaseActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
            }

            @Override
            public void onPermissionGranted(String permission) {
                System.out.println("Permission granted: " + permission);
            }

            @Override
            public void onPermissionPermanentlyDenied(String permission) {
                showPermissionSettingsDialog(permission);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermissionManager.getInstance().handlePermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void showPermissionSettingsDialog(String permission) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs " + permission + " permission to function properly. Please grant it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    MyPermissionManager.getInstance().redirectToAppSettings(this);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
