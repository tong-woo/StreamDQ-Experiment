#!/usr/bin/env bash
cd ../target
# reddit
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 5 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 15 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 20 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 25 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 30 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 35 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 40 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 45 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 50 -r 7

# wiki click stream
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 5 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 15 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 20 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 25 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 30 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 35 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 40 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 45 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.WindowSizeConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 50 -r 7