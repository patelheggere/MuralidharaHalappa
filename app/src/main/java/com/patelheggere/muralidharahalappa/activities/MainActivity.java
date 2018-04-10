package com.patelheggere.muralidharahalappa.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.patelheggere.muralidharahalappa.AppController;
import com.patelheggere.muralidharahalappa.R;
import com.patelheggere.muralidharahalappa.adapters.FeedListAdapter;
import com.patelheggere.muralidharahalappa.datamodels.FeedItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://muralidhara-halappa.firebaseio.com/News.json";
    private ActionBar mActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        if(mActionBar!=null)
        {
           getSupportActionBar().setIcon(R.drawable.muralidhara);
        }
        setContentView(R.layout.activity_main);
        feedItems = new ArrayList<FeedItem>();
        listView = findViewById(R.id.list_item);
        listAdapter = new FeedListAdapter(MainActivity.this, feedItems);
        listView.setAdapter(listAdapter);
        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        //getActionBar().setIcon( new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }

        // Inflate the layout for this fragment
        //return linearLayout;
    }


    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {

        try {
            System.out.println("parsedJson:"+response.toString());
            JSONObject object = new JSONObject(response.toString());
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String fbKey = iterator.next();
                JSONObject feedObj = object.getJSONObject(fbKey);
                FeedItem item = new FeedItem();
                item.setFbKey(fbKey);
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj.getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getLong("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url2") ? null : feedObj.getString("url2");
                String CommentCount = feedObj.isNull("commentCount") ? null : feedObj.getString("commentCount");
                String LikeCount = feedObj.isNull("LikeCount") ? null : feedObj.getString("LikeCount");
                item.setCommentCount(CommentCount);
                item.setLikeCount(LikeCount);
                item.setUrl(feedUrl);
                //item.setFbKey(iterator.next());
                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}


