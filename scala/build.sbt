name := "scala"
version := "0.1"
scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

// for FunSuite
libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.9" % "test"

// for FlatSpec
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.9" % "test"

// for FunSpec
libraryDependencies += "org.scalatest" %% "scalatest-funspec" % "3.2.9" % "test"

// for wordSpec
libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.9" % "test"

// for FreeSpec
libraryDependencies += "org.scalatest" %% "scalatest-freespec" % "3.2.9" % "test"

// for PropSepc
libraryDependencies += "org.scalatest" %% "scalatest-propspec" % "3.2.9" % "test"

// for FeatureSpec
libraryDependencies += "org.scalatest" %% "scalatest-featurespec" % "3.2.9" % "test"

// for RefSpec
libraryDependencies += "org.scalatest" %% "scalatest-refspec" % "3.2.9" % "test"

// scalaMock
libraryDependencies += "org.scalamock" %% "scalamock" % "5.1.0" % "test"