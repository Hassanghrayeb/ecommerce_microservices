-- Create Roles
insert into role
select 1, 'ADMIN', 'The admin role', true, 1, now() where not exists
(select 1 from role where id = 1);

insert into role
select 2, 'STANDARD', 'The standard role', false, 1, now() where not exists
(select 1 from role where id = 2);

---- Create Permissions ----
insert into permission
select 1, 'ADMIN', 'The admin permission' where not exists
(select 1 from permission where id = 1);

insert into permission (name, description)
select 'CAN_GET_USERS', 'permission to get users'
where not exists (select 1 from permission where name = 'CAN_GET_USERS');

insert into permission (name, description)
select 'CAN_EDIT_USERS', 'permission to edit users'
where not exists (select 1 from permission where name = 'CAN_EDIT_USERS');

insert into permission (name, description)
select 'CAN_DELETE_USERS', 'permission to delete users'
where not exists (select 1 from permission where name = 'CAN_DELETE_USERS');

insert into permission (name, description)
select 'CAN_GET_ROLES', 'permission to get roles'
where not exists (select 1 from permission where name = 'CAN_GET_ROLES');

insert into permission (name, description)
select 'CAN_EDIT_ROLES', 'permission to edit roles'
where not exists (select 1 from permission where name = 'CAN_EDIT_ROLES');

insert into permission (name, description)
select 'CAN_DELETE_ROLES', 'permission to delete roles'
where not exists (select 1 from permission where name = 'CAN_DELETE_ROLES');


---- Assign permissions to roles ----

-- assign the admin permission to the admin role
insert into role_permission select 1, 1 where not exists
(select 1 from role_permission where role_id = 1 and permission_id = 1);

insert into role_permission (role_id, permission_id)
select r.id, p.id
from role r, permission p
where r.name = 'ADMIN' and p.name = 'CAN_GET_USERS'
and not exists (select 1 from role_permission where role_id = r.id and permission_id = p.id);

insert into role_permission (role_id, permission_id)
select r.id, p.id
from role r, permission p
where r.name = 'ADMIN' and p.name = 'CAN_EDIT_USERS'
and not exists (select 1 from role_permission where role_id = r.id and permission_id = p.id);

insert into role_permission (role_id, permission_id)
select r.id, p.id
from role r, permission p
where r.name = 'ADMIN' and p.name = 'CAN_DELETE_USERS'
and not exists (select 1 from role_permission where role_id = r.id and permission_id = p.id);

insert into role_permission (role_id, permission_id)
select r.id, p.id
from role r, permission p
where r.name = 'ADMIN' and p.name = 'CAN_GET_ROLES'
and not exists (select 1 from role_permission where role_id = r.id and permission_id = p.id);

insert into role_permission (role_id, permission_id)
select r.id, p.id
from role r, permission p
where r.name = 'ADMIN' and p.name = 'CAN_EDIT_ROLES'
and not exists (select 1 from role_permission where role_id = r.id and permission_id = p.id);

insert into role_permission (role_id, permission_id)
select r.id, p.id
from role r, permission p
where r.name = 'ADMIN' and p.name = 'CAN_DELETE_ROLES'
and not exists (select 1 from role_permission where role_id = r.id and permission_id = p.id);


-- assign the role of admin to the admin login
insert into login_role select 1, 1, 1, now() where not exists
(select 1 from login_role where login_id = 1 and role_id = 1);
