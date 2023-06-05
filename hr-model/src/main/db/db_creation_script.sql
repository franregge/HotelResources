DROP TABLE IF EXISTS public.bookings;
DROP TABLE IF EXISTS public.rooms;
DROP TABLE IF EXISTS public.hotels;
DROP TABLE IF EXISTS public.users;
CREATE TABLE public.hotels
(
    id               SERIAL       NOT NULL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    number_of_floors INT          NOT NULL
);

CREATE TABLE public.rooms
(
    id          SERIAL NOT NULL PRIMARY KEY,
    room_number INT    NOT NULL,
    hotel_id    INT    NOT NULL,
    number_of_beds INT NOT NULL,
    base_price NUMERIC NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES public.hotels (id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE public.bookings
(
    id             SERIAL       NOT NULL PRIMARY KEY,
    room_id        INT          NOT NULL,
    arrival_date   DATE         NOT NULL,
    departure_date DATE         NOT NULL,
    dni            CHAR(9)      NOT NULL,
    name           VARCHAR(100) NOT NULL,
    surname1       VARCHAR(100) NOT NULL,
    surname2       VARCHAR(100),
    FOREIGN KEY (room_id) REFERENCES public.rooms (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX CONCURRENTLY u_hotel_id_room_number
    ON rooms (hotel_id, room_number);

ALTER TABLE rooms
    ADD CONSTRAINT u_hotel_id_room_number
        UNIQUE USING INDEX u_hotel_id_room_number;


CREATE TABLE public.users (
  id SERIAL PRIMARY KEY,
  user_name VARCHAR(255) NOT NULL,
  surname1 VARCHAR(255) NOT NULL,
  surname2 VARCHAR(255),
  id_document VARCHAR(255) NOT NULL,
  country VARCHAR(255) NOT NULL,
  phone_number VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  user_password VARCHAR(255) NOT NULL
);
