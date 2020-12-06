#!/usr/bin/env sh

TOPIC=$1

docker-compose exec kafka \
  kafka-topics --create \
  --zookeeper kafka-zookeeper:32181 \
  --replication-factor 1 \
  --partitions 1 \
  --topic $TOPIC
