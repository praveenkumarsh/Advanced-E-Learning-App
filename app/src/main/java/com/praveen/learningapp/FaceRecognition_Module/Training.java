package com.praveen.learningapp.FaceRecognition_Module;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.praveen.learningapp.R;
import com.praveen.learningapp.Activities.ExamActivity;
import com.praveen.learningapp.Test_Module.TestWebActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.praveen.learningapp.FaceRecognition_Module.Methods.PHOTOS_TRAIN_QTY;
import static com.praveen.learningapp.FaceRecognition_Module.Methods.reset;
import static com.praveen.learningapp.FaceRecognition_Module.training_Main.currentFace;


public class Training extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    Button btnTraining;
    static String name,testname;
    ToggleButton capture;
    int totalFaces;
    ProgressBar progressTrain;
    Boolean trained = false;

    //==============================================================================================
    //Face Training Module
    int totalPics = PHOTOS_TRAIN_QTY;
    int startPics = 1;
    private static final String TAG = "TrainActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private CascadeClassifier mFaceDetector;
    private File mCascadeFile;
    private Mat mRgba, mGray;
    private int mAbsoluteFaceSize = 0;
    private boolean takePhoto;
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


    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    //==============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startPics  = 1;
        reset();
        Toast.makeText(getApplicationContext(),"Reset Face Data",Toast.LENGTH_SHORT).show();



        btnTraining = findViewById(R.id.btnTrain);
        progressTrain = findViewById(R.id.ProgressTrain);
        mHandler = new Handler();

        Intent intent = getIntent();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        name = user.getUid();
        testname = intent.getStringExtra("testname");

        totalFaces = Integer.parseInt("1");

        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                train();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        if (currentFace<totalFaces){
//                            currentFace++;
//
//                            Toast.makeText(getApplicationContext(),"Trained Face for "+name,Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(Training.this,training_Main.class));
//
//                        }else{
//                            currentFace = 1;
//                            Toast.makeText(getApplicationContext(),"Trained Face for "+name,Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(Training.this,MainActivity.class));
//
//                        }
//                    }
//                }, 10000);   //10 seconds


            }
        });


        capture = (ToggleButton) findViewById(R.id.capture);
        capture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                captureOnClick();
            }
        });

        findViewById(R.id.resetCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPics  = 1;
                reset();
                Toast.makeText(getApplicationContext(),"Reset Face Data",Toast.LENGTH_SHORT).show();
            }
        });

        //==========================================================================================
        //Face Training Module
        mOpenCvCameraView = findViewById(R.id.CameraTrainingView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCameraIndex(mCameraId);
        mOpenCvCameraView.setCvCameraViewListener(this);

        ToggleButton mFlipCamera = findViewById(R.id.toggle_camera);
        mFlipCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCameraId = 0;
                    mOpenCvCameraView.disableView();
                    mOpenCvCameraView.setCameraIndex(mCameraId);
                    mOpenCvCameraView.enableView();
                } else {
                    mCameraId = 1;
                    mOpenCvCameraView.disableView();
                    mOpenCvCameraView.setCameraIndex(1);
                    mOpenCvCameraView.enableView();
                }
            }
        });



        //==========================================================================================
    }

    void captureOnClick()
    {
        if (capture.isChecked()) {
            Toast.makeText(this, "Capturing", Toast.LENGTH_SHORT).show();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        capturepics();
                    }catch (Exception e) {
                        Log.d(TAG, e.getLocalizedMessage(), e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {
                        if (Methods.isTrained()) {
                            Toast.makeText(Training.this, "Training successful", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Training.this, "Training unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        Log.d(TAG, e.getLocalizedMessage(), e);
                    }
                }
            }.execute();
        }else {
            Toast.makeText(this, "Captured", Toast.LENGTH_SHORT).show();

        }
    }

    public void capturepics(){
        startRepeatingTask();
        if (startPics>totalPics){
            capture.setChecked(false);
            stopRepeatingTask();
        }

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                takePhoto = true;
                startPics++;
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private int mInterval = 20; // 0.2 seconds by default, can be changed later
    private Handler mHandler;

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

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

        Mat rotImage;
        if (mCameraId==0){
            rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
                    mRgba.rows() / 2), 270, 1.0);
        }else{
            rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
                    mRgba.rows() / 2), 90, 1.0);
        }

//        Mat rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
//                mRgba.rows() / 2), 90, 1.0);

        Imgproc.warpAffine(mRgba, mRgba, rotImage, mRgba.size());

        Imgproc.warpAffine(mGray, mGray, rotImage, mRgba.size());


//        Computing absolute face size
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
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }else {
            Log.e(TAG, "Detection is not selected!");
        }

        //Drawing rectangle around detected face
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
        }

        //If one face is detected and Capture is pressed, method capturePhoto and alertRemainingPhotos are executed
        if (facesArray.length == 1) {
            if (takePhoto) {
                capturePhoto(mRgba);
                alertRemainingPhotos();
            }
        }
        return mRgba;
    }


    //Method for capturing photos
    private void capturePhoto(Mat rgbaMat) {
        try {
            Methods.takePhoto(Methods.numPhotos() + 1, rgbaMat.clone(), mFaceDetector);
        }catch (Exception e) {
            e.printStackTrace();
        }
        takePhoto = false;
    }

    //Method for training which is executed by pressing Train
    private void train() {
        int remainingPhotos = PHOTOS_TRAIN_QTY - Methods.numPhotos();
        if (remainingPhotos > 0) {
            Toast.makeText(this, "You need " + remainingPhotos + " more photo(s)", Toast.LENGTH_SHORT).show();
            return;
        }else if (Methods.isTrained()) {
            Toast.makeText(this, "Already trained", Toast.LENGTH_SHORT).show();
            return;
        }

        progressTrain.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Training started", Toast.LENGTH_SHORT).show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (!Methods.isTrained()) {
                        Methods.train();
                    }
                }catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    trained = true;
                    progressTrain.setVisibility(View.INVISIBLE);
                    if (Methods.isTrained()) {

                        Toast.makeText(Training.this, "Training successful", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Training.this, "Training unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                    if (currentFace<totalFaces){
                        currentFace++;

                        Toast.makeText(getApplicationContext(),"Trained Face for "+name,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Training.this,training_Main.class));

                    }else{
                        currentFace = 1;
                        Toast.makeText(getApplicationContext(),"Trained Face for "+name,Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(Training.this, TestWebActivity.class);
                                intent.putExtra("testname",testname);
                                startActivity(intent);
                                // yourMethod();
                            }
                        }, 5000);   //5 seconds

//                        startActivity(new Intent(Training.this, ExamActivity.class));

                    }
                }catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
            }
        }.execute();
    }

    //Method for displaying remaining number of photos
    private void alertRemainingPhotos() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int remainingPhotos = PHOTOS_TRAIN_QTY - Methods.numPhotos();
                if (remainingPhotos > 0) {
                    Toast.makeText(getBaseContext(), "You need " + remainingPhotos + " more photo(s)", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Training.this, "You took max number of photos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
