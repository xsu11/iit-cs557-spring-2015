package ca.uwaterloo.lab2_201_24;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ca.uwaterloo.sensortoy.AccelSensorEventListener;
import ca.uwaterloo.sensortoy.LineGraphView;
import ca.uwaterloo.util.FileUtil;
import ca.uwaterloo.util.WekaTest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	// App start flag
	public static int flag = 0;

	// Sensor variables
	public SensorManager sensorManager;
	public Sensor accelSensor;
	public SensorEventListener sensorEventListener;
	public static TextView accelOutput;

	// Button variables
	public static Button pause;
	public static Button resume;

	// Graph variables
	LinearLayout ll;
	LineGraphView graph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ll = (LinearLayout) findViewById(R.id.ButtonLL);

		// Assigning a sensor manger to sensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Accelerometer
		accelOutput = new TextView(getApplicationContext());

		// Creating an action listener
		sensorEventListener = new AccelSensorEventListener(accelOutput);

		// Set up linear accelerator
		accelSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		// Graphing vectors on the line graph
		graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList(
				"x", "y", "z"));

		((AccelSensorEventListener) sensorEventListener).setGraph(graph);

		graph = ((AccelSensorEventListener) sensorEventListener)
				.getUpdatedGraph();

		ll.addView(graph);

		// Adding the accelerometer output to layout
		ll.addView(accelOutput);

		// Assigning buttons with the tasks: pause, resume, and reset
		// reset = (Button) findViewById(R.id.resetSteps);
		// reset.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// ((AccelSensorEventListener) sensorEventListener).zeroSteps();
		// }
		// });

		// Set Stop onClick event
		pause = (Button) findViewById(R.id.pause);
		pause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onPause();
			}
		});

		// Set Start onClick event
		resume = (Button) findViewById(R.id.resume);
		resume.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onResume();
			}
		});
	}

	@Override
	// This method unregisters the accelerometer
	public void onPause() {
		// When the pause button is pressed, unregister the sensor listener
		// and then process the data
		sensorManager.unregisterListener(sensorEventListener, accelSensor);

		int dc = ((AccelSensorEventListener) sensorEventListener)
				.getDataCount();

		if (dc < WekaTest.NUM_POINT + 1) {
			try {
				FileUtil.writeSdcardFile(FileUtil.PATH, FileUtil.LOG_FILENAME,
						"Sample points not enough!\n");
				accelOutput.setText(accelOutput.getText().toString()
						+ "\nSample points not enough!\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			WekaTest weka = new WekaTest();

			double result = 100.0f;
			String resultStr = null;

			try {
				// process data
				result = weka.processData();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (result == 1.0f) {
				// test result is TRUE
				resultStr = "TRUE";
			} else if (result == 0.0f) {
				// test result is FALSE
				resultStr = "FALSE";
			} else if (result == -10.0f) {
				// still in training phase
				resultStr = "TRAINING DATA " + WekaTest.getTrainingCount()
						+ " - TRUE";
			} else if (result == -20.0f) {
				// still in training phase
				resultStr = "TRAINING DATA " + WekaTest.getTrainingCount()
						+ " - FALSE";
			} else {
				// error occurs
				try {
					FileUtil.writeSdcardFile(FileUtil.PATH,
							FileUtil.LOG_FILENAME,
							"Error of the result of processData" + result
									+ "\n");
					accelOutput.setText(accelOutput.getText().toString()
							+ "\nError in the result of processData: " + result
							+ "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Write result to the screen
			accelOutput.setText(accelOutput.getText().toString() + "\nResult: "
					+ resultStr + "\n");
		}

		// After the process of the data, delete test_data file
		FileUtil.deleteSdcardFile(FileUtil.PATH, FileUtil.TEST_FILENAME);

		// Enable the start button and disable the stop button
		resume.setEnabled(true);
		pause.setEnabled(false);

		// Reset the dataCount that counts the sample points
		((AccelSensorEventListener) sensorEventListener).setDataCount(0);

		super.onPause();
	}

	@Override
	// This method checks to see if the application is resumed by user,
	// if it is then the sensors are re-registered (listened to)
	public void onResume() {
		super.onResume();

		// Create train_data and test_data file if they don't exist
		File testFile = new File(FileUtil.PATH + "/" + FileUtil.TEST_FILENAME);
		File trainingFile = new File(FileUtil.PATH + "/"
				+ FileUtil.TRAINING_FILENAME);

		if (!testFile.exists()) {
			FileUtil.writeArffFileHeader(FileUtil.PATH, FileUtil.TEST_FILENAME,
					WekaTest.NUM_POINT + 1);
		}

		if (!trainingFile.exists()) {
			FileUtil.writeArffFileHeader(FileUtil.PATH,
					FileUtil.TRAINING_FILENAME, WekaTest.NUM_POINT + 1);
		}

		// For the first time the app starts, do not register the sensor
		// listener
		// If The start button is pressed, then register the sensor listener
		if (flag == 1) {
			sensorManager.registerListener(sensorEventListener, accelSensor,
					SensorManager.SENSOR_DELAY_GAME);

			// Enable the stop button and disable the start button
			resume.setEnabled(false);
			pause.setEnabled(true);
		}

		flag = 1;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	}
}
