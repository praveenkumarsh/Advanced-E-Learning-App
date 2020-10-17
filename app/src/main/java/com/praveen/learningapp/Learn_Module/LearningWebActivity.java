package com.praveen.learningapp.Learn_Module;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.praveen.learningapp.R;
import com.praveen.learningapp.Chat_Module.GroupChatActivity;
import com.praveen.learningapp.Services_Module.Texttospeech;
import com.praveen.learningapp.Services_Module.Translate;

import java.io.IOException;

public class LearningWebActivity extends AppCompatActivity implements ClipboardManager.OnPrimaryClipChangedListener{

    WebView webView;
    String response;
    byte[] speech;
    boolean speechcheck = false;
    CharSequence pasteData;
    Translate translateRequest = new Translate();
    Texttospeech texttospeech = new Texttospeech();
    ImageButton grpChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learningweb);

        Intent intent = getIntent();
        String url = intent.getStringExtra("URL_test");
        final String group = intent.getStringExtra("Subject");

        grpChat = findViewById(R.id.btn_grp);

        translateRequest = new Translate();
        texttospeech = new Texttospeech();

        webView = findViewById(R.id.WebView);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient() {
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         webView.loadUrl(url.endsWith(".pdf")?("https://docs.google.com/viewer?url="+url):url);
                                         return true;
                                     }
                                 });


        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert clipBoard != null;
        clipBoard.addPrimaryClipChangedListener(this);

        webView.loadUrl(url.endsWith(".pdf")?("https://docs.google.com/viewer?url="+url):url);

        grpChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LearningWebActivity.this, GroupChatActivity.class);
                intent.putExtra("GroupName",group);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onPrimaryClipChanged() {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        pasteData = "";
        ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
        pasteData = item.getText();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LearningWebActivity.this);
        alertDialogBuilder.setTitle("Action Required");
        alertDialogBuilder.setMessage("What you want to perform");
        alertDialogBuilder.setPositiveButton("Dictate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SpeechTask() {
                    protected void onPostExecute(Boolean result) {
                        if(speechcheck==false) {
                            speechcheck = true;
                            texttospeech.playMp3(speech);
                        }else{

                        }
                    }
                }.execute();
            }
        });
        alertDialogBuilder.setNegativeButton("Convert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new ConvertTask() {
                    protected void onPostExecute(Boolean result) {
                        String transs = translateRequest.prettify(response);
                        alertDialogBuilder.setTitle("Translated Text");
                        alertDialogBuilder.setMessage(pasteData+": "+transs);
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
                            }
                        });
                        alertDialogBuilder.create();
                        alertDialogBuilder.show();

                    }
                }.execute();
            }
        });
        alertDialogBuilder.create();
        alertDialogBuilder.show();

    }

    class ConvertTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            response = null;

            try {
                response = translateRequest.Post((String) pasteData);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    class SpeechTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            speech = null;
            speechcheck = false;
            speech = texttospeech.onSpeechButtonClicked((String) pasteData);

            return true;
        }
    }
}


