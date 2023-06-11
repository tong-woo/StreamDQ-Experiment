# Experiment CLI App

Experiment setup of master project
result has been saved to: <br /> [experiment result folder](experimentresult)

## Initial build

First of all, build by maven

```bash
mvn clean install
```

## Cli usage

```console
Options:
--path, -p -> path of the csv dataset (always required) { String }
--size, -s [1000] -> size of the flink window { Int }
--help, -h -> Usage info
```

## Run time experiment

For reddit data set, run command below:

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.RunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 1000
``` 

For wiki click stream data set, run command below:

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.RunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/1M_clickstream_enwiki-2023-04.csv -s 1000
``` 

## Latency experiment

To be added...