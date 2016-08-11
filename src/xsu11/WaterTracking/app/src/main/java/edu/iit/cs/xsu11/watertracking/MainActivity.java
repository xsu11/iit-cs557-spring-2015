package edu.iit.cs.xsu11.watertracking;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Arrays;

// import android.app.Activity;

public class MainActivity extends ActionBarActivity {
    // Global variables

    // Sensor variables
    public SensorManager sensorManager;
    public Sensor accelSensor;
    public SensorEventListener sensorEventListener;
    public TextView accelOutput;
    // public TextView maxAccelOutput;

    // Button variables
    Button saveBtn;
    Button startBtn;
    Button stopBtn;
    Button resetBtn;

    RadioGroup genderRG;
    RadioButton genderRB;
    RadioGroup ageRG;
    RadioButton ageRB;
    RadioGroup objectiveRG;
    RadioButton objectiveRB;

    // Graph variables
    LinearLayout graphLL;
    LineGraphView graph;

    Boolean saveFlag = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up vertical layout
        // ll = (LinearLayout) findViewById(R.id.label1);
        // ll.setOrientation(LinearLayout.VERTICAL);

        //Assigning a sensor manger to sensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        saveBtn = (Button) findViewById(R.id.SaveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                genderRG = (RadioGroup) findViewById(R.id.GenderRadioGroup);
                genderRB = (RadioButton) findViewById(genderRG.getCheckedRadioButtonId());
                String gender = genderRB.getText().toString();

                ageRG = (RadioGroup) findViewById(R.id.AgeRadioGroup);
                ageRB = (RadioButton) findViewById(ageRG.getCheckedRadioButtonId());
                String age = ageRB.getText().toString();

                objectiveRG = (RadioGroup) findViewById(R.id.ObjectiveRadioGroup);
                objectiveRB = (RadioButton) findViewById(objectiveRG.getCheckedRadioButtonId());
                String objective = objectiveRB.getText().toString();

                ((AccelSensorEventListener) sensorEventListener).saveInfo(gender, age, objective);
                saveFlag = Boolean.TRUE;
            }
        });

        startBtn = (Button) findViewById(R.id.StartButton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStart();
            }
        });

        stopBtn = (Button) findViewById(R.id.StopButton);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStop();
            }
        });

        //Assigning buttons with the tasks: pause, resume, and reset
        resetBtn = (Button) findViewById(R.id.ResetButton);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                genderRG.clearCheck();
                ageRG.clearCheck();
                objectiveRG.clearCheck();

                ((AccelSensorEventListener) sensorEventListener).zeroSteps();

                saveFlag = Boolean.FALSE;
            }
        });

//        pause = (Button) findViewById(R.id.pause);
//        pause.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onPause();
//            }
//        });

//        resume = (Button) findViewById(R.id.resume);
//        resume.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onResume();
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    // This method unregisters the accelerometer
//    public void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(sensorEventListener, accelSensor);
//    }
//
//    // This method checks to see if the application is resumed by user,
//    // if it is then the sensors are re-registered (listened to)
//    public void onResume() {
//        super.onResume();
//        sensorManager.registerListener(sensorEventListener, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
//    }

    public void onStart() {
        super.onStart();
        if (Boolean.TRUE.equals(saveFlag)) {
            //Accelerometer
            accelOutput = new TextView(getApplicationContext());
            // maxAccelOutput = new TextView(getApplicationContext());

            //Creating a action listener
            sensorEventListener = new AccelSensorEventListener(accelOutput);
            accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(sensorEventListener, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);

            //Adding the accelerometer output to layout
            graphLL = (LinearLayout) findViewById(R.id.GraphLL);
            graphLL.addView(accelOutput);
            // graphLL.addView(maxAccelOutput);

            //Graphing vectors on the line graph
            graph = new LineGraphView(getApplicationContext(),
                    100, Arrays.asList("x", "y", "z"));

            ((AccelSensorEventListener) sensorEventListener).setGraph(graph);
            graph = ((AccelSensorEventListener) sensorEventListener).getUpdatedGraph();
            graphLL.addView(graph);
        }
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorEventListener, accelSensor);
    }
}
