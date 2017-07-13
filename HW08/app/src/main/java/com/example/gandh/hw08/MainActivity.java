package com.example.gandh.hw08;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v7.appcompat.R.id.image;
import static android.support.v7.appcompat.R.id.wrap_content;

public class MainActivity extends AppCompatActivity implements Adaptor_recycler.fromadaptor {
    SharedPreferences.Editor editor;
    SharedPreferences prefs, sharedPrefs;
    ProgressBar pb;
    RelativeLayout rl1,rl2;
    Button set_current_city, search_city;
    Toolbar t;
    EditText cityname, countryname;
    TextView fixedtext1, widget_hedaer, widget_cloudy, widget_temp, widget_time,widget_temp_f, fixedtext2, fixed3, fixed4;
    ImageView widget_image;
    OkHttpClient client;
    String key =    "CcGVGr7vhPK3dDagCerm69PKuF1GvCrs",temp_sign="C",city_search_key;
    DatabaseReference mDatabase,child;
    ArrayList<weather_city> saved_city_list;
    Adaptor_recycler saved_city_adaptor;
    RecyclerView rcl1;
    PrettyTime pt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editor = getSharedPreferences("current_city", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("current_city", MODE_PRIVATE);
        //editor.clear().commit(); // to be removed later
        view_matcher();
       // Image m = (Image) R.drawable.images;
        setSupportActionBar(t);
        fixedtext2.setVisibility(View.INVISIBLE);
        pb.setVisibility(pb.INVISIBLE);
        rl1.setVisibility(View.INVISIBLE);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rcl1.setLayoutManager(lm);
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        child = mDatabase.child("saved_cities");
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(sharedPrefs.getString("temp","na").equals("f"))
            widget_temp.setVisibility(View.INVISIBLE);
        else
            widget_temp_f.setVisibility(View.INVISIBLE);
        if(storedpref_getter()==null)
        {
         set_current_city.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
             @Override
             public void onClick(View v) {
                alert_box_current_city();
             }
         });
        }
        else
        {
            set_current_city.setVisibility(View.INVISIBLE);
            fixedtext1.setVisibility(View.INVISIBLE);
            weather_current_city_data_getter();
        }

        search_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city_name = cityname.getText().toString();
                String country_name = countryname.getText().toString();
