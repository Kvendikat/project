create table users (
    id int unsigned unique not null auto_increment primary key,
    username varchar(100) not null unique,
    login varchar(100) not null unique,
    position varchar(100) not null,
    password varchar(255) not null,
    active bit default true,
    index (username)
);

create table user_roles (
    user_id int not null references users (id),
    role varchar (100) not null,
    primary key (user_id, role)
);