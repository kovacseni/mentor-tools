create table mentors (id bigint not null auto_increment,
                       mentor_name varchar(255) not null,
                       mentor_email varchar(255) not null,
                       position varchar(20),
                       primary key (id));