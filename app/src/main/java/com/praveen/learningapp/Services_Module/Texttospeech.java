package com.praveen.learningapp.Services_Module;

import android.media.MediaPlayer;
import android.util.Log;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.bytedeco.javacpp.Loader.getCacheDir;

public class Texttospeech {
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "0120befcc6874a02be6481eeb24963f8";
    // Replace below with your own service region
    private static String serviceRegion = "eastus";

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;

    public Texttospeech(){
        speechSubscriptionKey = "0120befcc6874a02be6481eeb24963f8";
        serviceRegion = "eastus";
        // Initialize speech synthesizer and its dependencies
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        assert(speechConfig != null);

        synthesizer = new SpeechSynthesizer(speechConfig);
        assert(synthesizer != null);
    }

    public byte[] onSpeechButtonClicked(String toSpeak) {
        String res = "";
        byte[] arr= null;

        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event
            SpeechSynthesisResult result = synthesizer.SpeakText(toSpeak);
            assert(result != null);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                res =  "Speech synthesis succeeded.";
                arr = result.getAudioData();
//                outputMessage.setText("Speech synthesis succeeded.");
            }

            else if (result.getReason() == ResultReason.Canceled) {
                String cancellationDetails =
                        SpeechSynthesisCancellationDetails.fromResult(result).toString();

                res = "Error synthesizing. Error detail: " +
                        System.lineSeparator() + cancellationDetails +
                        System.lineSeparator() + "Did you update the subscription info?";
            }

            result.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
        }

        return arr;
    }

    private MediaPlayer mediaPlayer = new MediaPlayer();
    public void playMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("temp", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
//            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }


}
