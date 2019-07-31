name := "spark-elasticsearch-example"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "ossrh repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.4.3",
  "org.elasticsearch" %% "elasticsearch-spark-20" % "7.2.1"
)

