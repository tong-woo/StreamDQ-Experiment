#!/usr/bin/env bash
cd ../target
# reddit
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 1000 -c 1 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 1000 -c 2 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 1000 -c 3  -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 1000 -c 4  -r 7

# wiki click stream
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100 -c 1 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100 -c 2 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100 -c 3 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.CheckNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100 -c 4 -r 7