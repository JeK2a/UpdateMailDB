-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:8889
-- Время создания: Окт 29 2018 г., 19:10
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
-- Структура таблицы `a_api_email_folders`
--

CREATE TABLE `a_api_email_folders` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `account_email` varchar(255) NOT NULL,
  `account_id` int(11) NOT NULL DEFAULT '0',
  `folder_name` varchar(255) NOT NULL,
  `last_add_uid` int(11) NOT NULL DEFAULT '0',
  `last_event_uid` int(11) NOT NULL DEFAULT '0',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `a_api_email_folders`
--
ALTER TABLE `a_api_email_folders`
  ADD PRIMARY KEY (`user_id`,`account_email`,`folder_name`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
