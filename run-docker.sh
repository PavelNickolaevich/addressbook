#!/usr/bin/env bash

docker container run \
--publish 5432:5432 \
--env POSTGRESQL_PASSWORD=my_pass \
--env POSTGRESQL_DATABASE=addressbook_db \
--env POSTGRESQL_USERNAME=addressbook \
--detach \
--volume ./db/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql \
--name db \
bitnami/postgresql:15

docker container start db

docker container run \
--publish 8080:8080 \
--link db \
--detach \
--volume ./configuration:/app/config \
addressbook:1

