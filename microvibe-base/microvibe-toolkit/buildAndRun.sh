#!/bin/bash

mvn package -DskipTests \
	&& cp target/microvibe-toolkit.jar app/ \
	&& ./app/main.sh app/conf/change.xml

