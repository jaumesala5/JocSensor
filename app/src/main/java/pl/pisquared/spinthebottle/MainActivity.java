package pl.pisquared.spinthebottle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private static final String SENSOR_MISSING_LIGHT = "No tens el sensor de llum, no obtindreu totes les funcions d'aquesta aplicació";
    private static final String SENSOR_MISSING_GYROSCOPE = "\n" + "El vostre dispositiu no té cap sensor de gyroscope, que és necessari per utilitzar aquesta aplicació";
    private static final int LIGHT_MODE = 1;
    private static final int DARK_MODE = 2;
    private static final float BREAKING_ANGULAR_ACC = 0.05f;
    private static final int ROTATION_ANIMATION_TIME_FACTOR = 20;
    private static final float MIN_ANGULAR_VELOCITY_TO_TRIGGER_SPIN = 2;
    private View containerView;
    private ImageView ivBottle;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private Sensor lightSensor;
    private int currentLightMode = LIGHT_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSensors();

        containerView = findViewById(R.id.container_view);
        ivBottle = findViewById(R.id.iv_bottle);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerSensors();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        int sensorType = event.sensor.getType();
        switch(sensorType)
        {
            case Sensor.TYPE_LIGHT:
                respondToLightChange(event.values[0]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                respondToLinearAcc(event.values);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    private void initSensors()
    {
        //comprovem que tinguin els sensors en cas de que no els tingesin els notifiquem
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager != null)
        {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if(gyroscope != null)
            {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                Toast.makeText(this, SENSOR_MISSING_GYROSCOPE, Toast.LENGTH_LONG).show();
            }
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if(lightSensor != null)
            {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                Toast.makeText(this, SENSOR_MISSING_LIGHT, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void registerSensors()
    {
        if(sensorManager != null)
        {
            if(gyroscope != null)
            {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                Toast.makeText(this, SENSOR_MISSING_GYROSCOPE, Toast.LENGTH_LONG).show();
            }
            if(lightSensor != null)
            {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                Toast.makeText(this, SENSOR_MISSING_LIGHT, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void unregisterSensors()
    {
        if(sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    private void respondToLightChange(float light)
    {
        //Amb el sensor de la llum que retorna un valor miram que sigui major de 20 lo qual el posarem light mode i li asignarem unes imatges concretes
        if(light > 20 && currentLightMode == DARK_MODE)
        {
            currentLightMode = LIGHT_MODE;
            containerView.setBackgroundResource(R.drawable.soft_background);
            ivBottle.setBackgroundResource(R.drawable.fruit_juice_bottle);
        }
        //Amb el sensor de la llum que retorna un valor miram que sigui menor de 20 lo qual el posarem dark mode i li asignarem unes imatges concretes
        else if(light < 20 && currentLightMode == LIGHT_MODE)
        {
            currentLightMode = DARK_MODE;
            containerView.setBackgroundResource(R.drawable.disco_background);
            ivBottle.setBackgroundResource(R.drawable.vodka_bottle);
        }
    }
//codi per fer rotar la imatge
    private void respondToLinearAcc(float [] values)
    {
        float w_z = values[2];
        int rotationDirection = w_z < 0 ? 1 : -1;

        if(Math.abs(w_z) > MIN_ANGULAR_VELOCITY_TO_TRIGGER_SPIN)
        {
            float time = Math.abs(w_z) / BREAKING_ANGULAR_ACC;
            float angularDistance = w_z * w_z / (2 * BREAKING_ANGULAR_ACC);

            ivBottle.animate()
                    .setDuration((long) (time * ROTATION_ANIMATION_TIME_FACTOR))
                    .rotation(angularDistance * rotationDirection).start();
        }
    }

}
