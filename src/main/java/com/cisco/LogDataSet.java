package com.cisco;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

public class LogDataSet {

    SparkSession spark = SparkSession.builder().appName("Build a DataFrame from Scratch").master("local[*]")
            .getOrCreate();

    List<String[]> stringAsList = new ArrayList<>();

    JavaSparkContext sparkContext = new JavaSparkContext(spark.sparkContext());

    JavaRDD<Row> rowRDD = sparkContext.parallelize(stringAsList).map(RowFactory::create);

    // Creates schema
    StructType schema = DataTypes
            .createStructType(new StructField[] { DataTypes.createStructField("foe1", DataTypes.StringType, false),
                    DataTypes.createStructField("foe2", DataTypes.StringType, false) });

    Dataset<Row> df = spark.sqlContext().createDataFrame(rowRDD, schema).toDF();

}
