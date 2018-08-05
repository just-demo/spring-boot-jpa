-- Using sequence value to avoid conflicts with Hibernate when testing data creation,
-- it is ok even if Hibernate uses Hi/Lo algorithm for id generation
delete from comment;
delete from post;
delete from user;

alter sequence user_id_seq restart with 1;

-- H2
insert into user (id, name) values (user_id_seq.nextval, 'user1');
insert into user (id, name) values (user_id_seq.nextval, 'user2');

-- HSQLDB
--insert into user (id, name) values (next value for user_id_seq, 'user1');
--insert into user (id, name) values (next value for user_id_seq, 'user2');

-- DERBY
--drop sequence users_id_seq restrict;
--create sequence users_id_seq start with 1;
--insert into users (id, name) values (next value for users_id_seq, 'user1');
--insert into users (id, name) values (next value for users_id_seq, 'user2');
