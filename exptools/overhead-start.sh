#!/usr/bin/env bash
cd ../target

# reddit
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 1000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/40M_reddit_posts.csv -s 1000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/60M_reddit_posts.csv -s 1000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/80M_reddit_posts.csv -s 1000 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/100M_reddit_posts.csv -s 1000 -r 7

# wiki click stream
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/2M_clickstream_enwiki-2023-04.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/4M_clickstream_enwiki-2023-04.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/6M_clickstream_enwiki-2023-04.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/8M_clickstream_enwiki-2023-04.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/10M_clickstream_enwiki-2023-04.csv -s 100 -r 7