create table trainingclasses (id bigint not null auto_increment,
                              training_class_name varchar(255) not null,
                              start_date date,
                              end_date date,
                              primary key (id));
