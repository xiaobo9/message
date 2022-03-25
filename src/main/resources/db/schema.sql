create schema MESSAGE;

create table t_user_sonar_issues (
     c_id              char(32),
     c_check_id        char(32)        null ,
     c_author          varchar(300)    null ,
     c_email           varchar(300)    null ,
     c_project_id      varchar(300)    null ,
     c_project_name    varchar(300)    null ,
     c_result          varchar(1000)   null ,
     dt_check_date     timestamp       null ,
     constraint pk_user_sonar_issues_id primary key( c_id )
);

CREATE INDEX i_user_sonar_issues_id
    ON t_user_sonar_issues (c_email);
COMMIT;
