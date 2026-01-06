-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 06, 2026 at 02:32 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `boutique_vetements`
--

-- --------------------------------------------------------

--
-- Table structure for table `inventory_log`
--

CREATE TABLE `inventory_log` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `quantity_change` int(11) NOT NULL,
  `type` enum('purchase','sale','adjustment','return') NOT NULL,
  `notes` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ligne_vente`
--

CREATE TABLE `ligne_vente` (
  `id_ligne` int(11) NOT NULL,
  `id_vente` int(11) DEFAULT NULL,
  `id_produit` int(11) DEFAULT NULL,
  `quantite` int(11) DEFAULT NULL,
  `prix_unitaire` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ligne_vente`
--

INSERT INTO `ligne_vente` (`id_ligne`, `id_vente`, `id_produit`, `quantite`, `prix_unitaire`) VALUES
(235, 472, 4, 1, 450),
(236, 472, 3, 1, 280),
(237, 473, 9, 1, 300),
(238, 474, 6, 2, 600),
(239, 475, 3, 1, 280),
(240, 476, 6, 1, 600),
(241, 477, 7, 1, 1800),
(242, 478, 9, 1, 300),
(243, 478, 6, 1, 600),
(244, 479, 8, 1, 520),
(245, 480, 7, 1, 1800),
(246, 481, 5, 2, 260),
(247, 482, 1, 2, 320),
(248, 483, 8, 1, 520),
(249, 484, 7, 1, 1800),
(251, 486, 4, 1, 450),
(252, 486, 1, 1, 320),
(253, 486, 3, 1, 280),
(254, 487, 4, 1, 450),
(255, 488, 7, 1, 1800),
(256, 489, 12, 1, 2200),
(257, 490, 11, 1, 120),
(258, 491, 7, 1, 1800),
(259, 492, 11, 1, 120),
(260, 492, 5, 1, 260),
(261, 493, 5, 1, 260),
(262, 494, 6, 1, 600),
(263, 495, 1, 1, 320),
(264, 495, 3, 1, 280),
(265, 496, 11, 1, 120),
(266, 497, 1, 1, 320),
(267, 498, 2, 1, 850),
(268, 499, 8, 1, 520),
(270, 501, 3, 1, 280),
(271, 502, 2, 1, 850),
(272, 503, 10, 1, 480),
(273, 504, 11, 1, 120),
(274, 505, 6, 1, 600),
(275, 506, 3, 1, 280),
(276, 507, 10, 2, 480),
(279, 510, 2, 1, 850),
(280, 511, 2, 1, 850),
(281, 512, 8, 1, 520),
(282, 513, 11, 1, 120),
(283, 514, 3, 2, 280),
(284, 515, 5, 4, 260),
(285, 516, 6, 1, 600),
(286, 517, 10, 1, 480),
(287, 518, 9, 1, 300),
(288, 518, 11, 1, 120),
(289, 519, 4, 1, 450);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `size` varchar(10) DEFAULT NULL,
  `color` varchar(30) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `barcode` varchar(50) DEFAULT NULL,
  `supplier` varchar(100) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `category`, `price`, `stock`, `size`, `color`, `description`, `image_url`, `barcode`, `supplier`, `cost_price`, `created_at`, `updated_at`, `is_active`) VALUES
