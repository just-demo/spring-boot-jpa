-- Using sequence value to avoid conflicts with Hibernate when testing data creation,
-- it is ok even if Hibernate uses Hi/Lo algorithm for id generation
delete from comment;
delete from post;
delete from user;

alter sequence user_id_seq restart with 1;

insert into user (id, name) values (user_id_seq.nextval, 'user1');
insert into user (id, name) values (user_id_seq.nextval, 'user2');
