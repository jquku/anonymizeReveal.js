#!/bin/bash

psql -h localhost -p 5432 -U postgres << EOF
\c learning_analytics_development
create table if not exists students_anonymized as (select * from students) with no data;
create table if not exists tracked_sessions_anonymized as (select * from tracked_sessions) with no data;
ALTER TABLE students_anonymized ALTER COLUMN created_at TYPE text, ALTER COLUMN updated_at TYPE text;
ALTER TABLE tracked_sessions_anonymized ALTER COLUMN created_at TYPE text, ALTER COLUMN updated_at TYPE text;
\COPY students (id, user_token, created_at, updated_at) TO 'students.csv' CSV HEADER
\COPY tracked_sessions(id, student_id, created_at, updated_at) TO 'tracked_sessions.csv' CSV HEADER
EOF
java LAnonymizer -cp ./libarx-3.8.0.jar
sed -i '1d' students_new.csv
sed -i '1d' tracked_sessions_new.csv
psql -h localhost -p 5432 -U postgres << EOF
\c learning_analytics_development
\COPY students_anonymized(id, user_token, created_at, updated_at) FROM 'students_new.csv' WITH (FORMAT csv);
\COPY tracked_sessions_anonymized(id, student_id, created_at, updated_at) FROM 'tracked_sessions_new.csv' WITH (FORMAT csv);
update tracked_sessions_anonymized set tracking_json = tracked_sessions.tracking_json from tracked_sessions_anonymized t1 inner join tracked_sessions on t1.id = tracked_sessions.id;
EOF


