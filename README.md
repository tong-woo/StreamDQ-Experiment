# Experiment CLI App

Experiment setup of my master thesis project<br />
The result has been saved to: [experiment result folder](experimentresult)

## Initial build

First of all, build by maven

```bash
mvn clean install
```

## Cli usage

```console
Options:
--path, -p -> path of the csv dataset (always required) { String }
--size, -s [1000] -> size of the flink window { Int } for experiment
--columnName, -c -> column name of the csv dataset (always required) { String } for baseline experiment
--help, -h -> Usage info
```

## Run time experiment

**For reddit data set:**

`To run Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.RunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 1000
``` 

`To run Baseline Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.BaselineRunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -c score
``` 

**For wiki click stream data set:**

`To run Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.RunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/5M_clickstream_enwiki-2023-04.csv -s 1000
``` 

`To run Baseline Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.BaselineRunTimeExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/5M_clickstream_enwiki-2023-04.csv -c count
``` 

## Latency experiment

**For reddit data set:**

`To run Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.LatencyExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 1000
``` 

`To run Baseine Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.BaselineLatencyExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -c score
```

**For wiki click stream data set:**

`To run Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.LatencyExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/5M_clickstream_enwiki-2023-04.csv -s 100
```

`To run Baseline Experiment`

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.BaselineLatencyExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/5M_clickstream_enwiki-2023-04.csv -c count
``` 
## Overhead experiment

To measure the overhead of anomaly detection, 
we measure the **component run time** of only aggregate constraint computation. Then make the subtraction to get overhead

Again for reddit:

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv -s 1000
``` 
For wiki click stream:

```bash
cd target
kotlin -cp streamdqexp-0.0.1-SNAPSHOT.jar com.tong.streamdqexp.OverheadExperimentApp -p /Users/wutong/Desktop/experiment/dataset/ClickStream/5M_clickstream_enwiki-2023-04.csv -s 1000
``` 