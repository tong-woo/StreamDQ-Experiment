#!/usr/bin/env bash
cd ../target
# reddit
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 1000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 10000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 100000 -r 7

# wiki click stream
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 1000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 10000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100000 -r 7