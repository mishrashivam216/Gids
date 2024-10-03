-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 01, 2024 at 01:11 PM
-- Server version: 10.6.18-MariaDB-cll-lve
-- PHP Version: 8.1.29

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
-- Table structure for table `tbl_global_data_set`
--

CREATE TABLE `tbl_global_data_set` (
  `id` tinyint(4) UNSIGNED NOT NULL,
  `name` varchar(200) NOT NULL,
  `status` enum('1','0') NOT NULL DEFAULT '1',
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE current_timestamp()
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tbl_global_data_set`
--

INSERT INTO `tbl_global_data_set` (`id`, `name`, `status`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 'Climate Zone', '1', 23, '2024-03-19 12:46:32', '2024-03-19 12:47:08'),
(2, 'District', '1', 23, '2024-03-19 12:46:44', '2024-03-19 12:47:10'),
(3, 'Block', '1', 23, '2024-03-19 12:46:52', '2024-03-19 12:47:13'),
(4, 'Village - गाँव', '1', 23, '2024-03-19 12:46:58', '2024-06-01 09:15:25'),
(7, 'NCO-2015 (3 Digits)', '1', 23, '2024-03-19 12:46:58', '2024-06-01 09:15:25'),
(8, 'NIC-2008 (5 Digits)', '1', 23, '2024-03-19 12:46:58', '2024-06-01 09:15:25');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_global_data_set`
--
ALTER TABLE `tbl_global_data_set`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_global_data_set`
--
ALTER TABLE `tbl_global_data_set`
  MODIFY `id` tinyint(4) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
