# Newsletter problem solution

You need `sbt` for the following commands

## Tests

Run tests

`sbt test`

Run tests, generate a coverage report in /target/scala-2.12/scoverage-report/index.html and open it in the default browser

`sbt showCoverage`

## Running

Run the application on localhost:8080

`sbt run`

## Packaging

Build a distributable zip file in /target/universal/newsletter-1.0.zip

`sbt universal:packageBin`