//                String city_name = "sanjose";
//                String country_name = "us";

                    Intent ib = new Intent(MainActivity.this, Weather_widget.class);
                    ib.putExtra("city", city_name);
                    ib.putExtra("country",country_name);
                    startActivityForResult(ib,500);
                    MainActivity.this.recreate();
            }
        });
    }

    void view_matcher()
    {
        rcl1 = (RecyclerView) findViewById(R.id.rcl1);
        widget_temp_f = (TextView) findViewById(R.id.text_temp_dup);
        t=(Toolbar)findViewById(R.id.tool_bar);
        pb = (ProgressBar) findViewById(R.id.pb1);
        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        fixedtext1 = (TextView) findViewById(R.id.textView2);
        set_current_city = (Button) findViewById(R.id.setcurrent_button);
        search_city = (Button) findViewById(R.id.button3);
        cityname = (EditText) findViewById(R.id.editText);
        countryname = (EditText) findViewById(R.id.editText2);
        widget_hedaer = (TextView) findViewById(R.id.text_head_city);
        widget_cloudy = (TextView) findViewById(R.id.text_cloudy);
        widget_temp = (TextView) findViewById(R.id.text_temperature);
        widget_time = (TextView) findViewById(R.id.text_time_pretty);
        widget_image = (ImageView) findViewById(R.id.imageView);
        fixedtext2 = (TextView) findViewById(R.id.textView6);
        fixed3 = (TextView) findViewById(R.id.textView4);
        fixed4 = (TextView) findViewById(R.id.textView5);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this,Preference_activity.class);
                int reqcode = 200;
                startActivityForResult(intent,reqcode);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==200)
        {
            if(resultCode==RESULT_OK)
            {
                if(data.getExtras()!=null) {
                    weather_current_city_code(data.getExtras().getString("country"), data.getExtras().getString("city"));
                    Log.d("democountry", data.getExtras().getString("country"));
                }
            }

            if(resultCode==100)
            {
                if(data.getExtras()!=null) {
                    widget_temp_f.setVisibility(View.INVISIBLE);
                    widget_temp.setVisibility(View.VISIBLE);
                    Toast.makeText(this,"Temperature unit has been changed from F to C",Toast.LENGTH_SHORT).show();
                    saved_city_adaptor = new Adaptor_recycler(MainActivity.this,saved_city_list,20,MainActivity.this,0);
                }
            }

            if(resultCode==300)
            {
                if(data.getExtras()!=null) {
                    widget_temp_f.setVisibility(View.VISIBLE);
                    widget_temp.setVisibility(View.INVISIBLE);
                    Toast.makeText(this,"Temperature unit has been changed from C to F",Toast.LENGTH_SHORT).show();
                    saved_city_adaptor = new Adaptor_recycler(MainActivity.this,saved_city_list,20,MainActivity.this,1);
                }
            }
        }
        else if(requestCode==500)
        {
            if(resultCode==RESULT_OK)
            {
                if(data.getExtras()!=null) {
                    weather_current_city_code(data.getExtras().getString("country"), data.getExtras().getString("city"));
                    Log.d("democountry", data.getExtras().getString("country"));
                }
            }

            if (resultCode==700)
            {
                weather_current_city_data_getter();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.save);
        menu.removeItem(R.id.current);
        return true;
    }


    void storedpref_setter(String city, String country, String city_key)
    {

            editor.clear();
            editor.putString("city", city);
            editor.putString("country", country);
            editor.putString("city_key", city_key);
            editor.commit();
            Toast.makeText(this,city+" details stored",Toast.LENGTH_SHORT).show();
            weather_current_city_data_getter();

    }

    String storedpref_getter()
    {

            String city_key = prefs.getString("city_key",null);
            return city_key;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void alert_box_current_city()
    {
        RelativeLayout l1 = new RelativeLayout(this);
        final EditText editText_city = new EditText(this);
        editText_city.setId(View.generateViewId());
        final EditText editText_country = new EditText(this);
        editText_city.setText("Enter your city");
        editText_country.setText("Enter your country");
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

                        weather_current_city_code(editText_country.getText().toString(),editText_city.getText().toString());

                    }
                })
                .create().show();
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                storedpref_setter(city_name, country_code, city_key);
                            }
                        });


                } catch (JSONException e) {
                   e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"Invalid city/country",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    void weather_current_city_data_getter()
    {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dataservice.accuweather.com/currentconditions/v1/"+storedpref_getter()+"?apikey="+key)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();

                try {
                    JSONArray array1 = new JSONArray(s);
                    JSONObject ob1 = array1.getJSONObject(0);
                    final weather_city weather_city = new weather_city();
                    weather_city.setTime(ob1.getString("LocalObservationDateTime"));
                    weather_city.setText(ob1.getString("WeatherText"));
                    String image_id =  ob1.getString("WeatherIcon");
                    if(image_id.length()==1)
                        weather_city.setIcon("http://developer.accuweather.com/sites/default/files/0"+image_id+"-s.png");
                    else
                        weather_city.setIcon("http://developer.accuweather.com/sites/default/files/"+image_id+"-s.png");
                    JSONObject ob2 = ob1.getJSONObject("Temperature");
                    JSONObject ob3 = ob2.getJSONObject("Metric");
                    weather_city.setTemp_c(ob3.getString("Value"));
                    JSONObject ob4 = ob2.getJSONObject("Imperial");
                    weather_city.setTemp_f(ob4.getString("Value"));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            widget_hedaer.setText(prefs.getString("city","nonecity")+","+prefs.getString("country","nonecountry"));
                            widget_cloudy.setText(weather_city.getText());
                            widget_temp.setText("Temperature: "+ weather_city.getTemp_c()+"°C");
                            widget_temp_f.setText("Temperature: "+ weather_city.getTemp_f()+"°F");
                            Picasso.with(MainActivity.this).load(weather_city.getIcon()).into(widget_image);
                            set_current_city.setVisibility(View.INVISIBLE);
                            fixedtext1.setVisibility(View.INVISIBLE);
                            rl1.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.INVISIBLE);
                            String d_ate = weather_city.getTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                             pt = new PrettyTime();
                            try {
                                widget_time.setText("Last updated: "+ pt.format(formatter.parse(d_ate)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        saved_city_list = new ArrayList<>();

        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saved_city_list = new ArrayList<>();
                Log.d("datachange",dataSnapshot.toString());
                if(dataSnapshot.hasChildren())
                {
                    fixedtext2.setVisibility(View.VISIBLE);
                    fixed3.setVisibility(View.INVISIBLE);
                    fixed4.setVisibility(View.INVISIBLE);
                }
                else
                {
                    fixedtext2.setVisibility(View.INVISIBLE);
                    fixed3.setVisibility(View.VISIBLE);
                    fixed4.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot snapshot  :
                      dataSnapshot.getChildren()  ) {
                    weather_city weather_city = new weather_city();
                    weather_city = snapshot.getValue(weather_city.class);
                    saved_city_list.add(weather_city);
                }
                Collections.sort(saved_city_list, new Comparator<weather_city>() {
                    @Override
                    public int compare(weather_city o1, weather_city o2) {
                        if(o1.favourite)
                            return -1;

                        return 0;
                    }
                });
                if(sharedPrefs.getString("temp","na").equals("f")) {
                    saved_city_adaptor = new Adaptor_recycler(MainActivity.this, saved_city_list, 20, MainActivity.this, 1);
                    widget_temp.setVisibility(View.INVISIBLE);
                    widget_temp_f.setVisibility(View.VISIBLE);
                }
                else {
                    saved_city_adaptor = new Adaptor_recycler(MainActivity.this, saved_city_list, 20, MainActivity.this, 0);
                    widget_temp_f.setVisibility(View.INVISIBLE);
                    widget_temp.setVisibility(View.VISIBLE);
                }

                rcl1.setAdapter(saved_city_adaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void view_selected_data(int position) {
        String city_key_delete = saved_city_list.get(position).citykey;

        child.child(city_key_delete).removeValue();

//        saved_city_list.remove(position);
//       // saved_city_adaptor = new Adaptor_recycler(MainActivity.this,saved_city_list,20,MainActivity.this);
      saved_city_adaptor.notifyItemChanged(position);
        Toast.makeText(this,"saved city deleted",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void favourite_changer(int position, boolean bol) {
        String city_key_delete = saved_city_list.get(position).citykey;
        child.child(city_key_delete).child("favourite").setValue(bol);
        saved_city_adaptor.notifyDataSetChanged();
    }

    @Override
    public void weather_widget_intent(String city, String country) {
        Intent ib = new Intent(MainActivity.this, Weather_widget.class);
        ib.putExtra("city", city);
        ib.putExtra("country",country);
        startActivityForResult(ib,500);
        MainActivity.this.recreate();
    }
}
