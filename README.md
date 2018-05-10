drop table "user";
drop table post;
drop table comment;

create table "user" (
	id bigint not null,
	"name" varchar(255),
	constraint pk_user primary key (id)
);

create table post (
	id bigint not null,
	title varchar(255),
	body varchar(255),
	user_id bigint,
	constraint pk_post primary key (id)
);

create table comment (
	id bigint not null,
	body varchar(255),
	post_id bigint,
	user_id bigint,
	constraint pk_comment primary key (id)
);