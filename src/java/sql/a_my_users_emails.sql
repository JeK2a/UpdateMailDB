-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:8889
-- Время создания: Окт 29 2018 г., 19:13
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
-- Структура таблицы `a_my_users_emails`
--

CREATE TABLE `a_my_users_emails` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_monitoring` int(1) NOT NULL DEFAULT '0' COMMENT 'мониторинг почтового ящика',
  `is_default` enum('1','0') NOT NULL COMMENT 'основная почта',
  `host` varchar(255) NOT NULL,
  `port` varchar(255) NOT NULL,
  `login` varchar(255) NOT NULL,
  `name_from` varchar(255) NOT NULL,
  `charset` varchar(255) NOT NULL,
  `secure` varchar(255) NOT NULL,
  `success` int(1) DEFAULT NULL COMMENT 'Удачный вход в почту',
  `update` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `a_my_users_emails`
--
ALTER TABLE `a_my_users_emails`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `a_my_users_emails`
--
ALTER TABLE `a_my_users_emails`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
