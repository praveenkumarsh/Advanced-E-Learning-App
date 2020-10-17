package com.praveen.learningapp.Settings_Module;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SettingsUtility {

    public static SettingsControl getOnDisplayControlSettings(Context mContext){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean FaceRecognitionMode = preferences.getBoolean("pref_face_recognition_mode",false);
        String NoOfFacesInFaceRecognition = preferences.getString("pref_no_of_faces","1");
        Boolean DisplayWidget = preferences.getBoolean("pref_display_widget",true);
        String TimerNoFaceCapturing = preferences.getString("pref_warning_no_face","10");
        String TimerManyFaceCapturing = preferences.getString("pref_warning_number_of_face","2");
        String CameraID = preferences.getString("pref_camera_location","1");
        Boolean KeepScreenOn = preferences.getBoolean("pref_keep_screen_on",false);
        String CameraSize = preferences.getString("pref_preview_size","10");

        return new SettingsControl(FaceRecognitionMode,NoOfFacesInFaceRecognition, DisplayWidget,TimerNoFaceCapturing,TimerManyFaceCapturing,CameraID,KeepScreenOn,CameraSize);
    }

    public static class SettingsControl {
        Boolean FaceRecognitionMode;
        String NoOfFacesInFaceRecognition;
        Boolean DisplayWidget;
        String TimerNoFaceCapturing;
        String TimerManyFaceCapturing;
        String CameraID;
        Boolean KeepScreenOn;
        String CameraSize;

        public SettingsControl(Boolean faceRecognitionMode, String noOfFacesInFaceRecognition, Boolean displayWidget, String timerNoFaceCapturing, String timerManyFaceCapturing, String cameraID, Boolean keepScreenOn, String cameraSize) {
            FaceRecognitionMode = faceRecognitionMode;
            NoOfFacesInFaceRecognition = noOfFacesInFaceRecognition;
            DisplayWidget = displayWidget;
            TimerNoFaceCapturing = timerNoFaceCapturing;
            TimerManyFaceCapturing = timerManyFaceCapturing;
            CameraID = cameraID;
            KeepScreenOn = keepScreenOn;
            CameraSize = cameraSize;
        }

        public Boolean getFaceRecognitionMode() {
            return FaceRecognitionMode;
        }

        public String getNoOfFacesInFaceRecognition() {
            return NoOfFacesInFaceRecognition;
        }

        public Boolean getDisplayWidget() {
            return DisplayWidget;
        }

        public String getTimerNoFaceCapturing() {
            return TimerNoFaceCapturing;
        }

        public String getTimerManyFaceCapturing() {
            return TimerManyFaceCapturing;
        }

        public String getCameraID() {
            return CameraID;
        }

        public Boolean getKeepScreenOn() {
            return KeepScreenOn;
        }

        public String getCameraSize() {
            return CameraSize;
        }
    }

}
