create table trainingclasses (id bigint not null auto_increment,
                              trainingclass_name varchar(255) not null,
                              start_date date,
                              end_date date,
                              syllabus_id bigint,
                              primary key (id));