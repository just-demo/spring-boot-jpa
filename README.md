// docker run -d -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres postgres
// docker run -d -p 27017:27017 --name mongo mongo
cd db
docker-compose up

// There is no need to run the scripts below because Hibernate will create everything on startup
drop table "user";
drop table post;
drop table comment;

create table "user" (
	id bigint not null,
	"name" varchar(255),
	created_by varchar(255),
	created_date timestamp,
	constraint user_pkey primary key (id)
);

create table post (
	id bigint not null,
	title varchar(255),
	body varchar(255),
	user_id bigint,
	constraint post_pkey primary key (id)
);

create table comment (
	id bigint not null,
	body varchar(255),
	post_id bigint,
	user_id bigint,
	constraint comment_pkey primary key (id)
);