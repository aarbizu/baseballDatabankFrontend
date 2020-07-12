build:
	./gradlew build spotlessCheck installDist idea

run:
	./build/install/baseballDatabankFrontend/bin/baseballDatabankFrontend

tasks:
	./gradlew tasks

.PHONY: build
