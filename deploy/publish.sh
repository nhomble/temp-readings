#!/usr/bin/env bash

source ./.env

TOPIC=$1

post_data() {
  cat <<EOF
  {
    "records": [
      {
        "value":
          {
            "description": "$description",
            "timestamp": "$timestamp",
            "temperature": "temperature",
            "direction": "$direction"
          }
        }
      ]
    }
  }
EOF
}

if [[ -z "$TOPIC" ]]; then
  echo "Define a topic!"
  exit 1
fi

while IFS=, read id description timestamp temperature direction; do
  echo $timestamp
  curl --request POST -sL \
  --url "http://localhost:${PORT_DOCKER_KAFKA}/topics/$TOPIC" \
  -H "Content-Type: application/vnd.kafka.json.v2+json" \
  --data "$(post_data)"
done <<<"$(cat IOT-temp.csv | sed "1 d")"
