# Ratio Editor [![Build Status](https://travis-ci.org/manabreak/ratio.svg?branch=master)](https://travis-ci.org/manabreak/ratio)

Ratio Editor is a voxel editor with some extra features.

## Building and running

Clone the repository:

    git clone https://github.com/manabreak/ratio.git

`cd` into `ratio` directory:

    cd ratio

Build using gradle `dist` task:

    ./gradlew dist

Run:

    java -jar editor/build/libs/editor.jar

## Executing tests

You can run all the unit tests by running `./gradlew clean test`

## Contributing to Ratio

All contributions - bug fixes, new features, documentation, increasing test coverage - are highly appreciated! While contribution guidelines are still on my TODO list, here are a few pointers:

- Follow the general coding style used throughout the code base; use auto-formatting

- Strive to follow SOLID principles and keep your code DRY

- When possible, verify the changes with unit tests; missing tests should be an exception

- Keep your changes as small as possible, but not smaller
