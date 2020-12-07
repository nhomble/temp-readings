#!/usr/bin/env bash

source ./.env

TOPIC=$1

docker-compose exec kafka \
  kafka-topics --create \
  --zookeeper kafka-zookeeper:"${PORT_DOCKER_ZK}" \
  --replication-factor 1 \
  --partitions 1 \
  --topic $TOPIC

