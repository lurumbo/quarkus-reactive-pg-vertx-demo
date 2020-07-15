FROM postgres:12.3

ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD 1111
ENV POSTGRES_DB usersdb

ADD users.sql /docker-entrypoint-initdb.d/users.sql
