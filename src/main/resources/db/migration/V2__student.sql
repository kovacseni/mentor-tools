create table students (id bigint not null auto_increment,
                       student_name varchar(255) not null,
                       email varchar(255) not null,
                       GitHub_profile varchar(255),
                       comment varchar(1000),
                       primary key (id));