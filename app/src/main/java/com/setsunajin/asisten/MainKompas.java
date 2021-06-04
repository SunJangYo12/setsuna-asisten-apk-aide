package com.setsunajin.asisten;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainKompas extends Activity implements SensorEventListener {

    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    TextView tvHeading;
    Senter s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kompas);

        image = (ImageView) findViewById(R.id.activity_kompas_imageViewCompass);
        tvHeading = (TextView) findViewById(R.id.activity_kompas_tvHeading);
        s = new Senter();
        s.runingKu();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        image.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                s.runingKu();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);

        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}

class Senter
{
    public static final String TOGGLE_SENTER = "TOGGLE_SENTER";
    public static boolean NYALA = false;
    public static Camera camera;

    public void runingKu(){
        NYALA = !NYALA;
        if (NYALA){
            //on
            if (camera==null) {
                camera = Camera.open();
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        camera.setPreviewTexture(new SurfaceTexture(0));
                    }catch(Exception e) {}
                }
                camera.startPreview();

            }
        }
        else {
            //off
            if (camera!=null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }

}

