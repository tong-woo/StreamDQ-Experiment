# Experiment CLI App
Experiment setup of master project

## Initial build
First of all, build by maven
```bash
mvn clean install
```

## Cli Usage
```console
Options:
--path, -p -> path of the csv dataset (always required) { String }
--size, -s [1000] -> size of the flink window { Int }
--help, -h -> Usage info
```

## Run Time Experiment
For reddit data set, run command below:
```bash
cd target
kotlin -cp streamdpexp-0.0.1-SNAPSHOT.jar com.tong.streamdpexp.RunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/100M_reddit_posts.csv -s 1000
``` 

For wiki click stream data set, run command below:

```bash
cd target
kotlin -cp streamdpexp-0.0.1-SNAPSHOT.jar com.tong.streamdpexp.RunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/1M_clickstream_enwiki-2023-04.csv -s 1000
``` 

## Latency Experiment

To be added...