#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
OPS_VARS=$DIR/ops.vars
export $(cat $OPS_VARS | xargs)

ROOT_DIR=$DIR/ops

function usage {
    echo "Invalid parameters"
    echo "Usage $0 [environment] [region] [command]"
    exit 1
}

CMD=$1

terraform get -update=true $ROOT_DIR

terraform $CMD \
          -var "key_file=$AWS_PRIVATE_KEY" \
          -var "state=$ROOT_DIR/swanson.tfstate" \
          -var "password=$SWANSON_PASSWORD" \
          -var "username=$SWANSON_USERNAME" \
          -var "vpc_id=$VPC_ID" \
          $ROOT_DIR
