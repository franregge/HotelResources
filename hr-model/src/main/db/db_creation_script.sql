DROP TABLE IF EXISTS public.hotels;
CREATE TABLE public.hotels
(
    id               SERIAL       NOT NULL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    number_of_floors INT          NOT NULL
);

DROP TABLE IF EXISTS public.rooms;
CREATE TABLE public.rooms
(
    id          SERIAL NOT NULL PRIMARY KEY,
    room_number INT    NOT NULL,
    hotel_id    INT    NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES public.hotels (id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS public.bookings;
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
