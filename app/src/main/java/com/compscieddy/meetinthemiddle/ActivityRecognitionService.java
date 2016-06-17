package com.compscieddy.meetinthemiddle;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by ambar on 6/17/16.
 */
public class ActivityRecognitionService extends IntentService {

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
          Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.ON_BICYCLE: {
          Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.ON_FOOT: {
          Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.RUNNING: {
          Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.STILL: {
          Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.TILTING: {
          Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.WALKING: {
          Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
          break;
        }
        case DetectedActivity.UNKNOWN: {
          Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
          break;
        }
      }
    }
  }
}
