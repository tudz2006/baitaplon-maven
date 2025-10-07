-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Máy chủ: localhost:3306
-- Thời gian đã tạo: Th9 15, 2025 lúc 09:57 PM
-- Phiên bản máy phục vụ: 10.11.14-MariaDB-log
-- Phiên bản PHP: 8.4.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `android1_baitaplonjava`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `donhang`
--

CREATE TABLE `donhang` (
  `id` int(11) NOT NULL,
  `madonhang` varchar(250) NOT NULL,
  `noidung` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`noidung`)),
  `giatien` int(11) NOT NULL,
  `trangthai` int(11) NOT NULL,
  `nhanvien_id` int(11) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `ghichu` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `donhang`
--

INSERT INTO `donhang` (`id`, `madonhang`, `noidung`, `giatien`, `trangthai`, `nhanvien_id`, `date`, `ghichu`) VALUES
(9, 'DHb85b5d5b', '{\"sp2\":1,\"sp1\":1}', 70000, 0, 1, NULL, ''),
(10, 'DH8d603d82', '{\"sp2\":1,\"sp1\":1}', 70000, 1, 2, '2025-09-14 06:24:05', ''),
(11, 'DH4a669809', '{\"sp2\":1,\"sp1\":1}', 70000, 1, 1, '2025-09-14 06:40:52', ''),
(12, 'DHc2c7f169', '{\"sp2\":1}', 50000, 0, 2, '2025-09-14 06:48:28', ''),
(13, 'DHb2813621', '{\"sp2\":1,\"sp1\":1}', 70000, 0, 2, '2025-09-14 06:48:48', ''),
(14, 'DH6fbc3330', '{\"sp2\":3}', 50000, 0, 1, '2025-09-14 06:50:29', ''),
(15, 'DH33c6c92b', '{\"sp2\":1,\"sp3\":1}', 100000, 0, 2, '2025-09-14 08:29:46', ''),
(16, 'DH2571b471', '{\"sp2\":1}', 50000, 0, 2, '2025-09-14 08:38:57', ''),
(17, 'DH46d82179', '{\"sp1\":1}', 20000, 0, 2, '2025-09-14 08:39:03', ''),
(18, 'DHcf8e99ce', '{\"sp3\":1}', 50000, 0, 2, '2025-09-14 08:39:09', ''),
(19, 'DH7ffc9365', '{\"sp2\":3}', 50000, 0, 2, '2025-09-14 08:39:18', ''),
(20, 'DH62ed181f', '{\"sp3\":1}', 50000, 0, 2, '2025-09-14 08:39:31', ''),
(21, 'DH7df9a54b', '{\"sp2\":1}', 50000, 0, 2, '2025-09-14 08:39:47', ''),
(22, 'DH46650a2c', '{\"sp1\":6}', 20000, 0, 2, '2025-09-14 08:40:00', ''),
(23, 'DHad7a619f', '{\"sp2\":2}', 50000, 0, 2, '2025-09-14 08:40:08', ''),
(24, 'DH5eac9632', '{\"sp2\":3}', 50000, 0, 2, '2025-09-14 08:40:17', ''),
(25, 'DHa9f7d9a8', '{\"sp2\":3}', 50000, 0, 2, '2025-09-14 08:40:25', ''),
(26, 'DH2a18e654', '{\"sp2\":2}', 50000, 0, 2, '2025-09-14 08:40:32', ''),
(27, 'DH3a3cfa93', '{\"sp2\":2}', 50000, 0, 2, '2025-09-14 08:49:36', ''),
(28, 'DH6612ec46', '{\"sp3\":2}', 50000, 0, 2, '2025-09-14 08:49:43', ''),
(29, 'DH6e570b04', '{\"sp2\":4,\"sp1\":2}', 70000, 0, 2, '2025-09-14 09:00:15', ''),
(30, 'DHb325fd13', '{\"sp3\":2}', 50000, 0, 2, '2025-09-14 09:00:22', ''),
(31, 'DHd4f20c05', '{\"sp2\":2}', 50000, 0, 2, '2025-09-14 09:00:25', '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nhanvien`
--

CREATE TABLE `nhanvien` (
  `id` int(11) NOT NULL,
  `name` varchar(250) NOT NULL,
  `thongtin` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `nhanvien`
--

INSERT INTO `nhanvien` (`id`, `name`, `thongtin`) VALUES
(1, 'Nguyễn Văn A', ''),
(2, 'Tạ Văn B', '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nhaphang`
--

CREATE TABLE `nhaphang` (
  `id` int(11) NOT NULL,
  `name` longtext NOT NULL,
  `price` int(11) NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `nhaphang`
--

INSERT INTO `nhaphang` (`id`, `name`, `price`, `count`) VALUES
(3, 'cacao', 50000, 60),
(4, 'cafe', 60000, 50000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `test_table`
--

CREATE TABLE `test_table` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `xuathang`
--

CREATE TABLE `xuathang` (
  `id` int(11) NOT NULL,
  `name` longtext NOT NULL,
  `price` int(11) NOT NULL,
  `nguyenlieu` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `xuathang`
--

INSERT INTO `xuathang` (`id`, `name`, `price`, `nguyenlieu`) VALUES
(1, 'sp1', 20000, '{\"cacao\":1,\"cafe\":1}'),
(2, 'sp2', 50000, '{}'),
(4, 'sp3', 50000, '{}');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `donhang`
--
ALTER TABLE `donhang`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `nhaphang`
--
ALTER TABLE `nhaphang`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `xuathang`
--
ALTER TABLE `xuathang`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `donhang`
--
ALTER TABLE `donhang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT cho bảng `nhanvien`
--
ALTER TABLE `nhanvien`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `nhaphang`
--
ALTER TABLE `nhaphang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `xuathang`
--
ALTER TABLE `xuathang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
