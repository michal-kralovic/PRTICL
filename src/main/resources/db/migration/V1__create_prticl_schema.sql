CREATE TABLE IF NOT EXISTS locations (
    id SERIAL PRIMARY KEY,
    x NUMERIC,
    y NUMERIC,
    z NUMERIC,
    world TEXT
);

CREATE TABLE IF NOT EXISTS players (
    id SERIAL PRIMARY KEY,
    uuid TEXT NOT NULL,
    username TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS nodes (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    repeat_delay INTEGER,
    particle_density INTEGER,
    particle_type TEXT,
    is_spawned BOOLEAN NOT NULL,
    is_enabled BOOLEAN NOT NULL,
    location_id INTEGER,
    FOREIGN KEY(location_id) REFERENCES locations(id),
    player_id INTEGER NOT NULL,
    FOREIGN KEY(player_id) REFERENCES players(id)
);