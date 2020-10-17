package com.praveen.learningapp.Services_Module;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.praveen.learningapp.R;
import com.praveen.learningapp.Activities.ExamActivity;
import com.praveen.learningapp.FaceRecognition_Module.Methods;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.praveen.learningapp.MainActivity.settingControl;
import static com.praveen.learningapp.Activities.ExamActivity.stop;
import static com.praveen.learningapp.FaceRecognition_Module.Methods.THRESHOLD;
import static com.praveen.learningapp.Test_Module.TestWebActivity.isForground;
import static com.praveen.learningapp.Test_Module.TestWebActivity.prevBackStatus;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import static com.praveen.learningapp.Test_Module.TestWebActivity.warningcount;

public class CameraViewService extends Service implements View.OnClickListener {

    static boolean noFaceDetected = false;
    static boolean selfFaceDetected = false;


    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;

    private TextureView cameraView;
    private CameraBridgeViewBase mOpenCvCameraView;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId = "1";
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;


    Thread thread;
    static boolean spying = false;
    static boolean spyingWindowDisplay = false;

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static double timer = 0;



    public CameraViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mFaceDetectionMatrix = new Matrix();
        mFaceDetectionMatrix.setRotate(90);
        startBackgroundThread();

        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {

                        Thread.sleep(1000);
                        if (noFaceDetected){
                            timer++;
                            if (Integer.parseInt(settingControl.getTimerNoFaceCapturing())!=0){
                                if (timer==Integer.parseInt(settingControl.getTimerNoFaceCapturing())){
                                    warningcount++;
                                    startService(new Intent(CameraViewService.this, WarningView.class));
                                }
                            }

                        }else{
                            timer = 0;
                        }
                        if (spying==true){
                            if (spyingWindowDisplay==false){
                                if (Integer.parseInt(settingControl.getTimerManyFaceCapturing())!=0) {
                                    spyingWindowDisplay = true;
                                    warningcount++;
                                    startService(new Intent(CameraViewService.this, WarningView.class));
                                }
                            }
                        }else{
                        }

                        if (stop==true){
                            thread.interrupt();
                            stopSelf();
                        }

                        if (!isForground&&prevBackStatus){
                            warningcount++;
                            prevBackStatus=false;
                            Log.d("cc1","In Background :");
//                                    Toast.makeText(getApplicationContext(), "You are not alone", Toast.LENGTH_SHORT).show();
                            startService(new Intent(CameraViewService.this, WarningView.class));
                        }

                        if (warningcount>3){
                            stop = true;
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());


                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    stop = false;

                                    Toast.makeText(getApplicationContext(),"Stopped, Test Auto Submitted Due to Maximum Warning Reached",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), ExamActivity.class));
                                }
                            }, 5000);   //5 seconds
                        }





                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();



        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_camera_view, null);

        //setting the layout parameters
        final WindowManager.LayoutParams params;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }else{
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }



        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        expandedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener(this);

        if (settingControl.getDisplayWidget()){
            collapsedView.setVisibility(View.VISIBLE);
        }else{
            expandedView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    expandedView.setVisibility(View.GONE);
                    collapsedView.setVisibility(View.GONE);
                }
            }, 5000);   //5 seconds
        }

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        cameraView = (TextureView) mFloatingView.findViewById(R.id.CameraTextureView);
        mOpenCvCameraView = mFloatingView.findViewById(R.id.CameraJavaView);

        expandedView.setLayoutParams(new RelativeLayout.LayoutParams(Integer.parseInt(settingControl.getCameraSize())*15, Integer.parseInt(settingControl.getCameraSize())*20));

        if (settingControl.getFaceRecognitionMode()){

            expandedView.setLayoutParams(new RelativeLayout.LayoutParams(30*15, 30*20));

            if (!OpenCVLoader.initDebug()) {
                Log.d("TAG", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
            }else {
                Log.d("TAG", "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }

            mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
            mOpenCvCameraView.setCameraIndex(Integer.parseInt(cameraId));
            mOpenCvCameraView.setCvCameraViewListener(cvCameraViewListener2);
            cameraView.setVisibility(View.INVISIBLE);

            //Loading trained classifier for face recognition from directory FacePics
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Methods.FACE_PICS);
                        File f = new File(folder, Methods.LBPH_CLASSIFIER);
                        Log.i(TAG, "Classifier = " + f);
                        mLBPHFaceRecognizer.read(f.getAbsolutePath());
                    }catch (Exception e) {
                        Log.d(TAG, e.getLocalizedMessage(), e);
                    }
                    return null;
                }
            }.execute();

        }else{
            mOpenCvCameraView.setVisibility(CameraBridgeViewBase.INVISIBLE);

            mOverlayView = (OverlayView) mFloatingView.findViewById(R.id.overlay_view);
            cameraView.setSurfaceTextureListener(textureListener);
        }


    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e("TAG", "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = cameraView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    try {
                        // Auto focus should be continuous for camera preview.
                        captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
                                CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL);
                        // Flash is automatically enabled when necessary.
