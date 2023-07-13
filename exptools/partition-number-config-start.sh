#!/usr/bin/env bash
cd ../target
#for reddit
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/1_partitions_20M_Reddit.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/10_partitions_20M_Reddit.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/100_partitions_20M_Reddit.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/1000_partitions_20M_Reddit.csv -s 100 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/10000_partitions_20M_Reddit.csv -s 100 -r 7

#for wiki click stream
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/1_partitions_2M_clickstream.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/10_partitions_2M_clickstream.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/100_partitions_2M_clickstream.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/1000_partitions_2M_clickstream.csv -s 10 -r 7
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.PartitionNumberConfigExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/10000_partitions_2M_clickstream.csv -s 10 -r 7