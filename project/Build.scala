package diffson

import sbt._
import Keys._
import com.typesafe.sbt.osgi.SbtOsgi._
import com.typesafe.sbt.osgi.OsgiKeys

import java.io.File

object DiffsonBuild extends Build {

  lazy val diffson = (Project(id = "diffson",
    base = file(".")) settings (
    organization := "org.gnieh",
    name := "diffson",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.10.2",
    crossScalaVersions := Seq("2.9.3", "2.10.2"),
    libraryDependencies ++= dependencies,
    parallelExecution := false,
    fork in test := true,
    resourceDirectories in Compile := List(),
    OsgiKeys.exportPackage := Seq(
      "gnieh.diffson",
      "gnieh.diffson.*"
    ),
    OsgiKeys.additionalHeaders := Map (
      "Bundle-Name" -> "Gnieh Json JsonPatch"
    ),
    OsgiKeys.bundleSymbolicName := "org.gnieh.diffson",
    OsgiKeys.privatePackage := Seq(),
    compileOptions)
    settings(osgiSettings: _*)
    settings(publishSettings: _*)
  )

  lazy val dependencies = Seq(
    "org.scalatest" %% "scalatest" % "2.0.M5b" % "test" cross CrossVersion.binaryMapped {
      case "2.9.3" => "2.9.0"
      case v => "2.10"
    },
    "net.liftweb" %% "lift-json" % "2.5" cross CrossVersion.binaryMapped {
      case "2.9.3" => "2.9.2"
      case v => "2.10"
    }
  )

  lazy val compileOptions = scalacOptions <++= scalaVersion map { v =>
    if(v.startsWith("2.10"))
      Seq("-deprecation", "-language:_")
    else
      Seq("-deprecation")
  }

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    // The Nexus repo we're publishing to.
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
        else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { x => false },
    pomExtra := (
      <url>https://github.com/gnieh/diffson</url>
      <licenses>
        <license>
          <name>The Apache Software License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>https://github.com/gnieh/diffson</url>
        <connection>scm:git:git://github.com/gnieh/diffson.git</connection>
        <developerConnection>scm:git:git@github.com:gnieh/diffson.git</developerConnection>
        <tag>HEAD</tag>
      </scm>
      <developers>
        <developer>
          <id>satabin</id>
          <name>Lucas Satabin</name>
          <email>lucas.satabin@gnieh.org</email>
        </developer>
      </developers>
      <ciManagement>
        <system>travis</system>
        <url>https://travis-ci.org/#!/gnieh/diffson</url>
      </ciManagement>
      <issueManagement>
        <system>github</system>
        <url>https://github.com/gnieh/diffson/issues</url>
      </issueManagement>
    )
  )

}