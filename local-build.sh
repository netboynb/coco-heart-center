#!/bin/bash

mvn -U clean package -Dmaven.test.skip=true -P local  -e
