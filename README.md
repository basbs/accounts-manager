# Accounts Manager

This is a simple utility for making working with jw.org congregation accounts
forms easier.

## Legal caveat

I do not own the copyright in the forms: they must be downloaded from jw.org
by someone with the appropriate access. This utility simply makes it easier
to keep track of accounting information and fill in the forms.

## Building

To build this project you need a recent version of
[Apache Maven](http://maven.apache.org/).

Open a command line and navigate to the directory where this file is located,
then run:

    mvn package

This will create an executable `AccountsManager` script under
`target/AccountsManager/bin`, and also a zip file of the compiled application
named `accounts-manager-<version>-full.zip` under `target`.

## About the code

Each month's data is stored in [YAML](http://yaml.org) format. Global
configuration (e.g. file paths) are also stored as YAML (in the user's
home directory). YAML parsing is handled using the
[jackson-dataformat-yaml](https://github.com/FasterXML/jackson-dataformat-yaml)
implementation that adapts [Jackson](http://wiki.fasterxml.com/JacksonHome)
APIs to use [SnakeYAML](http://snakeyaml.org) parsing. Following
[this example](https://github.com/artem-zinnatullin/AutoJackson) the YAML
data is parsed into value types managed by
[AutoValue](https://github.com/google/auto/tree/master/value).

PDF manipulations are handled by [Apache PDFBox](http://pdfbox.apache.org/).

The actual code is Java 8, and is built using Maven. Object lifetime is
managed by [Dagger](http://google.github.io/dagger/). Test cases are written
using JUnit 4.8 and [Truth](https://github.com/google/truth). The code follows
[Google Java Style](http://google.github.io/styleguide/javaguide.html).
