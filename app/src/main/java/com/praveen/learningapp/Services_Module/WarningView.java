package com.praveen.learningapp.Services_Module;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.praveen.learningapp.R;

import static com.praveen.learningapp.Activities.ExamActivity.stop;
import static com.praveen.learningapp.Test_Module.TestWebActivity.warningcount;



public class WarningView extends Service implements View.OnTouchListener {

    private WindowManager mWindowManager;
    private View mFloatingView;
    Thread thread;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    ImageView warning;
    private TextView warncount;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startBackgroundThread();

        thread = new Thread() {

            @Override
            public void run() {
                while (!thread.isInterrupted()) {
                    if (stop==true){
                        thread.interrupt();
                        stopSelf();
                    }
                }
            }
        };

        thread.start();

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_warning_view, null);
        warncount = mFloatingView.findViewById(R.id.warningcount);
        warncount.setText("Warning : "+warningcount+"/3");




        //setting the layout parameters
        final WindowManager.LayoutParams params;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
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

        warning = mFloatingView.findViewById(R.id.collapsed_iv);
        warning.setOnTouchListener(this);




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handledHere;

        final int action = ev.getAction();

        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN :

            case MotionEvent.ACTION_UP :
                int touchColor = getHotspotColor (R.id.image_areas, evX, evY);

                int tolerance = 10;

                if (closeMatch (Color.RED, touchColor, tolerance)){
//                    timer = 0;
                    stopSelf();
                }

                handledHere = false;
                break;

            default:
                handledHere = false;
        } // end switch
        return handledHere;
    }

    public boolean closeMatch (int color1, int color2, int tolerance) {
        if ((int) Math.abs (Color.red (color1) - Color.red (color2)) > tolerance ) return false;
        return true;
    }

    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) mFloatingView.findViewById (hotspotId);
        if (img == null) {
            Log.d ("ImageAreasActivity", "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                Log.d ("ImageAreasActivity", "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }
}
