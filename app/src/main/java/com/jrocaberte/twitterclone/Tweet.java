package com.jrocaberte.twitterclone;

public class Tweet {

    public String tweet;
    public String post_time;

    public Tweet() {

    }

    public Tweet(String tweet, String post_time) {
        this.tweet = tweet;
        this.post_time = post_time;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public void setPostTime(String post_time) {
        this.post_time = post_time;
    }

    public String getTweet() {
        return tweet;
    }

    public String getPostTime() {
        return post_time;
    }
}
