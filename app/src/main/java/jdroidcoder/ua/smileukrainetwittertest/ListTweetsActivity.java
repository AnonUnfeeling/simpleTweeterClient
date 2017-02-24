package jdroidcoder.ua.smileukrainetwittertest;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;
import com.twitter.sdk.android.tweetui.CollectionTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.net.MalformedURLException;


/**
 * Created by jdroidcoder on 24.02.17.
 */
public class ListTweetsActivity extends ListActivity {
    private TweetTimelineListAdapter adapter;
    private TweetReceiver tweetReceiver = new TweetReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        loadTweets();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS");
        intentFilter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE");
        registerReceiver(tweetReceiver, intentFilter);
    }

    private void loadTweets() {
        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(Session.twitterSession.getUserName())
                .build();
        adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);
    }

    public void makeTweet(View view) throws MalformedURLException {
        Intent intent = new ComposerActivity.Builder(ListTweetsActivity.this)
                .session(Session.twitterSession)
                .createIntent();
        startActivity(intent);
    }

    private class TweetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                    }

                    @Override
                    public void failure(TwitterException exception) {
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(tweetReceiver);
        super.onDestroy();
    }
}
