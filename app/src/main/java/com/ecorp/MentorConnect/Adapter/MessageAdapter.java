package com.ecorp.MentorConnect.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.ecorp.MentorConnect.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ecorp.MentorConnect.Model.Chat;
import com.ecorp.MentorConnect.R;
import com.paralleldots.paralleldots.App;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.*;

import static android.content.Context.VIBRATOR_SERVICE;
import static okio.Utf8.size;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    Typeface MR,MRR;


    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

        MRR = Typeface.createFromAsset(mContext.getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(mContext.getAssets(), "fonts/myriad.ttf");

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);
        holder.show_message.setTypeface(MRR);
        holder.txt_seen.setTypeface(MRR);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String email;
        if (fuser != null) {
            email = fuser.getEmail();
            //checks whether the user is mentor or mentee.If mentor, can use emotion detection
            if (email != null && !email.matches("[0-9]+@sastra.ac.in")) {
                if (getItemViewType(position) == MSG_TYPE_LEFT) {
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Log.d("receiver", chat.getMessage());
                            Log.d("chatid", mChat.get(position).getMessage());
                            Vibrator vibe= (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
                            if (vibe != null) {
                                vibe.vibrate(80);
                            }
                            sentiment senti = new sentiment();
                            senti.execute(chat.getMessage());
                            return false;
                        }
                    });
                }
            }
        }

        holder.show_message.setText(chat.getMessage());
        if(chat.getTime()!=null && !chat.getTime().trim().equals("")) {
            holder.time_tv.setText(holder.convertTime(chat.getTime()));
        }

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.profile_img);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView time_tv;
        public ViewHolder(View itemView) {
            super(itemView);


            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            time_tv = itemView.findViewById(R.id.time_tv);

        }

        public String convertTime(String time){
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String dateString = formatter.format(new Date(Long.parseLong(time)));
            return dateString;
        }


    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            Log.d("username",fuser.getEmail());
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    private class sentiment extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            int max= Integer.parseInt(s);

            Log.d("sentiment", s);
            Log.d("float", String.valueOf(s.length()));

            switch (max)
            {
                case 0:
                    Toast t=Toast.makeText(mContext,"Mentee is in Fear",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    LinearLayout toastContentView = (LinearLayout) t.getView();
                    ImageView imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.fear_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
                case 1:
                    t=Toast.makeText(mContext,"Mentee is Sad",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    toastContentView = (LinearLayout) t.getView();
                    imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.sad_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
                case 2:
                    t=Toast.makeText(mContext,"Mentee is Bored",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    toastContentView = (LinearLayout) t.getView();
                    imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.bored_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
                case 3:
                    t=Toast.makeText(mContext,"Mentee is Happy",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    toastContentView = (LinearLayout) t.getView();
                    imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.happy_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
                case 4:
                    t=Toast.makeText(mContext,"Mentee is Excited",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    toastContentView = (LinearLayout) t.getView();
                    imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.excited_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
                case 5:
                    t=Toast.makeText(mContext,"Mentee is Angry",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    toastContentView = (LinearLayout) t.getView();
                    imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.angry_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
                default:
                    t=Toast.makeText(mContext,"Thinking",Toast.LENGTH_LONG );
                    t.setGravity(Gravity.CENTER,0,0);
                    toastContentView = (LinearLayout) t.getView();
                    imageView = new ImageView(mContext.getApplicationContext());
                    imageView.setImageResource(R.mipmap.think_foreground);
                    toastContentView.addView(imageView,0);
                    t.show();
                    break;
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try{
                App pd = new App("XJCpdd3KX0jPZ7KlTsdKDfOv9jxBoho9XgEZr5OdEzQ");
                String s= pd.emotion(params[0]);
                Log.d("apimsg",params[0]);
                Log.d("apimsg",s);
                //Log.d("apimsg", String.valueOf(s.length()));

                int fear=s.indexOf("Fear");
                //Log.d("apimsg", String.valueOf(fear));
                int sad=s.indexOf("Sad");
                //Log.d("apimsg", String.valueOf(sad));
                int bored=s.indexOf("Bored");
                int happy=s.indexOf("Happy");
                int excited=s.indexOf("Excited");
                int angry=s.indexOf("Angry");

                Double[] emotions = new Double[6];
                emotions[0]= Double.parseDouble(s.substring(fear+6,fear+11));
                Log.d("apimsg", String.valueOf(emotions[0]));
                emotions[1]= Double.parseDouble(s.substring(sad+5,sad+10));
                Log.d("apimsg", String.valueOf(emotions[1]));
                emotions[2]=Double.parseDouble(s.substring(bored+7,bored+12));
                Log.d("apimsg", String.valueOf(emotions[2]));
                emotions[3]=Double.parseDouble(s.substring(happy+7,happy+12));
                Log.d("apimsg", String.valueOf(emotions[3]));
                emotions[4]=Double.parseDouble(s.substring(excited+9,excited+14));
                Log.d("apimsg", String.valueOf(emotions[4]));
                emotions[5]=Double.parseDouble(s.substring(angry+7,angry+12));
                Log.d("apimsg", String.valueOf(emotions[5]));


                int max=0;
                for (int i = 1; i < emotions.length; i++)
                    if (emotions[i] > emotions[max])
                        max = i;

                Log.d("values-f", String.valueOf(fear));
                Log.d("values-s", String.valueOf(sad));
                Log.d("values-b", String.valueOf(bored));
                Log.d("values-h", String.valueOf(happy));
                Log.d("values-e", String.valueOf(excited));
                Log.d("values-a", String.valueOf(angry));


                return String.valueOf(max);


            }
            catch(Exception e){
                System.out.println(e);
            }

            return null;
        }
    }
}