package com.example.gandh.hw08;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gandh on 4/5/2017.
 */

public class Weather_widget extends AppCompatActivity implements Adaptor_recycler.fromadaptor {
    Toolbar t;
    String city, country, key = "CcGVGr7vhPK3dDagCerm69PKuF1GvCrs",city_key1,headlinetext,extend_link;
    OkHttpClient client;
    TextView heading, headline, forecast, day_cond, night_cond, more_details, extend_details,temperature,temperature_c;
    ImageView day_img, night_img;
    RecyclerView rv;
    ProgressBar pb;
    ArrayList<weather_city> five_day_fcast;
    Adaptor_recycler adaptor;
    DatabaseReference mDatabase,child;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    boolean result_return =false;
    ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_city);
        view_matcher();
        setSupportActionBar(t);
        LinearLayoutManager gm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(gm);
        city = getIntent().getExtras().getString("city");
        country = getIntent().getExtras().getString("country");
        weather_current_city_code(country,city);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        child = mDatabase.child("saved_cities");
        editor = getSharedPreferences("current_city", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("current_city", MODE_PRIVATE);
        pd = new ProgressDialog(this);
        pd.setTitle("Loading Data");
        pd.show();
        pb.setVisibility(View.INVISIBLE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(sharedPrefs.getString("temp","na").equals("f"))
            temperature_c.setVisibility(View.INVISIBLE);
        else
            temperature.setVisibility(View.INVISIBLE);


    }

    void view_matcher()
    {
        pb = (ProgressBar) findViewById(R.id.pb3);
        day_img = (ImageView) findViewById(R.id.iv1);
        night_img = (ImageView) findViewById(R.id.iv2);
        t = (Toolbar) findViewById(R.id.tool_bar);
        heading= (TextView) findViewById(R.id.tv1);
        headline= (TextView) findViewById(R.id.tv2);
        forecast= (TextView) findViewById(R.id.tv3);
        temperature= (TextView) findViewById(R.id.tv4);
        temperature_c= (TextView) findViewById(R.id.tv4_dup);
        day_cond= (TextView) findViewById(R.id.tv5);
        night_cond= (TextView) findViewById(R.id.tv6);
        more_details= (TextView) findViewById(R.id.tv7);
        extend_details= (TextView) findViewById(R.id.tv8);
        rv = (RecyclerView) findViewById(R.id.rv2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this,Preference_activity.class);
                int reqcode = 400;
                startActivityForResult(intent,reqcode);
                break;

            case R.id.save:
                Map<String,Object> result = five_day_fcast.get(0).toMap();
                result.put("cityname",city);
                result.put("citykey",city_key1);
                result.put("country",country);
                String fbase_key =city_key1;
                Log.d("demo12",fbase_key);
                Map<String,Object> childupdate = new HashMap<>();

                if(!result_return)
                {
                    childupdate.put("/saved_cities/"+fbase_key,result);
                    mDatabase.updateChildren(childupdate);
                    five_day_fcast.get(0).setFbase_key(fbase_key);
                    child_exist_checker();
                    Toast.makeText(this,"City saved",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    childupdate.put("/saved_cities/"+fbase_key+"/temperature",five_day_fcast.get(0).getW_temp_min_c());
                    childupdate.put("/saved_cities/"+fbase_key+"/date",five_day_fcast.get(0).getCurrentTimeStamp());
                    mDatabase.updateChildren(childupdate);
                    child_exist_checker();
                    Toast.makeText(this,"City updated",Toast.LENGTH_SHORT).show();
                }


            break;

            case R.id.current:

                if(result_return)
            {
                editor.clear();
                editor.putString("city", city);
                editor.putString("country", country);
                editor.putString("city_key", city_key1);
                editor.commit();
                setResult(700);
                Toast.makeText(this,"current city details updated",Toast.LENGTH_SHORT).show();
            }
                else {
                    editor.clear();
                    editor.putString("city", city);
                    editor.putString("country", country);
                    editor.putString("city_key", city_key1);
                    setResult(700);
                    editor.commit();
                    Toast.makeText(this,"current city details saved",Toast.LENGTH_SHORT).show();

                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==400)
        {
            if(resultCode==RESULT_OK)
            {
                if(data.getExtras()!=null) {
//                    weather_current_city_code(data.getExtras().getString("country"), data.getExtras().getString("city"));
                    Log.d("democountry", data.getExtras().getString("country"));
                    Intent ia = new Intent();
                    ia.putExtra("country",data.getExtras().getString("country"));
                    ia.putExtra("city",data.getExtras().getString("city"));
                    setResult(RESULT_OK,ia);
                    finish();
                }
            }

            if(resultCode==100)
            {
                if(data.getExtras()!=null) {
                    temperature.setVisibility(View.INVISIBLE);
                    temperature_c.setVisibility(View.VISIBLE);
                    Toast.makeText(this,"Temperature unit has been changed from F to C",Toast.LENGTH_SHORT).show();
                }
            }

            if(resultCode==300)
            {
                if(data.getExtras()!=null) {
                    temperature.setVisibility(View.VISIBLE);
                    temperature_c.setVisibility(View.INVISIBLE);
                    Toast.makeText(this,"Temperature unit has been changed from C to F",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void weather_current_city_code(final String country_code, final String city_name)
    {

        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dataservice.accuweather.com/locations/v1/"+country_code+"/search?apikey="+key+"&q="+city_name)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s= response.body().string();

                try {
                    JSONArray array1 = new JSONArray(s);
                    JSONObject ob1 = array1.getJSONObject(0);
                    final  String city_key = ob1.getString("Key");
                    city_key1 =city_key;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // storedpref_setter(city_name, country_code, city_key);
                            weather_current_city_data_getter(country_code,city_name,city_key);

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Weather_widget.this,"Invalid city/country",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
            }
        });
    }

    void weather_current_city_data_getter(final String country_code, final String city_name, final String city_key)
    {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+city_key+"?apikey="+key)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.d("demo",s);
                try {

                    five_day_fcast = new ArrayList<weather_city>();
                    JSONObject ob1 = new JSONObject(s);
                    JSONObject ob2 = ob1.getJSONObject("Headline");
                     headlinetext = ob2.getString("Text");
                    extend_link = ob2.getString("MobileLink");

                    JSONArray ar1 = ob1.getJSONArray("DailyForecasts");
                    for(int i=0;i<ar1.length();i++)
                    {
                        JSONObject oblocal = ar1.getJSONObject(i);
                        weather_city weather_city = new weather_city();
                        weather_city.setW_date(oblocal.getString("Date"));

                        JSONObject ob3 = oblocal.getJSONObject("Temperature");
                        JSONObject ob4 = ob3.getJSONObject("Minimum");
                        weather_city.setW_temp_min_f(ob4.getString("Value"));
                        weather_city.setW_temp_min_c(String.format("%.2f",(Double.parseDouble(ob4.getString("Value"))-32)*0.55 ));
                        JSONObject ob5 = ob3.getJSONObject("Maximum");
                        weather_city.setW_temp_max_f(ob5.getString("Value"));
                        weather_city.setW_temp_max_c(String.format("%.2f",(Double.parseDouble(ob5.getString("Value"))-32)*0.55 ));

                        JSONObject ob6 = oblocal.getJSONObject("Day");
                        String image_day =  ob6.getString("Icon");
                        weather_city.setW_day_cond(ob6.getString("IconPhrase"));
                        if(image_day.length()==1)
                            weather_city.setDay_image("http://developer.accuweather.com/sites/default/files/0"+image_day+"-s.png");
                        else
                            weather_city.setDay_image("http://developer.accuweather.com/sites/default/files/"+image_day+"-s.png");

                        JSONObject ob7 = oblocal.getJSONObject("Night");
                        String image_night =  ob7.getString("Icon");
                        weather_city.setW_night_cond(ob7.getString("IconPhrase"));
                        if(image_night.length()==1)
                            weather_city.setNight_image("http://developer.accuweather.com/sites/default/files/0"+image_night+"-s.png");
                        else
                            weather_city.setNight_image("http://developer.accuweather.com/sites/default/files/"+image_night+"-s.png");

                        weather_city.setMore_data(oblocal.getString("MobileLink"));
                        Log.d("demo1",weather_city.toString());
                        five_day_fcast.add(weather_city);
                    }



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            child_exist_checker();
                            headline.setText(headlinetext);
                            heading.setText("Daily Forecast for "+city_name+", "+country_code);
                            adaptor = new Adaptor_recycler(Weather_widget.this,five_day_fcast,10,Weather_widget.this,2);
                            rv.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();
                            extend_details.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(extend_link));
                                    startActivity(browserIntent);
                                }
                            });

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void view_selected_data(int position)  {
        final weather_city weather_city = five_day_fcast.get(position);
        String dat_e = weather_city.getW_date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sd1 = new SimpleDateFormat("MMM dd,yyyy");
        try{
        forecast.setText("Forecast on "+sd1.format(sd.parse(dat_e)));
        } catch (ParseException e) {
        e.printStackTrace();
        }
        temperature.setText("Temperature: "+ weather_city.getW_temp_min_f()+"°F/"+weather_city.getW_temp_max_f()+"°F" );
        temperature_c.setText("Temperature: "+ weather_city.getW_temp_min_c()+"°C/"+weather_city.getW_temp_max_c() +"°C");
        Picasso.with(this).load(weather_city.getDay_image()).into(day_img);
        Picasso.with(this).load(weather_city.getNight_image()).into(night_img);
        day_cond.setText(weather_city.getW_day_cond());
        night_cond.setText(weather_city.getW_night_cond());
        more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(weather_city.getMore_data()));
                startActivity(browserIntent);
            }
        });
        pd.dismiss();

    }

    @Override
    public void favourite_changer(int position, boolean bol) {

    }

    @Override
    public void weather_widget_intent(String city, String country) {

    }

    void child_exist_checker()
    {

        child.child(city_key1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    result_return = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {

            finish();

        }return super.onKeyDown(keyCode, event);
    }

}
