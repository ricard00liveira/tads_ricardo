insert into perfis(id,nome) values (1, 'ROLE_ADMIN');
insert into perfis(id,nome) values (2, 'ROLE_USER');

insert into usuarios(id,nome,sobrenome,email,senha, is_confirmado) values (1,'Admin','do Sistema','admin@email.com','$2a$10$HKveMsPlst41Ie2LQgpijO691lUtZ8cLfcliAO1DD9TtZxEpaEoJe', true);
insert into usuarios(id,nome,sobrenome,email,senha, is_confirmado) values (2,'Usuario','do Sistema','user@email.com','$2a$10$HKveMsPlst41Ie2LQgpijO691lUtZ8cLfcliAO1DD9TtZxEpaEoJe', true);

insert into usuarios_perfis(usuarios_id,perfis_id) values(1, 1);
insert into usuarios_perfis(usuarios_id,perfis_id) values(2, 2);

# insert into token_confirmacao_email(id, data_de_criacao, token, usuario_id) value (1,'2024-05-02 10:00','aecc73b7-dae5-4011-925d-e07633d9993f',1);
