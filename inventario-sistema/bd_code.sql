mysql -u root -p

mysqldump -u root -p inventario_sistema > /usr/local/tomcat/webapps/inventario-sistema/inventario_sistema.sql

sudo mysql -u root -p inventario_sistema < /usr/local/tomcat/webapps/inventario-sistema/inventario_sistema.sql


CREATE DATABASE inventario_sistema;

USE inventario_sistema;

CREATE TABLE producto (
    producto_id INT(2) PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50),
    precio DECIMAL(16,2),
    estatus INT(2) DEFAULT 1
);

CREATE TABLE venta (
    venta_id INT(2) PRIMARY KEY AUTO_INCREMENT,
    producto_id INT(2),
    cantidad INT(10),
    estatus INT DEFAULT 1,
    FOREIGN KEY (producto_id) REFERENCES producto(producto_id) ON DELETE CASCADE
);

CREATE TABLE rol (
    rol_id INT(2) PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50),
    estatus INT DEFAULT 1
);

CREATE TABLE permiso (
    permiso_id INT(2) PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50),
    estatus INT DEFAULT 1
);

CREATE TABLE rol_permiso (
    rol_id INT(2),
    permiso_id INT(2),
    estatus INT(2) DEFAULT 1,
    FOREIGN KEY (rol_id) REFERENCES rol(rol_id), 
    FOREIGN KEY (permiso_id) REFERENCES permiso(permiso_id) 
);

CREATE TABLE usuario (
    usuario_id INT(2) PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    correo VARCHAR(50),
    contrasenia VARCHAR(25),
    rol_id INT(2),
    FOREIGN KEY (rol_id) REFERENCES rol(rol_id) ON DELETE CASCADE
);

CREATE TABLE inventario (
    inventario_id INT(2) PRIMARY KEY AUTO_INCREMENT,
    producto_id INT(2),
    usuario_id INT(2),
    cantidad INT(2),
    tipo_movimiento ENUM('entrada', 'salida', 'nuevo'),
    fecha_movimiento DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id), 
    FOREIGN KEY (producto_id) REFERENCES producto(producto_id) ON DELETE CASCADE
);
