CREATE TABLE ark_uid_worker_node (
    id BIGINT NOT NULL PRIMARY KEY IDENTITY(1,1) ,
    created TIMESTAMP NOT NULL,
    host_name VARCHAR(64) NOT NULL,
    launch_date TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    port VARCHAR(64) NOT NULL,
    type SMALLINT NOT NULL
);