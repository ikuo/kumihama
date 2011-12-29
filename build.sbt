name := "Keicho"

version := "1.0"

scalaVersion := "2.9.1"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.6.4"

libraryDependencies += "com.google.guava" % "guava" % "10.0.1"

libraryDependencies += "com.mongodb.casbah" % "casbah_2.8.1" % "2.0.2"

libraryDependencies += "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"

resolvers += "repo.novus rels" at "http://repo.novus.com/releases/"

resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"
