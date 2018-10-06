-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Июл 29 2018 г., 12:02
-- Версия сервера: 5.7.21
-- Версия PHP: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- --------------------------------------------------------

--
-- Структура таблицы `a_my_emails`
--

CREATE TABLE IF NOT EXISTS `a_my_emails` (
  `id` int(12) NOT NULL,
  `direction` enum('in','out') NOT NULL,
  `user_id` int(11) NOT NULL,
  `client_id` int(11) DEFAULT NULL,
  `uid` int(11) NOT NULL,
  `message_id` varchar(255) NOT NULL,
  `msgno` int(11) NOT NULL,
  `from` varchar(255) NOT NULL,
  `to` varchar(255) NOT NULL,
  `in_reply_to` varchar(255) NOT NULL,
  `references` varchar(255) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `size` int(11) NOT NULL,
  `subject` text COLLATE utf8mb4_general_ci NOT NULL,  -- varchar
  `imap_folder` varchar(255) NOT NULL,
  `recent` int(11) NOT NULL,
  `flagged` int(11) NOT NULL,
  `answered` int(11) NOT NULL,
  `deleted` int(11) NOT NULL,
  `seen` int(11) NOT NULL,
  `draft` int(11) NOT NULL,
  `udate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `a_my_emails`
--
ALTER TABLE `a_my_emails`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `message_id` (`message_id`) USING BTREE,
  ADD KEY `from` (`from`),
  ADD KEY `to` (`to`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `a_my_emails`
--
ALTER TABLE `a_my_emails`
  MODIFY `id` int(12) NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
