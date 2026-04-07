-- ============================================================
-- CiNect – V3: Full theatrical movie catalog (OMDb-sourced metadata)
-- Regenerate: node scripts/generate-movies-catalog.mjs && node scripts/generate-spring-movies-sql.mjs
-- ============================================================

-- Drop obsolete V2 demo films (fictional / placeholder titles)
DELETE FROM movie_genres WHERE movie_id IN (SELECT id FROM movies WHERE slug IN ('avengers-secret-wars', 'lat-mat-8-hoi-ket', 'inside-out-3', 'dune-part-three', 'mai-2', 'the-batman-2'));
DELETE FROM showtimes WHERE movie_id IN (SELECT id FROM movies WHERE slug IN ('avengers-secret-wars', 'lat-mat-8-hoi-ket', 'inside-out-3', 'dune-part-three', 'mai-2', 'the-batman-2'));
DELETE FROM movies WHERE slug IN ('avengers-secret-wars', 'lat-mat-8-hoi-ket', 'inside-out-3', 'dune-part-three', 'mai-2', 'the-batman-2');

-- Ensure movie IDs are generated when seeding rows.
-- Supabase typically has pgcrypto enabled; this default avoids needing explicit ids in VALUES.
ALTER TABLE movies ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE movies ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE movies ALTER COLUMN updated_at SET DEFAULT now();

