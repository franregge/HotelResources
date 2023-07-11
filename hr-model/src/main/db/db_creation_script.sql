DROP TABLE IF EXISTS public.bookings;
DROP TABLE IF EXISTS public.rooms;
DROP TABLE IF EXISTS public.hotels;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.roles_server_permissions;
DROP TABLE IF EXISTS public.server_permissions;
DROP TABLE IF EXISTS public.roles CASCADE;
DROP TABLE IF EXISTS public.countries;
DROP TABLE IF EXISTS public.countries;
DROP TABLE IF EXISTS public.roles_users;
DROP TABLE IF EXISTS public.shifts;
DROP TABLE IF EXISTS public.users_days_off;
DROP TABLE IF EXISTS public.hotel_employees;
DROP TABLE IF EXISTS public.hotel_shifts;


CREATE TABLE public.hotels
(
    id               SERIAL       NOT NULL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    number_of_floors INT          NOT NULL
);

CREATE TABLE public.rooms
(
    id             SERIAL  NOT NULL PRIMARY KEY,
    room_number    INT     NOT NULL,
    hotel_id       INT     NOT NULL,
    number_of_beds INT     NOT NULL,
    base_price     NUMERIC NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES public.hotels (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.countries
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);


CREATE TABLE public.users
(
    login_name    VARCHAR(255) NOT NULL PRIMARY KEY,
    user_name     VARCHAR(255) NOT NULL,
    surname1      VARCHAR(255) NOT NULL,
    surname2      VARCHAR(255),
    id_document   VARCHAR(255) NOT NULL,
    country_id    INT          NOT NULL,
    phone_number  VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL,
    shift_id INT,
    hotel_id INT not null,
    foreign key (country_id) references public.countries (id)
        on delete restrict
        on update cascade,
    foreign key(shift_id) references public.shifts(id)
        on delete set null
        on update cascade,
    foreign key(hotel_id) references public.hotels(id)
        on delete restrict
        on update cascade
);

CREATE TABLE public.bookings
(
    id             SERIAL       NOT NULL PRIMARY KEY,
    room_id        INT          NOT NULL,
    arrival_date   DATE         NOT NULL,
    departure_date DATE         NOT NULL,
    login_name     VARCHAR(255) NOT NULL,
    foreign key (login_name) references public.users (login_name) ON DELETE RESTRICT ON UPDATE restrict,
    FOREIGN KEY (room_id) REFERENCES public.rooms (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX CONCURRENTLY u_hotel_id_room_number
    ON rooms (hotel_id, room_number);

ALTER TABLE rooms
    ADD CONSTRAINT u_hotel_id_room_number
        UNIQUE USING INDEX u_hotel_id_room_number;

CREATE TABLE public.roles
(
    id                    SERIAL PRIMARY KEY,
    rolename              VARCHAR(255)      NOT NULL,
    xml_client_permission VARCHAR(10485760) NOT NULL DEFAULT ('<?xml version="1.0" encoding="UTF-8"?><security></security>')
);

CREATE TABLE public.server_permissions
(
    server_permission_id SERIAL PRIMARY KEY,
    method               VARCHAR(255) NOT NULL
);

CREATE TABLE public.roles_server_permissions
(
    id                   SERIAL PRIMARY KEY,
    role_id              INT,
    server_permission_id INT,
    FOREIGN KEY (role_id) references public.roles (role_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (server_permission_id) references public.server_permissions (server_permission_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.roles_users
(
    id         SERIAL PRIMARY KEY,
    login_name varchar(255),
    role_id    INT,
    FOREIGN KEY (login_name) references public.users (login_name) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) references public.roles (role_id) ON DELETE CASCADE ON UPDATE CASCADE
);

create TABLE public.shifts
(
	id SERIAL primary key,
	role_id int,
	mon varchar(23),
	tue varchar(23),
	wed varchar(23),
	thu varchar(23),
	fri varchar(23),
	sat varchar(23),
	sun varchar(23),

	foreign key (role_id) references public.roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

create type day as enum ('monday','tuesday','wednesday','thursday','friday','saturday','sunday');

create table public.users_days_off
(
	id serial not null primary key,
	login_name varchar(255) not null,
	day varchar(9) not null,
	foreign key(login_name) references public.users(login_name) ON DELETE CASCADE ON UPDATE CASCADE
	);

CREATE UNIQUE INDEX CONCURRENTLY u_login_name_day
    ON users_days_off (login_name, day);

ALTER TABLE users_days_off
    ADD CONSTRAINT u_login_name_day
        UNIQUE USING INDEX u_login_name_day;

CREATE TABLE public.hotel_employees
(
	id SERIAL primary key,
	hotel_id    INT,
    login_name varchar(255),
    FOREIGN KEY (hotel_id) references public.hotels (id) ON DELETE CASCADE ON UPDATE cascade,
    FOREIGN KEY (login_name) references public.users (login_name) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.hotel_shifts
(
	id SERIAL primary key,
	shift_id INT,
    hotel_id INT,
    FOREIGN KEY (shift_id) references public.shifts (id) ON DELETE CASCADE ON UPDATE cascade,
    FOREIGN KEY (hotel_id) references public.hotels (id) ON DELETE CASCADE ON UPDATE CASCADE
);
