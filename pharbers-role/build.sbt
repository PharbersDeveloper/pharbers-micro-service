def common = Seq(
	scalaVersion := "2.11.8",
	version := "1.0.0",
	organization := "com.pharbers"
)

lazy val root = (project in file(".")).
	enablePlugins(PlayScala)
	.disablePlugins(PlayFilters)
    .settings(common: _*).
	settings(
		name := "pharbers-role",
		fork in run := true,
		javaOptions += "-Xmx2G"
	)

// Play
routesGenerator := InjectedRoutesGenerator

// Docker
import NativePackagerHelper.directory
mappings in Universal ++= directory("pharbers_config_deploy")
		.map(x => x._1 -> x._2.replace("pharbers_config_deploy", "pharbers_config"))

// MVN
resolvers += Resolver.mavenLocal

// Dependencies
libraryDependencies += guice
libraryDependencies ++= Seq(
	"com.pharbers" % "pharbers-client-base_2.11" % "1.0.0",

	"org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
	"org.specs2" % "specs2_2.11" % "3.7" % Test
)
