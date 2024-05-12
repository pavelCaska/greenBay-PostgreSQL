CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists bids (
      bid_amount integer not null,
      id bigint not null,
      bidder_id uuid,
      item_id uuid,
      primary key (id)
);

create table items (
       last_bid integer not null,
       purchase_price integer not null,
       sellable boolean not null,
       starting_price integer not null,
       id uuid not null,
       seller_id uuid,
       description TEXT,
       name varchar(255),
       photourl varchar(255),
       primary key (id)
);

create table purchases (
       purchase_amount integer not null,
       id bigint not null,
       buyer_id uuid,
       item_id uuid,
       primary key (id)
);

create table users (
       balance float(53) not null,
--        id uuid DEFAULT uuid_generate_v4 (),
       id uuid not null,
       password varchar(255),
       roles varchar(255),
       username varchar(255) unique,
       primary key (id)
);

alter table if exists bids
    add constraint FKmtrc6tnwawlpk1u2km6qnxbha
        foreign key (bidder_id)
            references users;

alter table if exists bids
    add constraint FKg1mdb2uha9v6t2ujkvlmj3tuq
        foreign key (item_id)
            references items;

alter table if exists items
    add constraint FKsm9ro5ntn6yaav2m7ydato0fc
        foreign key (seller_id)
            references users;

alter table if exists purchases
    add constraint FKpbgddohncjt0ijwi5pgdjng5u
        foreign key (buyer_id)
            references users;

alter table if exists purchases
    add constraint FKh403vdm50hi3rwf8ukj5xubsc
        foreign key (item_id)
            references items;
