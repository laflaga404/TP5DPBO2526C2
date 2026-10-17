CREATE DATABASE IF NOT EXISTS db_people
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_people;

CREATE TABLE IF NOT EXISTS person (
    id VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    tahun_lahir SMALLINT UNSIGNED NOT NULL,
    pangkat VARCHAR(30) NOT NULL,
    email VARCHAR(120) NOT NULL,
    status ENUM('Aktif Dinas', 'Cuti Dinas') NOT NULL DEFAULT 'Aktif Dinas'
);

INSERT INTO person (id, nama, tahun_lahir, pangkat, email, status)
VALUES
('TNI001', 'Andi Pratama', 1998, 'Prada', 'andi@email.com', 'Aktif Dinas'),
('TNI002', 'Budi Santoso', 1995, 'Serda', 'budi@email.com', 'Aktif Dinas'),
('TNI003', 'Cahya Putra', 1990, 'Kapten', 'cahya@email.com', 'Aktif Dinas'),
('TNI004', 'Dimas Saputra', 1985, 'Mayor', 'dimas@email.com', 'Cuti Dinas'),
('TNI006', 'Joko Widodo', 1967, 'Kolonel', 'owi@email.com', 'Cuti Dinas'),
('TNI005', 'Eko Wijaya', 1975, 'Kolonel', 'eko@email.com', 'Aktif Dinas')
ON DUPLICATE KEY UPDATE
nama = VALUES(nama),
tahun_lahir = VALUES(tahun_lahir),
pangkat = VALUES(pangkat),
email = VALUES(email),
status = VALUES(status);
