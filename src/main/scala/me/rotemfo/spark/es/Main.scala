package me.rotemfo.spark.es

import java.sql.Timestamp
import java.time.Instant

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, udf}
import org.elasticsearch.spark.sql.EsSparkSQL
import org.slf4j.{Logger, LoggerFactory}

/**
  * project: spark-elasticsearch-example
  * package: me.rotemfo.spark.es
  * file:    Main
  * created: 2019-07-30
  * author:  rotem
  */
object Main {
  private final val logger: Logger = LoggerFactory.getLogger(getClass)
  private final val inputTable = "mytable"
  private final val keyField = "tenantid"

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(inputTable)
      .config("spark.master", "local[*]")
      .config("es.index.auto.create", "true")
      .getOrCreate()

    val data = spark.read.parquet(args(0))
    data.createTempView(inputTable)
    val query =
      s"""
         |SELECT DISTINCT $keyField
         |  FROM $inputTable
         """.stripMargin

    val keys = spark.sql(query).collect()
    try {
      keys.foreach(row => {
        val indexName = row.getAs[Long](keyField)

        logger.info(s"fetching data for $keyField=$indexName")
        val partQuery =
          s"""
             |SELECT *
             |  FROM $inputTable
             | WHERE $keyField=$indexName
         """.stripMargin

        val df = spark.sql(partQuery)
        val data = df.withColumn("date", toTimestamp(col("timestamp")))

        logger.info(s"pushing rows to index $indexName")
        EsSparkSQL.saveToEs(data, indexName.toString)
      })
    } finally {
      spark.close()
    }
  }

  private def toTimestamp: UserDefinedFunction = udf((timestamp: Long) => {
    Timestamp.from(Instant.ofEpochMilli(timestamp))
  })
}
