def common = Seq(
    scalaVersion := "2.11.8",
    version := "1.0.0",
    organization := "com.pharbers"
)

lazy val root = (project in file("."))
        .settings(common: _*)
        .settings(
            name := "pharbers-client-base",
            fork in run := true,
            javaOptions += "-Xmx2G",
            publishArtifact in packageDoc := false,
            publishArtifact in packageSrc := false,
            exportJars := true,
            crossPaths := true
        )

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
    "commons-httpclient" % "commons-httpclient" % "3.1",
    "org.mongodb" % "casbah_2.11" % "3.1.1",
    "com.easemob" % "rest-java-sdk" % "1.0.1",
    "org.apache.avro" % "avro" % "1.7.6",
    "log4j" % "log4j" % "1.2.17",
    "commons-io" % "commons-io" % "2.4",

    "com.typesafe.play" % "play-json_2.11" % "2.4.3",
    "com.typesafe.play" % "play_2.11" % "2.6.6",

    "com.pharbers" % "pharbers-module" % "0.1",
    "com.pharbers" % "pharbers-mongodb" % "0.1",
    "com.pharbers" % "pharbers-errorcode" % "0.1",
    "com.pharbers" % "pharbers-third" % "0.1",
    "com.pharbers" % "pharbers-security" % "0.1",
    "com.pharbers" % "pharbers-redis" % "0.1",
    "com.pharbers" % "pharbers-pattern" % "0.1"
)
