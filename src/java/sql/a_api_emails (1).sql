-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Окт 15 2018 г., 15:48
-- Версия сервера: 5.7.21
-- Версия PHP: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `tdf_asyst`
--

-- --------------------------------------------------------

--
-- Структура таблицы `a_api_emails`
--

CREATE TABLE IF NOT EXISTS `a_api_emails` (
  `email_account` varchar(255) NOT NULL,
  `folder` varchar(255) NOT NULL,
  `uid` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `client_id` int(11) DEFAULT NULL,
  `direction` enum('in','out') NOT NULL,
  `message_id` varchar(255) NOT NULL,
  `from` varchar(255) NOT NULL,
  `to` text NOT NULL,
  `cc` text,
  `bcc` text,
  `in_reply_to` varchar(255) NOT NULL,
  `subject` text NOT NULL,
  `references` varchar(255) NOT NULL,
  `message_date` datetime DEFAULT NULL,
  `size` int(11) NOT NULL,
  `flagged` int(11) NOT NULL,
  `forwarded` tinyint(1) NOT NULL,
  `answered` int(11) NOT NULL,
  `seen` int(11) NOT NULL,
  `draft` int(11) NOT NULL,
  `has_attachment` tinyint(1) NOT NULL,
  `label_1` tinyint(1) NOT NULL,
  `label_2` tinyint(1) NOT NULL,
  `label_3` tinyint(1) NOT NULL,
  `label_4` tinyint(1) NOT NULL,
  `label_5` tinyint(1) NOT NULL,
  `kept` tinyint(1) NOT NULL COMMENT 'удержан от удаления',
  `deleted` tinyint(1) NOT NULL,
  `removed` tinyint(1) NOT NULL COMMENT 'окончательно удален',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `a_api_emails`
--
ALTER TABLE `a_api_emails`
  ADD PRIMARY KEY (`email_account`,`folder`,`uid`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `client_id` (`client_id`),
  ADD KEY `direction` (`direction`),
  ADD KEY `message_id` (`message_id`),
  ADD KEY `from` (`from`),
  ADD KEY `flagged` (`flagged`),
  ADD KEY `seen` (`seen`),
  ADD KEY `label_1` (`label_1`),
  ADD KEY `label_2` (`label_2`),
  ADD KEY `label_3` (`label_3`),
  ADD KEY `label_4` (`label_4`),
  ADD KEY `label_5` (`label_5`),
  ADD FULLTEXT KEY `subject` (`subject`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
