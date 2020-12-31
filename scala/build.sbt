name := "scala"
version := "0.1"
scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.2",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test"
)

// for FunSuite
libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.2" % "test"

// for FlatSpec
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.2" % "test"

// for FunSpec
libraryDependencies += "org.scalatest" %% "scalatest-funspec" % "3.2.2" % "test"

// for wordSpec
libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.2" % "test"

// for FreeSpec
libraryDependencies += "org.scalatest" %% "scalatest-freespec" % "3.2.2" % "test"

// for PropSepc
libraryDependencies += "org.scalatest" %% "scalatest-propspec" % "3.2.2" % "test"

// for FeatureSpec
libraryDependencies += "org.scalatest" %% "scalatest-featurespec" % "3.2.2" % "test"

// for RefSpec
libraryDependencies += "org.scalatest" %% "scalatest-refspec" % "3.2.2" % "test"

// scalaMock
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test