INSERT INTO movies (title, original_title, slug, description, poster_url, banner_url, trailer_url, duration, release_date, director, cast_members, language, subtitles, rating, rating_count, age_rating, formats, status)
VALUES
  (
    'Deadpool & Wolverine',
    'Deadpool & Wolverine',
    'deadpool-wolverine',
    'Deadpool is offered a place in the Marvel Cinematic Universe by the Time Variance Authority, but instead recruits a variant of Wolverine to save his universe from extinction.',
    'https://m.media-amazon.com/images/M/MV5BZTk5ODY0MmQtMzA3Ni00NGY1LThiYzItZThiNjFiNDM4MTM3XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZTk5ODY0MmQtMzA3Ni00NGY1LThiYzItZThiNjFiNDM4MTM3XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    128,
    '2024-07-25',
    'Shawn Levy',
    '["Ryan Reynolds","Hugh Jackman","Emma Corrin"]'::jsonb,
    'English',
    'Vietnamese',
    7.5,
    125000,
    'C16'::age_rating,
    '["2D","IMAX","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Inside Out 2',
    'Inside Out 2',
    'inside-out-2',
    'A sequel that features Riley entering puberty and experiencing brand new, more complex emotions as a result. As Riley tries to adapt to her teenage years, her old emotions try to adapt to the possibility of being replaced.',
    'https://m.media-amazon.com/images/M/MV5BYWY3MDE2Y2UtOTE3Zi00MGUzLTg2MTItZjE1ZWVkMGVlODRmXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BYWY3MDE2Y2UtOTE3Zi00MGUzLTg2MTItZjE1ZWVkMGVlODRmXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    96,
    '2024-06-13',
    'Kelsey Mann',
    '["Amy Poehler","Maya Hawke","Kensington Tallman"]'::jsonb,
    'English',
    'Vietnamese',
    7.5,
    98000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Dune: Part Two',
    'Dune: Part Two',
    'dune-part-two',
    'Paul Atreides unites with the Fremen while on a warpath of revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the universe, he endeavors to prevent a terrible fu...',
    'https://m.media-amazon.com/images/M/MV5BNTc0YmQxMjEtODI5MC00NjFiLTlkMWUtOGQ5NjFmYWUyZGJhXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNTc0YmQxMjEtODI5MC00NjFiLTlkMWUtOGQ5NjFmYWUyZGJhXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    166,
    '2024-02-29',
    'Denis Villeneuve',
    '["Timothée Chalamet","Zendaya","Rebecca Ferguson"]'::jsonb,
    'English',
    'Vietnamese',
    8.4,
    210000,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Godzilla x Kong: The New Empire',
    'Godzilla x Kong: The New Empire',
    'godzilla-x-kong-the-new-empire',
    'Two ancient titans, Godzilla and Kong, clash in an epic battle as humans unravel their intertwined origins and connection to Skull Island''s mysteries.',
    'https://m.media-amazon.com/images/M/MV5BMTY0N2MzODctY2ExYy00OWYxLTkyNDItMTVhZGIxZjliZjU5XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMTY0N2MzODctY2ExYy00OWYxLTkyNDItMTVhZGIxZjliZjU5XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    115,
    '2024-03-28',
    'Adam Wingard',
    '["Rebecca Hall","Brian Tyree Henry","Dan Stevens"]'::jsonb,
    'English',
    'Vietnamese',
    6,
    89000,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Kung Fu Panda 4',
    'Kung Fu Panda 4',
    'kung-fu-panda-4',
    'After Po is tapped to become the Spiritual Leader of the Valley of Peace, he needs to find and train a new Dragon Warrior, while a wicked sorceress plans to re-summon all the master villains whom Po has vanquished to the spirit re...',
    'https://m.media-amazon.com/images/M/MV5BMzJlNGYxYzQtOTg4MC00OTMyLTkwYzMtZDRlNTgwY2YwOWYxXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMzJlNGYxYzQtOTg4MC00OTMyLTkwYzMtZDRlNTgwY2YwOWYxXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    94,
    '2024-03-07',
    'Mike Mitchell, Stephanie Stine',
    '["Jack Black","Awkwafina","Viola Davis"]'::jsonb,
    'English',
    'Vietnamese',
    6.3,
    76000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Furiosa: A Mad Max Saga',
    'Furiosa: A Mad Max Saga',
    'furiosa-a-mad-max-saga',
    'After being snatched from the Green Place of Many Mothers, while the tyrants Dementus and Immortan Joe fight for power and control, the young Furiosa must survive many trials as she puts together the means to find her way home.',
    'https://m.media-amazon.com/images/M/MV5BNTcwYWE1NTYtOWNiYy00NzY3LWIwY2MtNjJmZDkxNDNmOWE1XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNTcwYWE1NTYtOWNiYy00NzY3LWIwY2MtNjJmZDkxNDNmOWE1XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    148,
    '2024-05-23',
    'George Miller',
    '["Anya Taylor-Joy","Chris Hemsworth","Tom Burke"]'::jsonb,
    'English',
    'Vietnamese',
    7.5,
    112000,
    'C18'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Alien: Romulus',
    'Alien: Romulus',
    'alien-romulus',
    'While scavenging the deep ends of a derelict space station, a group of young space colonists come face to face with the most terrifying life form in the universe.',
    'https://m.media-amazon.com/images/M/MV5BMDU0NjcwOGQtNjNjOS00NzQ3LWIwM2YtYWVmODZjMzQzN2ExXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMDU0NjcwOGQtNjNjOS00NzQ3LWIwM2YtYWVmODZjMzQzN2ExXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    119,
    '2024-08-15',
    'Fede Alvarez',
    '["Cailee Spaeny","David Jonsson","Archie Renaux"]'::jsonb,
    'English',
    'Vietnamese',
    7.1,
    145000,
    'C18'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Beetlejuice Beetlejuice',
    'Beetlejuice Beetlejuice',
    'beetlejuice-beetlejuice',
    'After a family tragedy, three generations of the Deetz family return home to Winter River. Still haunted by Beetlejuice, Lydia''s life is turned upside down when her teenage daughter, Astrid, accidentally opens the portal to the Af...',
    'https://m.media-amazon.com/images/M/MV5BNTQ4Y2MzY2MtYzQyMS00ZTQ3LWIzZGQtNjVjYzgwYzA1MDkzXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNTQ4Y2MzY2MtYzQyMS00ZTQ3LWIzZGQtNjVjYzgwYzA1MDkzXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    105,
    '2024-09-05',
    'Tim Burton',
    '["Michael Keaton","Winona Ryder","Catherine O''Hara"]'::jsonb,
    'English',
    'Vietnamese',
    6.6,
    67000,
    'C13'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Joker: Folie à Deux',
    'Joker: Folie à Deux',
    'joker-folie-a-deux',
    'Struggling with his dual identity, failed comedian Arthur Fleck meets the love of his life, Harley Quinn, while incarcerated at Arkham State Hospital.',
    'https://m.media-amazon.com/images/M/MV5BNTRlNmU1NzEtODNkNC00ZGM3LWFmNzQtMjBlMWRiYTcyMGRhXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNTRlNmU1NzEtODNkNC00ZGM3LWFmNzQtMjBlMWRiYTcyMGRhXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    138,
    '2024-10-03',
    'Todd Phillips',
    '["Joaquin Phoenix","Lady Gaga","Brendan Gleeson"]'::jsonb,
    'English',
    'Vietnamese',
    5.2,
    198000,
    'C18'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Venom: The Last Dance',
    'Venom: The Last Dance',
    'venom-the-last-dance',
    'Eddie Brock and Venom must make a devastating decision as they''re pursued by a mysterious military man and alien monsters from Venom''s home world.',
    'https://m.media-amazon.com/images/M/MV5BZDMyYWU4NzItZDY0MC00ODE2LTkyYTMtMzNkNDdmYmFhZDg0XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZDMyYWU4NzItZDY0MC00ODE2LTkyYTMtMzNkNDdmYmFhZDg0XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    110,
    '2024-10-24',
    'Kelly Marcel',
    '["Tom Hardy","Chiwetel Ejiofor","Juno Temple"]'::jsonb,
    'English',
    'Vietnamese',
    6,
    72000,
    'C13'::age_rating,
    '["2D","IMAX","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Gladiator II',
    'Gladiator II',
    'gladiator-ii',
    'After his home is conquered by the tyrannical emperors who now lead Rome, Lucius is forced to enter the Colosseum and must look to his past to find strength to return the glory of Rome to its people.',
    'https://m.media-amazon.com/images/M/MV5BMWYzZTM5ZGQtOGE5My00NmM2LWFlMDEtMGNjYjdmOWM1MzA1XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMWYzZTM5ZGQtOGE5My00NmM2LWFlMDEtMGNjYjdmOWM1MzA1XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    148,
    '2024-11-21',
    'Ridley Scott',
    '["Paul Mescal","Denzel Washington","Pedro Pascal"]'::jsonb,
    'English',
    'Vietnamese',
    6.5,
    134000,
    'C16'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Moana 2',
    'Moana 2',
    'moana-2',
    'After receiving an unexpected call from her wayfinding ancestors, Moana must journey to the far seas of Oceania and into dangerous, long-lost waters for an adventure unlike anything she''s ever faced.',
    'https://m.media-amazon.com/images/M/MV5BZDUxNThhYTUtYjgxNy00MGQ4LTgzOTEtZjg1YTU5NTcwNThlXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZDUxNThhYTUtYjgxNy00MGQ4LTgzOTEtZjg1YTU5NTcwNThlXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    100,
    '2024-11-26',
    'David G. Derrick Jr., Jason Hand, Dana Ledoux Miller',
    '["Auli''i Cravalho","Dwayne Johnson","Hualalai Chung"]'::jsonb,
    'English',
    'Vietnamese',
    6.3,
    45000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Wicked',
    'Wicked',
    'wicked',
    'Elphaba, a young woman ridiculed for her green skin, and Galinda, a popular girl, become friends at Shiz University in the Land of Oz. After an encounter with the Wonderful Wizard of Oz, their friendship reaches a crossroads.',
    'https://m.media-amazon.com/images/M/MV5BOWMwYjYzYmMtMWQ2Ni00NWUwLTg2MzAtYzkzMDBiZDIwOTMwXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BOWMwYjYzYmMtMWQ2Ni00NWUwLTg2MzAtYzkzMDBiZDIwOTMwXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    160,
    '2024-11-21',
    'Jon M. Chu',
    '["Cynthia Erivo","Ariana Grande","Jeff Goldblum"]'::jsonb,
    'English',
    'Vietnamese',
    7.4,
    156000,
    'P'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Sonic the Hedgehog 3',
    'Sonic the Hedgehog 3',
    'sonic-the-hedgehog-3',
    'Sonic, Knuckles, and Tails reunite against a powerful new adversary, Shadow, a mysterious villain with powers unlike anything they have faced before. With their abilities outmatched, Team Sonic must seek out an unlikely alliance.',
    'https://m.media-amazon.com/images/M/MV5BMjZjNjE5NDEtOWJjYS00Mjk2LWI1ZDYtOWI1ZWI3MzRjM2UzXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMjZjNjE5NDEtOWJjYS00Mjk2LWI1ZDYtOWI1ZWI3MzRjM2UzXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    110,
    '2024-12-19',
    'Jeff Fowler',
    '["Jim Carrey","Ben Schwartz","Keanu Reeves"]'::jsonb,
    'English',
    'Vietnamese',
    6.9,
    41000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Transformers One',
    'Transformers One',
    'transformers-one',
    'The untold origin story of Optimus Prime and Megatron, better known as sworn enemies, but who once were friends bonded like brothers who changed the fate of Cybertron forever.',
    'https://m.media-amazon.com/images/M/MV5BZWI1ZDY1YTQtMjRkNy00ZDZhLWE3OTItMTIwNzliY2Y1MTZhXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZWI1ZDY1YTQtMjRkNy00ZDZhLWE3OTItMTIwNzliY2Y1MTZhXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    104,
    '2024-09-19',
    'Josh Cooley',
    '["Chris Hemsworth","Brian Tyree Henry","Scarlett Johansson"]'::jsonb,
    'English',
    'Vietnamese',
    7.6,
    52000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Mufasa: The Lion King',
    'Mufasa: The Lion King',
    'mufasa-the-lion-king',
    'Mufasa, a cub lost and alone, meets a sympathetic lion named Taka, the heir to a royal bloodline. The chance meeting sets in motion an expansive journey of a group of misfits searching for their destiny.',
    'https://m.media-amazon.com/images/M/MV5BYjBkOWUwODYtYWI3YS00N2I0LWEyYTktOTJjM2YzOTc3ZDNlXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BYjBkOWUwODYtYWI3YS00N2I0LWEyYTktOTJjM2YzOTc3ZDNlXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    118,
    '2024-12-19',
    'Barry Jenkins',
    '["Aaron Pierre","Kelvin Harrison Jr.","Tiffany Boone"]'::jsonb,
    'English',
    'Vietnamese',
    6.6,
    38000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Bad Boys: Ride or Die',
    'Bad Boys: Ride or Die',
    'bad-boys-ride-or-die',
    'When their late police captain gets linked to drug cartels, wisecracking Miami cops Mike Lowrey and Marcus Burnett embark on a dangerous mission to clear his name.',
    'https://m.media-amazon.com/images/M/MV5BZWNjZWUwNDgtYTM4ZC00Zjk0LTg3ZWItNGEyZmVkZTIxZDk0XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZWNjZWUwNDgtYTM4ZC00Zjk0LTg3ZWItNGEyZmVkZTIxZDk0XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    115,
    '2024-06-06',
    'Adil El Arbi, Bilall Fallah',
    '["Will Smith","Martin Lawrence","Vanessa Hudgens"]'::jsonb,
    'English',
    'Vietnamese',
    6.5,
    88000,
    'C16'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'The Wild Robot',
    'The Wild Robot',
    'the-wild-robot',
    'After a shipwreck, an intelligent robot called Roz is stranded on an uninhabited island. To survive the harsh environment, Roz bonds with the island''s animals and cares for an orphaned baby goose.',
    'https://m.media-amazon.com/images/M/MV5BZWNiZjVlZTUtNGUwYi00MjJmLTg2MDctNWEzYTJiMzY1ODc4XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZWNiZjVlZTUtNGUwYi00MjJmLTg2MDctNWEzYTJiMzY1ODc4XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    102,
    '2024-09-26',
    'Chris Sanders',
    '["Lupita Nyong''o","Pedro Pascal","Kit Connor"]'::jsonb,
    'English',
    'Vietnamese',
    8.2,
    62000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'A Quiet Place: Day One',
    'A Quiet Place: Day One',
    'a-quiet-place-day-one',
    'A young woman named Sam finds herself trapped in New York City during the early stages of an invasion by alien creatures with ultra-sensitive hearing.',
    'https://m.media-amazon.com/images/M/MV5BMDdjZTljZWMtMDIwNi00MTA5LTkxZmItNmY0NDA3ZDM0N2M2XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMDdjZTljZWMtMDIwNi00MTA5LTkxZmItNmY0NDA3ZDM0N2M2XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    99,
    '2024-06-27',
    'Michael Sarnoski',
    '["Lupita Nyong''o","Joseph Quinn","Alex Wolff"]'::jsonb,
    'English',
    'Vietnamese',
    6.3,
    99000,
    'C16'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Civil War',
    'Civil War',
    'civil-war-2024',
    'In a dystopian future, four journalists travel across the United States during a nation-wide conflict. While trying to survive, they aim to reach the White House to interview the president before he is overthrown.',
    'https://m.media-amazon.com/images/M/MV5BYTkzMjc0YzgtY2E0Yi00NDBlLWI0MWUtODY1ZjExMDAyOWZiXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BYTkzMjc0YzgtY2E0Yi00NDBlLWI0MWUtODY1ZjExMDAyOWZiXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    109,
    '2024-04-11',
    'Alex Garland',
    '["Kirsten Dunst","Wagner Moura","Cailee Spaeny"]'::jsonb,
    'English',
    'Vietnamese',
    7,
    121000,
    'C18'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Kingdom of the Planet of the Apes',
    'Kingdom of the Planet of the Apes',
    'kingdom-of-the-planet-of-the-apes',
    'Many years after the reign of Caesar, a young ape goes on a journey that will lead him to question everything he''s been taught about the past and make choices that will define a future for apes and humans alike.',
    'https://m.media-amazon.com/images/M/MV5BZDRlZTc3YTItOTk3Yi00NmU4LWFiOGUtNjgwMDZjNjIzNTU1XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZDRlZTc3YTItOTk3Yi00NmU4LWFiOGUtNjgwMDZjNjIzNTU1XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    145,
    '2024-05-09',
    'Wes Ball',
    '["Owen Teague","Freya Allan","Kevin Durand"]'::jsonb,
    'English',
    'Vietnamese',
    6.8,
    77000,
    'C13'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Ghostbusters: Frozen Empire',
    'Ghostbusters: Frozen Empire',
    'ghostbusters-frozen-empire',
    'When the discovery of an ancient artifact unleashes an evil force, Ghostbusters new and old must join forces to protect their home and save the world from a second ice age.',
    'https://m.media-amazon.com/images/M/MV5BOWI5NGUyMzUtMGFiNi00ZTc3LTk1YTQtMDU0NGRjNGIxZTZmXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BOWI5NGUyMzUtMGFiNi00ZTc3LTk1YTQtMDU0NGRjNGIxZTZmXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    115,
    '2024-03-21',
    'Gil Kenan',
    '["Paul Rudd","Carrie Coon","Finn Wolfhard"]'::jsonb,
    'English',
    'Vietnamese',
    6.1,
    54000,
    'C13'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Kraven the Hunter',
    'Kraven the Hunter',
    'kraven-the-hunter',
    'Kraven''s complex relationship with his ruthless father, Nikolai Kravinoff, starts him down a path of vengeance with brutal consequences, motivating him to become not only the greatest hunter in the world, but also one of its most ...',
    'https://m.media-amazon.com/images/M/MV5BZDU0YTI5ODAtN2NmMS00YTg3LTgyNDItN2RmOWEzOTkzZjcyXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZDU0YTI5ODAtN2NmMS00YTg3LTgyNDItN2RmOWEzOTkzZjcyXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    127,
    '2024-12-12',
    'J.C. Chandor',
    '["Aaron Taylor-Johnson","Ariana DeBose","Fred Hechinger"]'::jsonb,
    'English',
    'Vietnamese',
    5.5,
    43000,
    'C16'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Despicable Me 4',
    'Despicable Me 4',
    'despicable-me-4',
    'Gru, Lucy, Margo, Edith, and Agnes welcome a new member to the family, Gru Jr., who is intent on tormenting his dad. Gru faces a new nemesis in Maxime Le Mal and his girlfriend Valentina, and the family is forced to go on the run.',
    'https://m.media-amazon.com/images/M/MV5BNzY0ZTlhYzgtOTgzZC00ZTg2LTk4NTEtZDllM2E2NGE5Njg2XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNzY0ZTlhYzgtOTgzZC00ZTg2LTk4NTEtZDllM2E2NGE5Njg2XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    94,
    '2024-07-02',
    'Chris Renaud, Patrick Delage',
    '["Steve Carell","Kristen Wiig","Pierre Coffin"]'::jsonb,
    'English',
    'Vietnamese',
    6.2,
    51000,
    'P'::age_rating,
    '["2D","3D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Mai',
    'Mai',
    'mai-2024',
    'Restlessly haunted by the past, Mai is greeted by a new dawn when she reluctantly befriends the neighborhood ladies'' man. But when her yesterday catches up to her today, what will become of her tomorrow?',
    'https://m.media-amazon.com/images/M/MV5BNTgwNDBhOGItNTIxZC00ZjMzLWFhZTYtOGNiM2MzYWViMWUwXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNTgwNDBhOGItNTIxZC00ZjMzLWFhZTYtOGNiM2MzYWViMWUwXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    131,
    '2024-02-09',
    'Tran Thanh',
    '["Huynh Uyen An","Hong Dao","Phuong Anh Dao"]'::jsonb,
    'Vietnamese',
    NULL,
    6.8,
    34000,
    'C16'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Face Off 7: One Wish',
    'Face Off 7: One Wish',
    'lat-mat-7-mot-dieu-uoc',
    'When a 73-year-old widow is forced to depend on the care of her 5 busy adult children after an injury, she begins to question the meaning of family love.',
    'https://m.media-amazon.com/images/M/MV5BMGY2MWU4YTUtMTUxMi00NTk5LWI3ODQtZDZkNzIxNWU0MjgxXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMGY2MWU4YTUtMTUxMi00NTk5LWI3ODQtZDZkNzIxNWU0MjgxXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    138,
    '2024-04-25',
    'Ly Hai',
    '["Tram Anh","Truong Minh Cuong","Ceri Thu Ha"]'::jsonb,
    'Vietnamese',
    NULL,
    7.3,
    28000,
    'C13'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'The 4 Rascals',
    'The 4 Rascals',
    'bo-tu-bao-thu',
    'The 4 rascals decide to take matters into their own hands and plot their own plan to "solve" a love triangle.',
    'https://m.media-amazon.com/images/M/MV5BNTQ5YjZiNTgtZmY1Ny00NzIxLTkyNGEtZjhiNjA0ZTAyNTg0XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNTQ5YjZiNTgtZmY1Ny00NzIxLTkyNGEtZjhiNjA0ZTAyNTg0XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    132,
    '2025-01-28',
    'Tran Thanh',
    '["Tran Thanh","Le Giang","Le Duong Bao Lam"]'::jsonb,
    'Vietnamese',
    NULL,
    5.5,
    12000,
    'C16'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Tunnel',
    'Tunnel',
    'dia-dao-mat-troi-trong-bong-toi',
    'In 1967, as the Vietnam War raged, a Vietnamese revolutionary guerrilla team became the U.S. military''s top target - charged with safeguarding a secret group of intelligence agents at all costs.',
    'https://m.media-amazon.com/images/M/MV5BNzQzYzNkNjMtZGExNy00OTliLWFiYjgtYTc4MGFiZGY4YmJkXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNzQzYzNkNjMtZGExNy00OTliLWFiYjgtYTc4MGFiZGY4YmJkXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    128,
    '2025-04-03',
    'Bùi Thac Chuyên',
    '["Ngoc Chi Bao","John Bourdon","Damien Cole"]'::jsonb,
    'Vietnamese',
    NULL,
    7.4,
    22000,
    'C16'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Betting with Ghost',
    'Betting with Ghost',
    'lam-giau-voi-ma',
    'Centers on a man who becomes a ''God of Gamblers'' suddenly with the help of the Ghost.',
    'https://m.media-amazon.com/images/M/MV5BNWU0OGNmZmEtNmU1Yi00MzdlLTkwNDItMWRlZTZiZTM2ZTUxXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNWU0OGNmZmEtNmU1Yi00MzdlLTkwNDItMWRlZTZiZTM2ZTUxXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    120,
    '2024-09-01',
    'Nguyen Nhat Trung',
    '["Tuan Tran"]'::jsonb,
    'Vietnamese',
    NULL,
    0,
    9000,
    'C16'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Ma Da: The Drowning Spirit',
    'Ma Da: The Drowning Spirit',
    'ma-da-the-drowning-spirit',
    'Corpse collector Le confronts MA DA - the vengeful drowning spirit, after it abducts her daughter. Determined to save her child, Mrs. Le embarks on a desperate rescue to prevent her child''s ghostly fate beneath the Mekong River.',
    'https://m.media-amazon.com/images/M/MV5BMGRkMDNkNmEtNDIxNi00Njg2LWJkYTMtNmNlNTM1NjljY2EzXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMGRkMDNkNmEtNDIxNi00Njg2LWJkYTMtNmNlNTM1NjljY2EzXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    90,
    '2024-08-15',
    'Nguyen Huu Hoang',
    '["Da Chuc","Trung Dan","Dieu Duc"]'::jsonb,
    'Vietnamese',
    NULL,
    5,
    8000,
    'C18'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Quy Cau',
    'Quy Cau',
    'quy-cau',
    'The return of Nam and his girlfriend Xuan brings countless troubles to the family. He gradually discovers the broken relationships of the family members.',
    'https://m.media-amazon.com/images/M/MV5BOGUxMDE5ZGQtODgyZS00NWZmLWFjZDEtOGM1YmVmOTViMzkyXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BOGUxMDE5ZGQtODgyZS00NWZmLWFjZDEtOGM1YmVmOTViMzkyXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    99,
    '2023-12-21',
    'Luu Thanh Luan',
    '["Vân Dung","Mie","Hanh Thuy Ngo Pham"]'::jsonb,
    'Vietnamese',
    NULL,
    5.2,
    7000,
    'C16'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Dao, Pho Va Piano',
    'Dao, Pho Va Piano',
    'dao-pho-va-piano',
    'In 1946 Hanoi battle, a Hanoian fighter hides at pho vendors'' homes to retrieve arms from French soldiers, offering pho as a bargain for chives.',
    'https://m.media-amazon.com/images/M/MV5BOWIxOWUxNzktMWY5OS00M2NkLThiY2UtNGFkNTY0YzNkYzZjXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BOWIxOWUxNzktMWY5OS00M2NkLThiY2UtNGFkNTY0YzNkYzZjXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    100,
    '2024-11-16',
    'Phi Tien Son',
    '["Doan Quoc Dam","Tuan Hung","Cao Thi Thuy Linh"]'::jsonb,
    'Vietnamese',
    NULL,
    0,
    6000,
    'C13'::age_rating,
    '["2D"]'::jsonb,
    'NOW_SHOWING'::movie_status
  ),
  (
    'Avatar: Fire and Ash',
    'Avatar: Fire and Ash',
    'avatar-fire-and-ash',
    'Jake and Neytiri''s family grapples with grief, encountering a new, aggressive Na''vi tribe, the Ash People, who are led by the fiery Varang, as the conflict on Pandora escalates and a new moral focus emerges.',
    'https://m.media-amazon.com/images/M/MV5BZDYxY2I1OGMtN2Y4MS00ZmU1LTgyNDAtODA0MzAyYjI0N2Y2XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZDYxY2I1OGMtN2Y4MS00ZmU1LTgyNDAtODA0MzAyYjI0N2Y2XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    197,
    '2025-12-18',
    'James Cameron',
    '["Sam Worthington","Zoe Saldaña","Sigourney Weaver"]'::jsonb,
    'English',
    'Vietnamese',
    7.4,
    0,
    'C13'::age_rating,
    '["2D","3D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'The Fantastic Four: First Steps',
    'The Fantastic Four: First Steps',
    'fantastic-four-first-steps',
    'Forced to balance their roles as heroes with the strength of their family bond, the Fantastic Four must defend Earth from a ravenous space god called Galactus and his enigmatic herald, the Silver Surfer.',
    'https://m.media-amazon.com/images/M/MV5BOGM5MzA3MDAtYmEwMi00ZDNiLTg4MDgtMTZjOTc0ZGMyNTIwXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BOGM5MzA3MDAtYmEwMi00ZDNiLTg4MDgtMTZjOTc0ZGMyNTIwXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    115,
    '2025-07-24',
    'Matt Shakman',
    '["Pedro Pascal","Vanessa Kirby","Ebon Moss-Bachrach"]'::jsonb,
    'English',
    'Vietnamese',
    6.8,
    0,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'Mission: Impossible - The Final Reckoning',
    'Mission: Impossible - The Final Reckoning',
    'mission-impossible-the-final-reckoning',
    'Hunt and the IMF pursue a dangerous AI called the Entity that''s infiltrated global intelligence. With governments and a figure from his past in pursuit, Hunt races to stop it from forever changing the world.',
    'https://m.media-amazon.com/images/M/MV5BZGQ5NGEyYTItMjNiMi00Y2EwLTkzOWItMjc5YjJiMjMyNTI0XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BZGQ5NGEyYTItMjNiMi00Y2EwLTkzOWItMjc5YjJiMjMyNTI0XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    169,
    '2025-05-22',
    'Christopher McQuarrie',
    '["Tom Cruise","Hayley Atwell","Ving Rhames"]'::jsonb,
    'English',
    'Vietnamese',
    7.2,
    0,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'Superman',
    'Superman',
    'superman-2025',
    'Superman must reconcile his alien Kryptonian heritage with his human upbringing as reporter Clark Kent. As the embodiment of truth, justice and the American way he soon finds himself in a world that views these as old-fashioned.',
    'https://m.media-amazon.com/images/M/MV5BOGMwZGJiM2EtMzEwZC00YTYzLWIxNzYtMmJmZWNlZjgxZTMwXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BOGMwZGJiM2EtMzEwZC00YTYzLWIxNzYtMmJmZWNlZjgxZTMwXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    129,
    '2025-07-10',
    'James Gunn',
    '["David Corenswet","Rachel Brosnahan","Nicholas Hoult"]'::jsonb,
    'English',
    'Vietnamese',
    7,
    0,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'Thunderbolts*',
    'Thunderbolts*',
    'thunderbolts',
    'After finding themselves ensnared in a death trap, an unconventional team of antiheroes must go on a dangerous mission that will force them to confront the darkest corners of their pasts.',
    'https://m.media-amazon.com/images/M/MV5BYWE2NmNmYTItZGY0ZC00MmY2LTk1NDAtMGUyMGEzMjcxNWM0XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BYWE2NmNmYTItZGY0ZC00MmY2LTk1NDAtMGUyMGEzMjcxNWM0XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    127,
    '2025-05-01',
    'Jake Schreier',
    '["Florence Pugh","Sebastian Stan","Julia Louis-Dreyfus"]'::jsonb,
    'English',
    'Vietnamese',
    7.1,
    0,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'The Batman: Part II',
    'The Batman: Part II',
    'the-batman-part-ii',
    'Except for the bat signal in the final shot of "The Penguin," we never got our Robert Pattinson Batman cameo in 8 episodes charting Oz Cobb''s brutal rise from mafia chauffeur to Gotham City crime boss. In this short explainer, IMDb tries to answer: Where the heck was Batman? And what can we expect from director Matt Reeves''s ''The Batman: Part II'' (2026).',
    'https://m.media-amazon.com/images/M/MV5BMTU2NzhiYWUtYThlZi00OWIyLTk3YWEtZjY3NmJjOTZiZDAyXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMTU2NzhiYWUtYThlZi00OWIyLTk3YWEtZjY3NmJjOTZiZDAyXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    120,
    '2027-09-30',
    'Matt Reeves',
    '["Robert Pattinson","Colin Farrell","Andy Serkis"]'::jsonb,
    'English',
    'Vietnamese',
    0,
    0,
    'C16'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'Spider-Man: Brand New Day',
    'Spider-Man: Brand New Day',
    'spider-man-brand-new-day',
    'Peter Parker tries to focus on college and leave Spider-Man behind. But when a new threat endangers his friends, he must break his promise and suit up again, teaming with an unexpected ally to protect those he loves.',
    'https://m.media-amazon.com/images/M/MV5BMTJhZGE3NmYtYTg0Ny00MWUzLWE0MmUtYTZjYTg1ZjVlMWRkXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BMTJhZGE3NmYtYTg0Ny00MWUzLWE0MmUtYTZjYTg1ZjVlMWRkXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    120,
    '2026-07-30',
    'Destin Daniel Cretton',
    '["Sadie Sink","Jon Bernthal","Zendaya"]'::jsonb,
    'English',
    'Vietnamese',
    0,
    0,
    'C13'::age_rating,
    '["2D","3D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'Jurassic World: Rebirth',
    'Jurassic World: Rebirth',
    'jurassic-world-rebirth',
    'Five years post-Jurassic World: Dominion (2022), an expedition braves isolated equatorial regions to extract DNA from three massive prehistoric creatures for a groundbreaking medical breakthrough.',
    'https://m.media-amazon.com/images/M/MV5BNjg2NTcwYWQtYzk4NS00MTJhLWEzZjItMzIxNjk3YzlkYzU0XkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BNjg2NTcwYWQtYzk4NS00MTJhLWEzZjItMzIxNjk3YzlkYzU0XkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    133,
    '2025-07-01',
    'Gareth Edwards',
    '["Scarlett Johansson","Mahershala Ali","Jonathan Bailey"]'::jsonb,
    'English',
    'Vietnamese',
    5.8,
    0,
    'C13'::age_rating,
    '["2D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  ),
  (
    'How to Train Your Dragon',
    'How to Train Your Dragon',
    'how-to-train-your-dragon-2025',
    'As an ancient threat endangers both Vikings and dragons alike on the isle of Berk, the friendship between Hiccup, an inventive Viking, and Toothless, a Night Fury dragon, becomes the key to both species forging a new future together.',
    'https://m.media-amazon.com/images/M/MV5BODA5Y2M0NjctNWQzMy00ODRhLWE0MzUtYmE1YTAzZjYyYmQyXkEyXkFqcGc@._V1_SX300.jpg',
    'https://m.media-amazon.com/images/M/MV5BODA5Y2M0NjctNWQzMy00ODRhLWE0MzUtYmE1YTAzZjYyYmQyXkEyXkFqcGc@._V1_SX1280.jpg',
    NULL,
    125,
    '2025-06-12',
    'Dean DeBlois',
    '["Mason Thames","Nico Parker","Gerard Butler"]'::jsonb,
    'English',
    'Vietnamese',
    7.7,
    0,
    'P'::age_rating,
    '["2D","3D","IMAX"]'::jsonb,
    'COMING_SOON'::movie_status
  )
ON CONFLICT (slug) DO UPDATE SET
  title = EXCLUDED.title,
  original_title = EXCLUDED.original_title,
  description = EXCLUDED.description,
  poster_url = EXCLUDED.poster_url,
  banner_url = EXCLUDED.banner_url,
  trailer_url = EXCLUDED.trailer_url,
  duration = EXCLUDED.duration,
  release_date = EXCLUDED.release_date,
  director = EXCLUDED.director,
  cast_members = EXCLUDED.cast_members,
  language = EXCLUDED.language,
  subtitles = EXCLUDED.subtitles,
  rating = EXCLUDED.rating,
  rating_count = EXCLUDED.rating_count,
  age_rating = EXCLUDED.age_rating,
  formats = EXCLUDED.formats,
  status = EXCLUDED.status;


-- Movie ↔ genre links (idempotent refresh per slug)
DELETE FROM movie_genres WHERE movie_id IN (SELECT id FROM movies WHERE slug IN ('deadpool-wolverine', 'inside-out-2', 'dune-part-two', 'godzilla-x-kong-the-new-empire', 'kung-fu-panda-4', 'furiosa-a-mad-max-saga', 'alien-romulus', 'beetlejuice-beetlejuice', 'joker-folie-a-deux', 'venom-the-last-dance', 'gladiator-ii', 'moana-2', 'wicked', 'sonic-the-hedgehog-3', 'transformers-one', 'mufasa-the-lion-king', 'bad-boys-ride-or-die', 'the-wild-robot', 'a-quiet-place-day-one', 'civil-war-2024', 'kingdom-of-the-planet-of-the-apes', 'ghostbusters-frozen-empire', 'kraven-the-hunter', 'despicable-me-4', 'mai-2024', 'lat-mat-7-mot-dieu-uoc', 'bo-tu-bao-thu', 'dia-dao-mat-troi-trong-bong-toi', 'lam-giau-voi-ma', 'ma-da-the-drowning-spirit', 'quy-cau', 'dao-pho-va-piano', 'avatar-fire-and-ash', 'fantastic-four-first-steps', 'mission-impossible-the-final-reckoning', 'superman-2025', 'thunderbolts', 'the-batman-part-ii', 'spider-man-brand-new-day', 'jurassic-world-rebirth', 'how-to-train-your-dragon-2025'));
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'deadpool-wolverine' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'deadpool-wolverine' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'deadpool-wolverine' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'inside-out-2' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'inside-out-2' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'inside-out-2' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dune-part-two' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dune-part-two' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dune-part-two' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'godzilla-x-kong-the-new-empire' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'godzilla-x-kong-the-new-empire' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'godzilla-x-kong-the-new-empire' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kung-fu-panda-4' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kung-fu-panda-4' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kung-fu-panda-4' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'furiosa-a-mad-max-saga' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'furiosa-a-mad-max-saga' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'furiosa-a-mad-max-saga' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'alien-romulus' AND g.slug = 'horror' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'alien-romulus' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'alien-romulus' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'beetlejuice-beetlejuice' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'beetlejuice-beetlejuice' AND g.slug = 'fantasy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'beetlejuice-beetlejuice' AND g.slug = 'horror' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'joker-folie-a-deux' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'joker-folie-a-deux' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'joker-folie-a-deux' AND g.slug = 'romance' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'venom-the-last-dance' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'venom-the-last-dance' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'venom-the-last-dance' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'gladiator-ii' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'gladiator-ii' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'gladiator-ii' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'moana-2' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'moana-2' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'moana-2' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'wicked' AND g.slug = 'fantasy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'wicked' AND g.slug = 'romance' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'wicked' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'sonic-the-hedgehog-3' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'sonic-the-hedgehog-3' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'sonic-the-hedgehog-3' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'transformers-one' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'transformers-one' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'transformers-one' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mufasa-the-lion-king' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mufasa-the-lion-king' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mufasa-the-lion-king' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'bad-boys-ride-or-die' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'bad-boys-ride-or-die' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'bad-boys-ride-or-die' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'the-wild-robot' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'the-wild-robot' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'the-wild-robot' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'a-quiet-place-day-one' AND g.slug = 'horror' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'a-quiet-place-day-one' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'a-quiet-place-day-one' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'civil-war-2024' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'civil-war-2024' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'civil-war-2024' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kingdom-of-the-planet-of-the-apes' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kingdom-of-the-planet-of-the-apes' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kingdom-of-the-planet-of-the-apes' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'ghostbusters-frozen-empire' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'ghostbusters-frozen-empire' AND g.slug = 'fantasy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'ghostbusters-frozen-empire' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kraven-the-hunter' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kraven-the-hunter' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'kraven-the-hunter' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'despicable-me-4' AND g.slug = 'animation' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'despicable-me-4' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'despicable-me-4' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mai-2024' AND g.slug = 'romance' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mai-2024' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'lat-mat-7-mot-dieu-uoc' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'lat-mat-7-mot-dieu-uoc' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'bo-tu-bao-thu' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'bo-tu-bao-thu' AND g.slug = 'romance' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'bo-tu-bao-thu' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dia-dao-mat-troi-trong-bong-toi' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dia-dao-mat-troi-trong-bong-toi' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dia-dao-mat-troi-trong-bong-toi' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'lam-giau-voi-ma' AND g.slug = 'comedy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'lam-giau-voi-ma' AND g.slug = 'horror' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'ma-da-the-drowning-spirit' AND g.slug = 'horror' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'ma-da-the-drowning-spirit' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'quy-cau' AND g.slug = 'horror' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'quy-cau' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dao-pho-va-piano' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'dao-pho-va-piano' AND g.slug = 'romance' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'avatar-fire-and-ash' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'avatar-fire-and-ash' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'avatar-fire-and-ash' AND g.slug = 'fantasy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'fantastic-four-first-steps' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'fantastic-four-first-steps' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'fantastic-four-first-steps' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mission-impossible-the-final-reckoning' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mission-impossible-the-final-reckoning' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'mission-impossible-the-final-reckoning' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'superman-2025' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'superman-2025' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'superman-2025' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'thunderbolts' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'thunderbolts' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'thunderbolts' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'the-batman-part-ii' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'the-batman-part-ii' AND g.slug = 'thriller' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'the-batman-part-ii' AND g.slug = 'drama' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'spider-man-brand-new-day' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'spider-man-brand-new-day' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'spider-man-brand-new-day' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'jurassic-world-rebirth' AND g.slug = 'action' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'jurassic-world-rebirth' AND g.slug = 'sci-fi' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'jurassic-world-rebirth' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'how-to-train-your-dragon-2025' AND g.slug = 'fantasy' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'how-to-train-your-dragon-2025' AND g.slug = 'adventure' ON CONFLICT DO NOTHING;
INSERT INTO movie_genres (movie_id, genre_id) SELECT m.id, g.id FROM movies m, genres g WHERE m.slug = 'how-to-train-your-dragon-2025' AND g.slug = 'drama' ON CONFLICT DO NOTHING;