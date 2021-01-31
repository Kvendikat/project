create table protocols (
    id int unsigned unique not null auto_increment primary key,
    number varchar(30) not null,
    date timestamp not null,
    note varchar(255),
    producer_id int references producers(id),
    user_id int references users(id),
    laboratory_boss_id int references laboratory_boss(id),
    department_boss_id int references department_boss(id),
    plate_id int references plates(id)
);

create table plates (
    id int unsigned unique not null auto_increment primary key,
    number varchar(30) not null unique,
    length int not null,
    width int not null,
    height int not null,
    param_one double not null,
    param_two double not null,
    in_storage bit default true,
    has_crack bit default false,
    note varchar(255),
    what_for_id int references whatfor(id),
    protocol_id int references protocols(id),
    types_id int references types(id),
    materials_id int references materials(id),
    crash bit default false,
    archive bit default false
);

create table producers (
    id int unsigned unique not null auto_increment primary key,
    name varchar(40) not null,
    surname varchar(100),
    position varchar(100) not null,
    active bit default true
);

create table types (
    id int unsigned unique not null auto_increment primary key,
    name varchar(30) not null,
    active bit default true
);

create table what_for (
    id int unsigned unique not null auto_increment primary key,
    type varchar(30) not null,
    quantity int not null,
    active bit default true
);

create table department_boss(
    id int unsigned unique not null auto_increment primary key,
    name varchar(30) not null,
    active bit default true
);

create table laboratory_boss(
    id int unsigned unique not null auto_increment primary key,
    name varchar(30),
    active bit default true
);

create table materials (
    id int unsigned unique not null auto_increment primary key,
    name varchar(30),
    active bit default true
);

insert into types (id, name)
values
    (1, '100x100x40'),
    (2, '120x80x50'),
    (3, '90x90x45'),
    (4, '150x90x70'),
    (5, '200x70x50');

insert into producers (id, name, surname, position)
values
    (1, 'ООО Кирпич', 'А.Б.Тюльпанов', 'Представитель ООО Кирпич'),
    (2, 'АО Мастер', 'И.И.Березов', 'Представитель АО Мастер'),
    (3, 'ЗАО Строитель', 'В.Г.Дубров','Представитель ЗАО Строитель'),
    (4, 'ИП Мастерок', null, 'Представитель ИП Мастерок');

insert into what_for (id, type, quantity)
values
    (1, 'Малый', 16),
    (2, 'Средний', 8),
    (3, 'Большой', 4),
    (4, 'Огромный', 2);

insert into department_boss (id, name)
values
    (1, 'А.А. Булков'),
    (2, 'О.О. Хлебов');

insert into laboratory_boss (id, name)
values
    (1, 'Н.Н. Летова'),
    (2, 'В.В. Землянова');

insert into materials (id, name)
values
    (1, 'Состав 1'),
    (2, 'Состав 2'),
    (3, 'Состав 3'),
    (4, 'Состав 4'),
    (5, 'Состав 5');

insert into plates(id, number, length, width, height, param_one, param_two, in_storage, has_crack, note, what_for_id, protocol_id, types_id, materials_id)
values
    (1, 'A01.09.20', 100, 101, 42, 5, 12, true, false, 'скол', null, null, 3, 1),
    (2, 'A02.09.20', 99, 99, 40, 6, 15, true, false, null , null, null, 3, 1),
    (3, 'A03.09.19', 89, 90, 46, 2, 14, true, true, null, null, null, 3, 2),
    (4, 'B07.09.19', 91, 92, 44, 3, 8, true, true, 'скол', null, null, 3, 2),
    (5, 'B05.09.20', 201, 67, 53, 5, 17, true, false, 'скол', null, null, 3, 1),
    (6, 'C06.10.20', 204, 72, 49, 5, 22, true, false, 'скол', null, null, 3, 3),
    (7, 'C02.09.20', 120, 81, 50, 9, 12, true, true, 'скол', null, null, 4, 1),
    (8, 'C01.09.19', 119, 83, 50, 7, 15, false, false, 'скол', null, null, 4, 1),
    (9, 'D01.10.20', 150, 90, 70, 1, 18, false, false, 'скол', null, null, 5, 1),
    (10, 'D02.09.20', 148, 96, 73, 2, 13, false, false, 'скол', null, null, 5, 1);