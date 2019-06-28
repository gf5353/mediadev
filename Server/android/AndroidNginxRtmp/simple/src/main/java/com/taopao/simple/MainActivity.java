package com.taopao.simple;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.taopao.androidnginxrtmp.service.NginxUtils;

public class MainActivity extends Activity {
    //    Nginx mNginx;
    private String url;
    private final int reqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mNginx = Nginx.create();
        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onClick(v);

            }
        });
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        TextView tv_ip = (TextView) findViewById(R.id.tv_ip);
        url = "rtmp://" + ip + ":1935/live/xxx";
        tv_ip.setText("推拉流地址" + url);
    }

    private void onClick(View v) {
        TextView textView = (TextView) v;
//                mNginx.start();
//                Intent intent = new Intent(MainActivity.this, NginxService.class);
//                intent.setAction(NginxService.ACTION_START_NGINX);
//                startService(intent);
        int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
            return;
        }
        if (v.getTag() == null) {
            NginxUtils.startNginx(MainActivity.this);
            textView.setText("关闭服务");
            v.setTag("");
        } else {
            NginxUtils.stopNginx(MainActivity.this);
            textView.setText("开启服务");
            v.setTag(null);
        }
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case reqCode:
                //权限拒绝或者通过
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onClick(findViewById(R.id.open));
                }
                break;
            default:
                break;
        }
    }
}
