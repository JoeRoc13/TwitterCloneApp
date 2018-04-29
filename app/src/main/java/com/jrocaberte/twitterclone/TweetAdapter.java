package com.jrocaberte.twitterclone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private Context context;
    private List<Tweet> list;

    public TweetAdapter(Context context, List<Tweet> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tweet_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = list.get(position);
        holder.mTweet.setText(tweet.getTweet());
        holder.mPostTime.setText(tweet.getPostTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTweet, mPostTime;

        public ViewHolder(View itemView) {
            super(itemView);

            mTweet = itemView.findViewById(R.id.tweet);
            mPostTime = itemView.findViewById(R.id.post_time);
        }
    }

}
