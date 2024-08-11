-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jul 24, 2024 at 07:22 AM
-- Server version: 10.6.18-MariaDB-cll-lve
-- PHP Version: 8.1.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `study_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_map_dependency_fields`
--

CREATE TABLE `tbl_map_dependency_fields` (
  `id` tinyint(11) UNSIGNED NOT NULL,
  `parent_global_set_id` tinyint(11) UNSIGNED NOT NULL,
  `child_global_set_id` tinyint(11) UNSIGNED NOT NULL,
  `created_by` smallint(5) UNSIGNED NOT NULL,
  `status` enum('0','1') NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE current_timestamp()
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tbl_map_dependency_fields`
--

INSERT INTO `tbl_map_dependency_fields` (`id`, `parent_global_set_id`, `child_global_set_id`, `created_by`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 2, 23, '1', '2024-03-19 12:52:15', '0000-00-00 00:00:00'),
(2, 2, 3, 23, '1', '2024-03-19 13:01:51', '0000-00-00 00:00:00'),
(3, 3, 4, 23, '1', '2024-03-19 13:01:58', '0000-00-00 00:00:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_map_dependency_fields`
--
ALTER TABLE `tbl_map_dependency_fields`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_map_dependency_fields`
--
ALTER TABLE `tbl_map_dependency_fields`
  MODIFY `id` tinyint(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
