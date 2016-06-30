package com.compscieddy.meetinthemiddle;

import android.app.IntentService;
import android.content.Intent;

import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by ambar on 6/17/16.
 */
public class ActivityRecognitionService extends IntentService {

  private static final Lawg lawg = Lawg.newInstance(ActivityRecognition.class.getSimpleName());

  public ActivityRecognitionService() {
    super("ActivityRecognitionService");
  }

  public ActivityRecognitionService(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (ActivityRecognitionResult.hasResult(intent)) {
      ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
      handleDetectedActivities(result.getProbableActivities());
    }
  }

  private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
    for( DetectedActivity activity : probableActivities ) {
      switch( activity.getType() ) {
        case DetectedActivity.IN_VEHICLE: {
          lawg.d( "ActivityRecogition - In Vehicle: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.ON_BICYCLE: {
          lawg.d( "ActivityRecogition - On Bicycle: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.ON_FOOT: {
          lawg.d( "ActivityRecogition - On Foot: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.RUNNING: {
          lawg.d( "ActivityRecogition - Running: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.STILL: {
          lawg.d( "ActivityRecogition - Still: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.TILTING: {
          lawg.d( "ActivityRecogition - Tilting: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.WALKING: {
          lawg.d( "ActivityRecogition - Walking: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.UNKNOWN: {
          lawg.d( "ActivityRecogition - Unknown: " + activity.getConfidence() );
          break;
        }
      }
    }
  }
}
