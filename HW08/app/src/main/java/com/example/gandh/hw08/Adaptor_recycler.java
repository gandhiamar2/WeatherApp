package com.example.gandh.hw08;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.gandh.hw08.R.drawable.star_gold;

/**
 * Created by gandh on 4/5/2017.
 */

public class Adaptor_recycler extends RecyclerView.Adapter {
    int viewtype;
    ArrayList<weather_city> five_day_fcast = new ArrayList<>();
    Context context;
    fromadaptor intf;
    View v;
    int c_f;

    Adaptor_recycler(Context context,ArrayList<weather_city> five_day_fcast,int viewtype,fromadaptor intf,int c_f){
        this.five_day_fcast = five_day_fcast;
        this.viewtype= viewtype;
        this.context = context;
        this.intf = intf;
        this.c_f = c_f;
    }

    interface fromadaptor{

        void view_selected_data(int position);
        void favourite_changer(int position,boolean bol);
        void weather_widget_intent(String city, String country);
    }

    class View_holder extends RecyclerView.ViewHolder{
        TextView date,head,temp;
        ImageView img;
        ImageButton ib;
        View v;
        int view_type;
        public View_holder(View itemView, int view_type) throws ParseException {
            super(itemView);
            v = itemView;
            this.view_type = view_type;
            if(view_type==10) {
                date = (TextView) itemView.findViewById(R.id.textView);
                img = (ImageView) itemView.findViewById(R.id.imageView2);
                intf.view_selected_data(0);
            }
            else if(view_type==20)
            {
                date = (TextView) itemView.findViewById(R.id.tt3);
                head = (TextView) itemView.findViewById(R.id.tt1);
                temp = (TextView) itemView.findViewById(R.id.tt2);
                ib = (ImageButton) itemView.findViewById(R.id.imageButton);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType==10) {
             v = inflater.inflate(R.layout.grid_recycler, parent, false);

        }
        else if(viewType==20)
        {
             v = inflater.inflate(R.layout.linear_recycler, parent, false);
        }
        View_holder holder = null;
        try {
            holder = new View_holder(v,viewType);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return viewtype;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
           final View_holder view_holder = (View_holder) holder;
        if(view_holder.view_type==10) {
            String dat_e = five_day_fcast.get(position).getW_date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sd1 = new SimpleDateFormat("dd'th' MMM yyyy");
            try {
                view_holder.date.setText(sd1.format(sd.parse(dat_e)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Picasso.with(context).load(five_day_fcast.get(position).getDay_image()).into(view_holder.img);
            view_holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intf.view_selected_data(position);
                }
            });
        }
        else if(view_holder.view_type==20)
        {
            //view_holder.date.setText(five_day_fcast.get(position).);

            view_holder.head.setText(five_day_fcast.get(position).cityname+","+five_day_fcast.get(position).country);
            if(c_f==0)
            view_holder.temp.setText("Temperature: "+five_day_fcast.get(position).temperature+"°C");
            else if(c_f==1)
            view_holder.temp.setText("Temperature: "+String.format("%.2f",(Double.parseDouble(five_day_fcast.get(position).temperature)*1.8)+32)+"°F");
            String date_e = five_day_fcast.get(position).date;
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PrettyTime pt = new PrettyTime();
            try {
                view_holder.date.setText("Last updated: "+ pt.format(sd.parse(date_e)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(five_day_fcast.get(position).favourite)
            {
                view_holder.ib.setImageDrawable(context.getDrawable(R.drawable.star_gold));
            }
            view_holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intf.weather_widget_intent(five_day_fcast.get(position).cityname,five_day_fcast.get(position).country);
                }
            });
            view_holder.ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!five_day_fcast.get(position).favourite)
                    {
                        view_holder.ib.setImageDrawable(context.getDrawable(R.drawable.star_gold));
                        five_day_fcast.get(position).favourite = true;
                        intf.favourite_changer(position,true);
                    }
                    else
                    {
                        view_holder.ib.setImageDrawable(context.getDrawable(R.drawable.star_gray));
                        intf.favourite_changer(position,false);
                    }
                }
            });
            view_holder.v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    intf.view_selected_data(position);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return five_day_fcast.size();
    }
}
