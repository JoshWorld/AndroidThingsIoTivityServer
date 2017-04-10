package com.kmk.iotivityserver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kmk.iotivityserver.resource.TurnoffResource;
import com.kmk.iotivityserver.resource.TurnonResource;

import org.iotivity.base.ModeType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final Activity mActivity = this;

    TurnonResource turnonResource;
    TurnoffResource turnoffResource;

    Gpio ledGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        startServer();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopServer();
        super.onDestroy();
    }

    private void startServer() {
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            ledGpio = service.openGpio("IO13");
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PlatformConfig platformConfig = new PlatformConfig(mActivity, ServiceType.IN_PROC, ModeType.SERVER,
                "0.0.0.0", 5683, QualityOfService.LOW);
        OcPlatform.Configure(platformConfig);
        turnonResource = new TurnonResource(ledGpio);
        turnoffResource = new TurnoffResource(ledGpio);
        try {
            turnonResource.registerResource();
            turnoffResource.registerResource();
        } catch (OcException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Server is started");
    }

    private void stopServer() {
        try {
            turnonResource.unregisterResource();
            turnoffResource.unregisterResource();

            if (ledGpio != null) {
                ledGpio.close();
            }
        } catch (OcException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Server is stopped");
    }
}