-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:8889
-- Время создания: Окт 29 2018 г., 19:12
-- Версия сервера: 5.7.23
-- Версия PHP: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `test`
--

-- --------------------------------------------------------

--
-- Структура таблицы `a_api_emails`
--

CREATE TABLE `a_api_emails` (
  `email_account` varchar(255) NOT NULL,
  `folder` varchar(255) NOT NULL,
  `uid` int(11) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `client_id` int(11) NOT NULL DEFAULT '0',
  `direction` enum('in','out') NOT NULL,
  `message_id` varchar(255) NOT NULL,
  `from` text,
  `to` text,
  `cc` text,
  `bcc` text,
  `in_reply_to` text,
  `subject` text NOT NULL,
  `references` varchar(255) NOT NULL,
  `message_date` timestamp NULL DEFAULT NULL,
  `size` int(11) NOT NULL,
  `flagged` int(1) NOT NULL DEFAULT '0',
  `forwarded` tinyint(1) NOT NULL DEFAULT '0',
  `answered` int(11) NOT NULL,
  `seen` int(1) NOT NULL DEFAULT '0',
  `draft` tinyint(1) NOT NULL DEFAULT '0',
  `has_attachment` tinyint(1) NOT NULL DEFAULT '0',
  `label_1` tinyint(1) NOT NULL DEFAULT '0',
  `label_2` tinyint(1) NOT NULL DEFAULT '0',
  `label_3` tinyint(1) NOT NULL DEFAULT '0',
  `label_4` tinyint(1) NOT NULL DEFAULT '0',
  `label_5` tinyint(1) NOT NULL DEFAULT '0',
  `kept` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'удержан от удаления',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'окончательно удален',
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
  ADD KEY `flagged` (`flagged`),
  ADD KEY `seen` (`seen`),
  ADD KEY `label_1` (`label_1`),
  ADD KEY `label_2` (`label_2`),
  ADD KEY `label_3` (`label_3`),
  ADD KEY `label_4` (`label_4`),
  ADD KEY `label_5` (`label_5`);
ALTER TABLE `a_api_emails` ADD FULLTEXT KEY `subject` (`subject`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