//                        setAutoFlash(captureRequestBuilder);

                        // Finally, we start displaying the camera preview.
                        captureRequest = captureRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(captureRequest,
                                mCaptureCallback, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraViewService.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e("TAG", "is camera open");
        try {
//            cameraId = manager.getCameraIdList()[1];
            cameraId = settingControl.getCameraID();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "openCamera X");
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e("TAG", "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        closeCamera();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutExpanded:
                //switching views
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;

            case R.id.buttonClose:
                //closing the widget
                stopSelf();
                break;
        }
    }


    private OverlayView mOverlayView;
    private Matrix mFaceDetectionMatrix;
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            Integer mode = result.get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
            Face[] faces = result.get(CaptureResult.STATISTICS_FACES);
            Log.d("Test","Mode : "+mode+" | Faces : ");
            if (faces!=null){
                if (faces.length==0){
                    noFaceDetected = true;
                }else{
                    noFaceDetected = false;
//                    stopService(new Intent(CameraViewService.this,WarningView.class));
                }
//                Log.d("Test","Faces : "+faces.length);
//                Log.d("Test","No of faces needed : "+Integer.parseInt(settingControl.getTimerManyFaceCapturing()));
                if (Integer.parseInt(settingControl.getTimerManyFaceCapturing())!=0) {
                    if (faces.length >= Integer.parseInt(settingControl.getTimerManyFaceCapturing())) {
                        if (spying == true) {
                            Toast.makeText(getApplicationContext(), "You are not alone", Toast.LENGTH_SHORT).show();
                        } else {
                            spying = true;
                        }

                    } else {
                        spying = false;
                    }
                }
            }

            if (faces != null && mode != null) {
                if (faces.length > 0) {
                    for(int i = 0; i < faces.length; i++) {
                        if (faces[i].getScore() > 50) {
                            Log.d("Test", "faces : " + faces.length + " , mode : " + mode);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }




    };


    //==============================================================================================
    //Face Recognizer Module
    private static final String TAG = "FaceRecognizerActivity";
    private CascadeClassifier mFaceDetector;
    private File mCascadeFile;
    private Mat mRgba, mGray;
    private int mAbsoluteFaceSize = 0;
    private opencv_face.FaceRecognizer mLBPHFaceRecognizer = opencv_face.LBPHFaceRecognizer.create();
    private int mCameraId = 1;

    //Connection between app and OpenCV Manager
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                //Loading detection classifier from resources
                                InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved);
                                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                                mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");
                                FileOutputStream os = new FileOutputStream(mCascadeFile);

                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = is.read(buffer)) != -1) {
                                    os.write(buffer, 0, bytesRead);
                                }
                                is.close();
                                os.close();

                                mFaceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                                if (mFaceDetector.empty()) {
                                    Log.e(TAG, "Failed to load cascade classfier");
                                    mFaceDetector = null;
                                }else {
                                    Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                                }
                                cascadeDir.delete();

                            }catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                            }
                            return null;
                        }
                    }.execute();

                    mOpenCvCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };


    CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {
        @Override
        public void onCameraViewStarted(int width, int height) {
            mGray = new Mat();
            mRgba = new Mat();
        }

        @Override
        public void onCameraViewStopped() {
            mGray.release();
            mRgba.release();
        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();


            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();

            Mat rotImage;
            if (cameraId.equals("0")){
                rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
                        mRgba.rows() / 2), 270, 1.0);
            }else{
                rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
                        mRgba.rows() / 2), 90, 1.0);
            }



            Imgproc.warpAffine(mRgba, mRgba, rotImage, mRgba.size());

            Imgproc.warpAffine(mGray, mGray, rotImage, mRgba.size());

            //Computing absolute face size
            if (mAbsoluteFaceSize == 0) {
                int height = mGray.rows();
                float mRelativeFaceSize = 0.2f;
                if (Math.round(height * mRelativeFaceSize) > 0) {
                    mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
                }
            }

            MatOfRect faces = new MatOfRect();

            //Using detection classifier
            if (mFaceDetector != null) {
                mFaceDetector.detectMultiScale(mGray, faces, 1.1, 5, 2,
                        new org.opencv.core.Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new org.opencv.core.Size());
            }else {
                Log.e(TAG, "Detection is not selected!");
            }


            //Drawing rectangle around detected face
            org.opencv.core.Rect[] facesArray = faces.toArray();

            if (facesArray.length==0){
                noFaceDetected = true;
            }else{
                noFaceDetected = false;
//                    stopService(new Intent(CameraViewService.this,WarningView.class));
            }

            if (facesArray.length>1){
                if (spying == true) {
                } else {
                    spying = true;
                }

            } else {
                spying = false;

            }


            for (int i = 0; i < facesArray.length; i++) {
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
            }

            //If one face is detected, face prediction is executed
            if (facesArray.length == 1) {
                try {
                    //Conversion from OpenCV Mat to JavaCV Mat
                    opencv_core.Mat javaCvMat = new opencv_core.Mat((Pointer) null) {{address = mGray.getNativeObjAddr();}};
                    //Picture resizing
                    resize(javaCvMat, javaCvMat, new opencv_core.Size(Methods.IMG_WIDTH, Methods.IMG_HEIGHT));
                    //Histogram equalizing
                    equalizeHist(javaCvMat, javaCvMat);

                    IntPointer label = new IntPointer(1);
                    DoublePointer confidence = new DoublePointer(1);
                    mLBPHFaceRecognizer.predict(javaCvMat, label, confidence);

                    int predictedLabel = label.get(0);
                    double acceptanceLevel = confidence.get(0);
                    String name;
                    Log.d(TAG, "Prediction completed, predictedLabel: " + predictedLabel + ", acceptanceLevel: " + acceptanceLevel);
                    if (predictedLabel == -1 || acceptanceLevel >= THRESHOLD) {
                        name = "-";
                        noFaceDetected = true;
                        selfFaceDetected = false;
                    } else {
                        noFaceDetected = false;
                        spying = false;
//                        selfFaceDetected = true;
                        name = Integer.toString(predictedLabel);
                    }

                    //The result of face recognition
                    for (Rect face : facesArray) {
                        int posX = (int) Math.max(face.tl().x - 10, 0);
                        int posY = (int) Math.max(face.tl().y - 10, 0);
                        if (name.equals("-")){
                            Imgproc.putText(mRgba, "Waiting", new Point(posX, posY),
                                    Core.FONT_HERSHEY_TRIPLEX, 0.5, new Scalar(0, 255, 255, 0));
                        }else{
                            Imgproc.putText(mRgba, "Recognized", new Point(posX, posY),
                                    Core.FONT_HERSHEY_TRIPLEX, 0.5, new Scalar(0, 255, 0, 255));
                        }

                    }
                }catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
            }
            return mRgba;
        }
    };




}