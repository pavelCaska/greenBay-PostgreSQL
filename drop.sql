
    alter table if exists bids 
       drop constraint if exists FKmtrc6tnwawlpk1u2km6qnxbha;

    alter table if exists bids 
       drop constraint if exists FKg1mdb2uha9v6t2ujkvlmj3tuq;

    alter table if exists items 
       drop constraint if exists FKsm9ro5ntn6yaav2m7ydato0fc;

    alter table if exists purchases 
       drop constraint if exists FKpbgddohncjt0ijwi5pgdjng5u;

    alter table if exists purchases 
       drop constraint if exists FKh403vdm50hi3rwf8ukj5xubsc;

    drop table if exists bids cascade;

    drop table if exists items cascade;

    drop table if exists purchases cascade;

    drop table if exists users cascade;
