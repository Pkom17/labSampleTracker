FROM postgres:14.4
LABEL author="I-TECH CIV"
LABEL description="Postgres DB for Lab Sample Tracker Application"
LABEL version="1.0"
COPY init_db.sql /docker-entrypoint-initdb.d/


