# User Guide

This is the user guide for the
[Accounts Manager](http://github.com/dpryden/accounts-manager) program.

## Prerequisites

As a prerequisite, you will first need to install the Java 8 runtime.
You can download this from [java.com](http://www.java.com/). When installing
Java, ensure you're only installing the runtime and not any other bundled
programs. You also do not need to install the Java web browser plugin.

Once you have Java installed, you should be able to open a command prompt
and run

    java -version

And receive some output like:

    java version "1.8.0_66"
    Java(TM) SE Runtime Environment (build 1.8.0_66-b17)
    Java HotSpot(TM) 64-Bit Server VM (build 25.66-b17, mixed mode)

As long as it starts with version 1.8 or better you should be good to go.

## Installation

To install the Accounts Manager program, simply unzip the
`accounts-manager-<version>-full.zip` to a convenient location.
(Development versions are tagged with a `-SNAPSHOT` suffix, so for example
the current latest version is `1.0-SNAPSHOT`.)

This should produce an `accounts-manager-<version>` folder. Inside this
folder will be three folders, named `bin`, `lib`, and `docs`.

The `bin` folder contains scripts for running the program, named
`AccountsManager.bat` for Windows and `AccountsManager` for Unix-like systems.

The `lib` folder contains Java libraries (JAR files) that are used by the
application.

The `docs` folder contains the documentation.

## Setting up

You will need to pick a folder on your hard drive for storing the accounts
information for each month. You will also need a folder to store the PDF
forms from jw.org.

### Creating the configuration file

You will need to create a file named `.accounts-manager.yaml` in your home
directory (on a Unix system this is the `~` directory, which typically is
named `/home/<username>`; on Windows it will be usually named something like
`C:\Users\<username>`.

The `.accounts-manager.yaml` file is a YAML file and can be edited with any
YAML editor, or with a plain text editor (e.g. Notepad). Copy the
`example-config.yaml` file from the `docs` directory to your home directory
and customize it according to your needs.

### Creating a previous month to record unreconciled transactions

You will need one previous month's reconciliation data to get started.
The next month's data can be filled in incrementally.

For the most recent *completed and reconciled* month, add a folder named
like `YYYY-MM` under your accounts folder (the folder specified as the
`root-dir` in the config file). Copy the `bootstrap-month.yaml` file from
the `docs` folder to this month's folder, rename it to `accounts.yaml`,
and fill in the unreconciled transactions accordingly.

### Creating an empty file for the current month

You will need to create an empty file to get the first month going.
(Subsequent months will get created automatically when you run
`AccountsManager close-month`.)

For the *current* month, add a folder named after the date (in `YYYY-MM`
format) under your accounts folder (the folder specified as the `root-dir`
in the config file). Copy the `first-month.yaml` file from the `docs`
folder to this month's folder, rename it to `accounts.yaml`, and fill in
the `opening-balance` and `receipts-carried-forward` accordingly.

### Final set-up

You may also find it convenient to add the `bin` directory to your system's
`PATH` (the process for doing this depends on your operating system).

## Running the program for the first time

To start with, run:

    AccountsManager help

You should see a help screen like this:

    Usage: AccountsManager <command> [args]

    Commands:
        add-deposit: Adds a deposit to the current month.
        add-expense: Add an expense item to the current month.
        add-receipts: Add receipts to the current month.
        close-month: Close the current month and compute totals.
        dump-config: Dump the current config data to console.
        dump-month: Dump the current month data to console.
        generate-forms: Generate PDF forms in the current month's folder.
        reconcile: Reconcile a bank statement with the accounts data.
        help: Show this help.

    Common arguments:
        --month=YYYY-MM    Set the current month.
                           (if not set, the value from the config file is used
                           instead)


## Common tasks

TODO(dpryden): Add documentation for common tasks

## Getting more help

There are plenty of known limitations with the program (basically, anything
I haven't needed yet I probably haven't written yet). If you find any bugs
or anything else that I need to do, please file a bug using the GitHub
issues interface: https://github.com/dpryden/accounts-manager/issues

