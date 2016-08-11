package ca.uwaterloo.sensortoy;

import java.io.IOException;
import java.lang.Math;

import ca.uwaterloo.util.FileUtil;
import ca.uwaterloo.util.WekaTest;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class AccelSensorEventListener implements SensorEventListener {

	// Global variables

	private TextView output;
	private float[] values = { 0, 0, 0 };
	private float[] samplePoints = new float[WekaTest.NUM_POINT];

	// Totally 192 features
	private float[] features = new float[WekaTest.NUM_POINT + 1];
	private LineGraphView graph;

	// Count the sample points
	private int dataCount = 0;

	// Low pass filter variables
	private float previousLowValue = 0;
	// private float previousHighValue = 0;

	// New vector which is the vector sum of x,y, and z
	private float vectorSum;

	// First stage for state machine
	private int stage = 1;

	// State machine variables
	private int iterator = 0;
	private float high = 0;

	// Constructor takes in a reference to the text view to allow changes
	public AccelSensorEventListener(TextView accelOutput) {
		output = accelOutput;
	}

	// Returns values
	public float[] getValues() {
		return values;
	}

	// Set up graph
	public void setGraph(LineGraphView graphIn) {
		graph = graphIn;
	}

	// Update graph
	public LineGraphView getUpdatedGraph() {
		return graph;
	}

	// Low pass filter uses the previous accel Field and the current accel
	// variable.
	public float lowpass(float in) {
		float out = previousLowValue;
		// previousHighValue = previousLowValue;
		float l = 0.24f;

		out = l * in + (1 - l) * out;
		previousLowValue = out;
		return out;
	}

	@Override
	public void onAccuracyChanged(Sensor s, int i) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Summation of vectors x,y, and z
		vectorSum = (float) Math
				.pow((Math.pow(event.values[0], 2)
						+ Math.pow(event.values[1], 2) + Math.pow(
						event.values[2], 2)), 1);

		// values = se.values;
		values[0] = lowpass(vectorSum);

		// This is to get rid of the y and z values on the graph
		values[1] = 0;
		values[2] = 0;

		graph.addPoint(values);

		// State machine
		// Stage 2, this stage check to see if vectorSum is between 8 and 25.

		if ((vectorSum > 8 && vectorSum < 25) || stage == 2) {
			stage = 2;

			// if high is less than vectorSum, lets high equal vectorSum
			// else, moves to stage 3
			if (vectorSum > high) {
				high = vectorSum;
			} else {
				stage = 3;
			}
		}

		// Stage 3, the data must drop to below 18% of the max in stage 2 for
		// at least 18 iterations.
		// This is to stop one from simply shaking the device to generate steps.
		if (vectorSum < high * 0.18 && stage == 3) {
			iterator++;
		} else if (iterator > 18) {
			iterator = 0;
		}

		// If the value in stage 3 stays below 18% of max for a sufficient time
		// then we count the step and set stage back to 1.
		if (iterator == 18) {
			iterator = 0;
			stage = 1;
		}
		// End of state machine

		/*
		 * if ("".equals(FILENAME)) { // Get file name Time t = new Time();
		 * t.setToNow(); int year = t.year; int month = t.month; int date =
		 * t.monthDay; int hour = t.hour; // 0-23 int minute = t.minute; int
		 * second = t.second;
		 * 
		 * FILENAME = "/" + year + "-" + month + "-" + date + "_" + hour + "-" +
		 * minute + "-" + second + ".txt"; }
		 */

		// features[0] ~ features[188]
		if (dataCount < WekaTest.NUM_POINT) {
			samplePoints[dataCount] = values[0];
			if (dataCount > 0) {
				features[dataCount - 1] = samplePoints[dataCount]
						- samplePoints[dataCount - 1];
			}

			dataCount++;
		} else if (dataCount == WekaTest.NUM_POINT) {
			// Calculate mean of the samplePoints
			float sum = 0;
			for (int i = 0; i < WekaTest.NUM_POINT; i++) {
				sum += samplePoints[i];
			}

			float mean = sum / WekaTest.NUM_POINT;

			// features[189]
			features[dataCount - 1] = mean;

			// Calculate standard deviation
			float divSum = 0;
			for (int i = 0; i < WekaTest.NUM_POINT; i++) {
				divSum += Math.pow(samplePoints[i] - mean, 2);
			}

			float sd = (float) Math.sqrt((divSum / (float) WekaTest.NUM_POINT));

			// features[190]
			features[dataCount] = sd;

			// Get string of features to wait to write to file
			String featureStr = "";
			int i;
			for (i = 0; i < WekaTest.NUM_POINT + 1; i++) {
				featureStr += features[i] + ",";
			}
			featureStr += "1\n";

			// Write to test file
			try {
				// TODO - what to write in result column?
				FileUtil.writeSdcardFile(FileUtil.PATH, FileUtil.TEST_FILENAME,
						featureStr);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// reset related global variables
			resetSamplePoints();
			resetFeatures();

			// increment dataCount to get it out of boundary and stop recording
			// sample points
			dataCount++;
		}

		// Displaying data
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			output.setText(String
					.format("\nAcceleration: \nx_Vector: (%f)\ny_Vector: (%f)\nz_Vector: (%f)\n",
							event.values[0], event.values[1], event.values[2]));
			output.setTextColor(Color.BLACK);
		}
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int newDataCount) {
		dataCount = newDataCount;
	}

	public void resetFeatures() {
		for (int i = 0; i < WekaTest.NUM_POINT + 1; i++) {
			features[i] = 0.0f;
		}
	}

	public void resetSamplePoints() {
		for (int i = 0; i < WekaTest.NUM_POINT; i++) {
			samplePoints[i] = 0.0f;
		}
	}
}
