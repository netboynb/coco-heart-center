#!/bin/bash

mvn -U clean package -Dmaven.test.skip=true -P online   -e