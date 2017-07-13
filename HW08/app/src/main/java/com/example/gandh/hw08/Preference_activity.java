package com.example.gandh.hw08;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by gandh on 4/5/2017.
 */

public class Preference_activity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        prefs = getSharedPreferences("current_city", MODE_PRIVATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
     void alert_box_current_city()
    {
        RelativeLayout l1 = new RelativeLayout(this);
        final EditText editText_city = new EditText(this);
        editText_city.setId(View.generateViewId());
        final EditText editText_country = new EditText(this);
        editText_city.setText(prefs.getString("city","Enter your city"));
        editText_country.setText(prefs.getString("country","Enter your country"));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        l1.setLayoutParams(layoutParams);

        lp2.addRule(RelativeLayout.BELOW,editText_city.getId());

        l1.addView(editText_city,lp);
        l1.addView(editText_country,lp2);

        new AlertDialog.Builder(this)
                .setTitle("Enter City Details")
                .setView(l1)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //weather_current_city_code(editText_country.getText().toString(),editText_city.getText().toString());

                        Intent ia = new Intent();
                        ia.putExtra("country",editText_country.getText().toString());
                        ia.putExtra("city",editText_city.getText().toString());
                        setResult(RESULT_OK,ia);
                        Log.d("democountry",editText_country.getText().toString());
                        finish();
                    }
                })
                .create().show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        String temp = sharedPrefs.getString("temp", "NA");
        Log.d("demotemp",temp);
        if(temp.equals("c"))
        {
            Intent ia = new Intent();
            ia.putExtra("temp",temp);
            setResult(100,ia);
            finish();
        }
        else if(temp.equals("f"))
        {
            Intent ia = new Intent();
            ia.putExtra("temp",temp);
            setResult(300,ia);
            finish();;
        }
    }

    public static   class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference myPre = (Preference) findPreference("temp");

            Preference myPref = (Preference) findPreference("change_city");
            if(((Preference_activity)getActivity()).prefs.getString("city",null)!=null)
            myPref.setSummary("Update Current city");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public boolean onPreferenceClick(Preference preference) {
                   // PreferenceActivity pref = new Preference_activity();
                    ((Preference_activity)getActivity()).alert_box_current_city();
                    return false;
                }
            });
        }
    }

}
