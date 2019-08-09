name := "spark-elasticsearch-example"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "ossrh repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.4.3",
  "org.elasticsearch" %% "elasticsearch-spark-20" % "7.2.1"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case "META-INF/services" => MergeStrategy.concat
  case "log4j.propreties" => MergeStrategy.first
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  // ----
  // required for spark-sql to read different data types (e.g. parquet/orc/csv...)
  // ----
  case PathList("META-INF", "services", _@_*) => MergeStrategy.first
  case PathList("META-INF", _@_*) => MergeStrategy.discard
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case _ => MergeStrategy.first
}

mainClass in assembly := Some("me.rotemfo.spark.es.Main")

test in assembly := {}
