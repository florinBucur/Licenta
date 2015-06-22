package com.bucur.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.FriendsAdapter;
import items.FeedFriendItem;


public class FriendsActivity extends Activity {

    private List<FeedFriendItem> feedItemList = new ArrayList<FeedFriendItem>();

    public FriendsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_friends);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Intent intent = getIntent();
        if (intent != null) {

            GraphRequestBatch request = new GraphRequestBatch(
                    GraphRequest.newMyFriendsRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        JSONArray jsonArray,
                                        GraphResponse response) {

                                    System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                    System.out.println("getFriendsData onCompleted : response " + response);

                                    if (null == feedItemList) {
                                        feedItemList = new ArrayList<FeedFriendItem>();
                                    }

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            FeedFriendItem item = new FeedFriendItem();

                                            item.setFbid(jsonArray.getJSONObject(i).getString("id"));

                                            item.setName(jsonArray.getJSONObject(i).getString("name"));
                                            feedItemList.add(item);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    try {
                                        JSONObject jsonObject = response.getJSONObject();
                                        System.out.println("getFriendsData onCompleted : jsonObject " + jsonObject);

                                        JSONObject summary = jsonObject.getJSONObject("summary");
                                        System.out.println("getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })

            );
            request.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch graphRequests) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
                    FriendsAdapter mAdapter = new FriendsAdapter(getApplicationContext(), feedItemList);

                    recyclerView.setAdapter(mAdapter);

                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                }
            });

            request.executeAsync();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
