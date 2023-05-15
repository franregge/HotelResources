DROP TABLE IF EXISTS public.hotels;
CREATE TABLE public.hotels (
    id SERIAL NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL
);