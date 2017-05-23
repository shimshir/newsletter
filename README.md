# Newsletter problem solution

You need `sbt` for the following commands

## Tests

`sbt test` - runs tests

`sbt coverage test coverageReport` - runs tests and generates a coverage report in /target/scala-2.12/scoverage-report/index.html

## Running

`sbt run` - runs the application on localhost:8080

## Packaging

`sbt universal:packageBin` - builds a distributable zip file in /target/universal/newsletter-1.0.zip