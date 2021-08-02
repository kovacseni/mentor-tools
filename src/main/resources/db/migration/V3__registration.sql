create table registrations (id bigint not null auto_increment,
                            trainingclass_id bigint not null,
                            student_id bigint not null,
                            registration_status varchar(20) not null,
                            primary key (id));