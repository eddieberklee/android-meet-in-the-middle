package com.compscieddy.meetinthemiddle;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ambar on 6/17/16.
 */
public class ActivityRecognitionService extends IntentService {

  private static final Lawg lawg = Lawg.newInstance(ActivityRecognition.class.getSimpleName());
  private File mFile;
  final String mFilename = "activity_log.txt";
  private String mFilePath;

  public ActivityRecognitionService() {
    this("ActivityRecognitionService");
  }

  public ActivityRecognitionService(String name) {
    super(name);
  }

  private void appendToFile(String line) {
    FileOutputStream outputStream;
    try {
      outputStream = openFileOutput(mFilePath, Context.MODE_APPEND);
      outputStream.write(line.getBytes());
      outputStream.close();
    } catch (Exception e) {
      lawg.e("Error while trying to write a line to a text file in ActivityRecognitionService:writeToFile()");
      e.printStackTrace();
    }
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    init();
    if (ActivityRecognitionResult.hasResult(intent)) {
      ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
      handleDetectedActivities(result.getProbableActivities());
    }
  }

  private void init() {
    mFilePath = getApplicationContext().getExternalFilesDir(null) + mFilename;
    lawg.d("mFilePath: " + mFilePath);
    lawg.d("init " + getApplicationContext().getExternalFilesDir(null) + " filename: " + mFilename);
    mFile = new File(getApplicationContext().getExternalFilesDir(null), mFilename);
  }

  private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    String timestampLogTitle = dateFormat.format(date);

    for( DetectedActivity activity : probableActivities ) {
      int activityType = activity.getType();
      switch(activityType) {
        case DetectedActivity.IN_VEHICLE: {
          lawg.d( "ActivityRecogition - In Vehicle: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Vehicle " + activity.getConfidence());
          break;
        }
        case DetectedActivity.ON_BICYCLE: {
          lawg.d( "ActivityRecogition - On Bicycle: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Bicycle " + activity.getConfidence());
          break;
        }
        case DetectedActivity.ON_FOOT: {
          lawg.d( "ActivityRecogition - On Foot: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "On Foot " + activity.getConfidence());
          break;
        }
        case DetectedActivity.RUNNING: {
          lawg.d( "ActivityRecogition - Running: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Running " + activity.getConfidence());
          break;
        }
        case DetectedActivity.STILL: {
          lawg.d( "ActivityRecogition - Still: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Still " + activity.getConfidence());
          break;
        }
        case DetectedActivity.TILTING: {
          lawg.d( "ActivityRecogition - Tilting: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Tilting " + activity.getConfidence());
          break;
        }
        case DetectedActivity.WALKING: {
          lawg.d( "ActivityRecogition - Walking: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Walking " + activity.getConfidence());
          break;
        }
        case DetectedActivity.UNKNOWN: {
          lawg.d( "ActivityRecogition - Unknown: " + activity.getConfidence() );
          appendToFile(timestampLogTitle + "Unknown " + activity.getConfidence());
          break;
        }
      }
    }
  }
}