(1, 'Pantalon jean large', 'Pantalons', 320.00, 10, 'L', 'Bleu', 'Pantalon jean large et confortable', 'images/pantalon_jean_large_bleu.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-05 12:52:02', 1),
(2, 'Robe de soirée élégante', 'Robes', 850.00, 6, 'M', 'Noir', 'Robe de soirée élégante pour occasions spéciales', 'images/robe_soiree_elegante.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-05 14:23:18', 1),
(3, 'Sweatshirt à capuche', 'Sweatshirts', 280.00, 7, 'L', 'Blanc', 'Sweatshirt à capuche confortable', 'images/sweat_capuche_blanc.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-05 14:31:18', 1),
(4, 'Jupe en cuir élégante', 'Jupes', 450.00, 9, 'M', 'Noir', 'Jupe en cuir élégante et moderne', 'images/jupe_cuir_marron.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-06 08:09:30', 1),
(5, 'Haut en satin col nœud', 'Hauts', 260.00, 6, 'M', 'Bleu poussiéreux', 'Haut en satin à manches longues avec col nœud', 'images/haut_satin_bleu.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-05 19:14:03', 1),
(6, 'Jellaba marocaine', 'Traditionnel', 600.00, 8, 'L', 'Bleu', 'Jellaba marocaine traditionnelle', 'images/jellaba_bleu.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-06 06:13:40', 1),
(7, 'Caftan marocain', 'Traditionnel', 1800.00, 15, 'XL', 'Noir', 'Caftan marocain noir élégant', 'images/caftan_noir.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-03 17:46:38', 1),
(8, 'Robe moderne', 'Robes', 520.00, 10, 'M', 'Bleu', 'Robe moderne au design contemporain', 'images/robe_moderne_bleu.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-05 14:23:23', 1),
(9, 'Pantalon classique', 'Pantalons', 300.00, 12, 'L', 'Beige', 'Pantalon beige élégant', 'images/pantalon_beige.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-06 07:58:57', 1),
(10, 'Veste chic', 'Vestes', 480.00, 7, 'M', 'Rouge', 'Veste rouge chic et tendance', 'images/veste_rouge.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-06 07:58:38', 1),
(11, 'T-shirt basique', 'T-shirts', 120.00, 23, 'M', 'Blanc', 'T-shirt blanc basique en coton', 'images/tshirt_blanc.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-06 07:58:57', 1),
(12, 'Manteau_Beige', 'Manteaux', 2200.00, 10, 'L', 'Beige', 'Manteau trench oversize en coton, design moderne et élégant', 'images/Manteau_Beige.jpg', NULL, NULL, NULL, '2025-12-27 22:34:25', '2026-01-04 10:34:17', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `role` enum('admin','manager','cashier') DEFAULT 'cashier',
  `phone` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`, `full_name`, `role`, `phone`, `created_at`, `last_login`, `is_active`) VALUES
(1, 'admin', 'admin123', 'admin@boutiquechic.ma', 'Mohammed Alaoui', 'admin', '0612345678', '2025-12-27 19:18:23', '2026-01-02 20:45:31', 1),
(2, 'manager', 'manager123', 'manager@boutiquechic.ma', 'Fatima Benali', 'manager', '0623456789', '2025-12-27 19:18:23', '2026-01-05 13:20:12', 1),
(3, 'cashier', 'cashier123', 'cashier@boutiquechic.ma', 'Youssef Idrissi', 'cashier', '0634567890', '2025-12-27 19:18:23', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `vente`
--

CREATE TABLE `vente` (
  `id_vente` int(11) NOT NULL,
  `numero_facture` varchar(20) DEFAULT NULL,
  `date_vente` datetime DEFAULT NULL,
  `total` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vente`
--

INSERT INTO `vente` (`id_vente`, `numero_facture`, `date_vente`, `total`) VALUES
(472, 'FACT-000001', '2022-03-03 12:36:54', 730),
(473, 'FACT-000002', '2022-02-05 12:37:15', 300),
(474, 'FACT-000003', '2022-01-03 12:57:07', 1200),
(475, 'FACT-000004', '2022-01-03 12:57:16', 280),
(476, 'FACT-000005', '2022-01-03 12:58:38', 600),
(477, 'FACT-000006', '2022-12-28 13:48:05', 1800),
(478, 'FACT-000007', '2022-01-03 14:24:03', 900),
(479, 'FACT-000008', '2023-12-29 14:24:22', 520),
(480, 'FACT-000009', '2023-12-30 14:24:28', 1800),
(481, 'FACT-000010', '2023-12-31 10:24:45', 520),
(482, 'FACT-000011', '2023-12-29 09:04:57', 640),
(483, 'FACT-000012', '2023-12-29 09:04:57', 520),
(484, 'FACT-000013', '2023-12-30 14:25:38', 1800),
(486, 'FACT-000014', '2024-03-31 15:26:56', 1050),
(487, 'FACT-000015', '2024-03-31 17:45:03', 450),
(488, 'FACT-000016', '2024-12-31 17:45:16', 1800),
(489, 'FACT-000017', '2024-01-01 17:50:01', 2200),
(490, 'FACT-000018', '2025-09-02 17:50:08', 120),
(491, 'FACT-000019', '2025-07-03 18:46:38', 1800),
(492, 'FACT-000020', '2025-08-03 18:48:04', 380),
(493, 'FACT-000021', '2025-12-30 10:39:53', 260),
(494, 'FACT-000022', '2025-12-31 10:40:11', 600),
(495, 'FACT-000023', '2026-01-01 11:11:53', 600),
(496, 'FACT-000024', '2026-01-02 11:19:41', 120),
(497, 'FACT-000025', '2026-01-03 11:21:51', 320),
(498, 'FACT-000026', '2026-01-03 11:22:09', 850),
(499, 'FACT-000027', '2026-01-03 11:23:16', 520),
(501, 'FACT-000028', '2026-01-04 16:07:32', 280),
(502, 'FACT-000029', '2026-01-04 16:07:47', 850),
(503, 'FACT-000030', '2026-01-04 16:28:17', 480),
(504, 'FACT-000031', '2026-01-04 16:33:25', 120),
(505, 'FACT-000032', '2026-01-04 16:33:44', 600),
(506, 'FACT-000033', '2026-01-04 16:36:08', 280),
(507, 'FACT-000034', '2026-01-04 16:36:30', 960),
(510, 'FACT-000035', '2026-01-05 15:22:59', 850),
(511, 'FACT-000036', '2026-01-05 15:23:18', 850),
(512, 'FACT-000037', '2026-01-05 15:23:23', 520),
(513, 'FACT-000038', '2026-01-05 15:31:06', 120),
(514, 'FACT-000039', '2026-01-05 15:31:18', 560),
(515, 'FACT-000040', '2026-01-05 20:14:03', 1040),
(516, 'FACT-000041', '2026-01-06 07:13:40', 600),
(517, 'FACT-000042', '2026-01-06 08:58:38', 480),
(518, 'FACT-000043', '2026-01-06 08:58:56', 420),
(519, 'FACT-000044', '2026-01-06 09:09:30', 450);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `inventory_log`
--
ALTER TABLE `inventory_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `idx_product` (`product_id`),
  ADD KEY `idx_date` (`created_at`);

--
-- Indexes for table `ligne_vente`
--
ALTER TABLE `ligne_vente`
  ADD PRIMARY KEY (`id_ligne`),
  ADD KEY `id_vente` (`id_vente`),
  ADD KEY `id` (`id_produit`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `barcode` (`barcode`),
  ADD KEY `idx_name` (`name`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_barcode` (`barcode`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_email` (`email`);

--
-- Indexes for table `vente`
--
ALTER TABLE `vente`
  ADD PRIMARY KEY (`id_vente`),
  ADD UNIQUE KEY `numero_facture` (`numero_facture`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `inventory_log`
--
ALTER TABLE `inventory_log`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ligne_vente`
--
ALTER TABLE `ligne_vente`
  MODIFY `id_ligne` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=290;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `vente`
--
ALTER TABLE `vente`
  MODIFY `id_vente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=520;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `inventory_log`
--
ALTER TABLE `inventory_log`
  ADD CONSTRAINT `inventory_log_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `inventory_log_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `ligne_vente`
--
ALTER TABLE `ligne_vente`
  ADD CONSTRAINT `ligne_vente_ibfk_1` FOREIGN KEY (`id_vente`) REFERENCES `vente` (`id_vente`),
  ADD CONSTRAINT `ligne_vente_ibfk_2` FOREIGN KEY (`id_produit`) REFERENCES `products` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
