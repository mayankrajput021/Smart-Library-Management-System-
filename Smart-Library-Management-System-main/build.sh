#!/usr/bin/env bash
# Simple build helper for SLMS - no Maven/Gradle required.
# Usage:
#   ./build.sh compile   -> compiles main sources into out/
#   ./build.sh run       -> compiles (if needed) and runs the console app
#   ./build.sh test      -> compiles main+test sources and runs the test suite
#   ./build.sh clean     -> removes build output and generated data/logs

set -e

compile_main() {
  mkdir -p out
  find src/main -name "*.java" > /tmp/slms_main_sources.txt
  javac -d out -cp src/main/java @/tmp/slms_main_sources.txt
}

compile_test() {
  compile_main
  mkdir -p testout
  find src/test -name "*.java" > /tmp/slms_test_sources.txt
  javac -d testout -cp "out:src/test/java" @/tmp/slms_test_sources.txt
}

case "$1" in
  compile)
    compile_main
    echo "Compiled to out/"
    ;;
  run)
    compile_main
    java -cp out com.slms.Main
    ;;
  test)
    compile_test
    java -cp "out:testout" com.slms.test.TestRunner
    ;;
  clean)
    rm -rf out testout data logs
    echo "Cleaned build output and generated data/logs."
    ;;
  *)
    echo "Usage: $0 {compile|run|test|clean}"
    exit 1
    ;;
esac
