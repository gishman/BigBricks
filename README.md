# BigBricks
[![Build Status](https://travis-ci.org/homedepot/BigBricks.svg?branch=master)](https://travis-ci.org/homedepot/BigBricks)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.homedepot/bigbricks/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.homedepot/bigbricks)

Design, deploy, and execute a big data pipeline
Check out the BigBricks-Core for the DSL details
```
                      name="SimpleOneSparkJob"
                      env="hd-www-search"
                      zone="us-east1-c"
                      sparkjob sparkJobName {
                            mainClass="com.homedepot.biglibrary.electricexamples.CreateData"
                            args= "--output,gs://mosambi/testout"
                            props=""
                            jarLocation="gs://mosambi/ElectricTemplate-0.0.1.jar"
                            }
                      cluster simpleCluster {
                            workers=2
                            image="n1-standard-4"
                            properties="spark:spark.executor.cores=4"
                            }
                      run sparkJobName on  simpleCluster
                      delete "gs://mosambi/testout"
```
