package com.praveen.learningapp.Settings_Module;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.praveen.learningapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mContext = this.getApplicationContext();

        PACKAGE_NAME = getApplicationContext().getApplicationInfo();
        mContext = this.getApplicationContext();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);

            ListPreference face_rec_faces_included = findPreference("pref_no_of_faces");
            ListPreference no_face_warning = findPreference("pref_warning_no_face");
            ListPreference multiple_face_warning = findPreference("pref_warning_number_of_face");



            //======================================================================================
            // For Share App
            PreferenceScreen preferenceShare = findPreference("shareapp");
            preferenceShare.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    share();
                    return true;
                }
            });


            PreferenceScreen preferenceSharelink = findPreference("shareappLink");
            preferenceSharelink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    shareViaLink();
                    return true;
                }
            });

            //=====================================Help Page========================================

            PreferenceScreen preferenceHelp = findPreference("pref_help");
            preferenceHelp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    helpPage();
                    return true;
                }
            });

            //=================================Reset================================================

            PreferenceScreen preferenceReset = findPreference("pref_reset");
            preferenceReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((ActivityManager)getActivity().getSystemService(ACTIVITY_SERVICE))
                            .clearApplicationUserData();
                    return true;
                }
            });


            //================================Information About Developer===========================
            //Information About Developer
            PreferenceScreen preferenceMailTO = findPreference("Developer");
            preferenceMailTO.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:dce.pks@gmail.com"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, "Send Feedback").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    return true;
                }
            });

            //======================================================================================
        }
    }

    public static class CameraSettingFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.camera_preferences, rootKey);
        }
    }

    //==============================================================================================

    private static Context mContext;
    public static ApplicationInfo PACKAGE_NAME;

    static  final void shareViaLink(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Learning App Mode App build by Praveen Kumar Sharma (Student of Delhi Technological University) : "+"https://praveensharma.cf/LearningApp");
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.getApplicationContext().startActivity(Intent.createChooser(sharingIntent, "Share app via").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }




    static void helpPage(){
        String url = "https://praveensharma.cf/LearningApp_Help";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);

    }

    //Getting apk File For Sharing
    static final void share(){
        ApplicationInfo app = mContext.getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        // Append file and send Intent
        File originalApk = new File(filePath);
        try {
            //Make new directory in new location
            File tempFile = new File(mContext.getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;

            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            tempFile = new File(tempFile.getPath() + "/" + mContext.getResources().getString(R.string.app_name)+" "+pInfo.versionName + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            Uri apkURI = FileProvider.getUriForFile(
                    mContext,
                    mContext.getApplicationContext()
                            .getPackageName() + ".provider", tempFile);

            //Open share dialog
            intent.putExtra(Intent.EXTRA_STREAM, apkURI);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.getApplicationContext().startActivity(Intent.createChooser(intent, "Share app via").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }




}
