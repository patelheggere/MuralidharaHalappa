package com.patelheggere.muralidharahalappa.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.patelheggere.muralidharahalappa.AppController;
import com.patelheggere.muralidharahalappa.R;
import com.patelheggere.muralidharahalappa.datamodels.FeedItem;
import com.patelheggere.muralidharahalappa.helper.FeedImageView;

import java.util.List;

/**
 * Created by Patel Heggere on 4/9/2018.
 */

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       // firebaseDatabase = FirebaseDatabase.getInstance();// firebase instance
      //  mFireBaseUserRef = firebaseDatabase.getReference("commentsLikes").child("comments").child(feedItems.get(position).getFbKey());
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        //TextView LikeCount = (TextView)convertView.findViewById(R.id.tvLike);
       // TextView CommentCount = (TextView)convertView.findViewById(R.id.tvComment);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);


        final FeedItem item = feedItems.get(position);

        name.setText(item.getName());
        if(item.getCommentCount()!=null) {
            //CommentCount.setText("Comments:" + item.getCommentCount());
        }
        else
        {
           // CommentCount.setText("Comments:"+"0");
        }
        if(item.getLikeCount()!=null) {
           // LikeCount.setText("Likes:" + item.getLikeCount());
        }
        else
        {
           // LikeCount.setText("Likes:"+"0");
        }

        final View finalConvertView = convertView;
        final View finalConvertView1 = convertView;



        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(item.getTimeStamp())),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
    /*
    public void onShowPopup(View v, final String fbKey){

        LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        View inflatedView = layoutInflater.inflate(R.layout.commentpopuplayout, null,false);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        ImageButton sendButton = (ImageButton)inflatedView.findViewById(R.id.sendbtn);
        final EditText comment = (EditText)inflatedView.findViewById(R.id.writeComment);
        // get device size
        Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items
        setSimpleList(listView);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countofcomment=0;
                SharedPreferences settings=activity.getSharedPreferences("prefs",0);
                CommentModel commentModel=new CommentModel();
                commentModel.setCommentedBy(settings.getString("Phone",null));
                commentModel.setCommentMessage(comment.getText().toString());
                commentModel.setCommentedByName(settings.getString("Name",null));
                commentModel.setCommentedOn(ServerValue.TIMESTAMP);
                mFireBaseUserRef.child(commentModel.getCommentedBy()).setValue(commentModel,null);
                mFireBaseUserRef = firebaseDatabase.getReference().child("News").child(fbKey);
                String count = AppConstatnts.firebaseUrl+"News/"+fbKey+"/commentCount.json";
                try {
                    URL url = new URL(count);
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            httpsURLConnection.getInputStream()));
                    String inputLine="";
                    while ((inputLine = in.readLine()) != null)

                        System.out.println("ascbcbakd:"+inputLine);
                    System.out.println("dsfdsg:"+"\""+0+"\"");

                    if(inputLine.equalsIgnoreCase("\""+0+"\"")) {

                        countofcomment = countofcomment + 1;
                        mFireBaseUserRef.child("commentCount").setValue(String.valueOf(countofcomment));

                    }
                    else {
                        inputLine = inputLine.replaceAll("^\"|\"$", "");
                        System.out.println("Sdfghj"+inputLine);
                        countofcomment = Integer.parseInt(inputLine);
                        countofcomment = countofcomment + 1;
                        mFireBaseUserRef.child("commentCount").setValue(String.valueOf(countofcomment));
                    }

                    in.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, width,height-50, true );
        // set a background drawable with rounders corners
        //GradientDrawable drawable = (GradientDrawable)activity.getResources().getDrawable(R.drawable.wwe_logo);
        //drawable.setColor(Color.parseColor("#00bfff"));
        //popWindow.setBackgroundDrawable(drawable);
        //popWindow.setBackgroundDrawable(activity.getResources().getColor(R.color.colorPrimary));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }
    void setSimpleList(ListView listView){

        ArrayList<String> contactsList = new ArrayList<String>();

        for (int index = 0; index < 20; index++) {
            contactsList.add("I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
        }

        listView.setAdapter(new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, contactsList));
    }
*/

}

