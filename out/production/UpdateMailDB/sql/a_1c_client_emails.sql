-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Июл 29 2018 г., 11:58
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
-- Структура таблицы `a_1c_client_emails`
--

CREATE TABLE IF NOT EXISTS `a_1c_client_emails` (
  `client_id` int(12) NOT NULL COMMENT 'код контагента',
  `contact_id` int(12) NOT NULL COMMENT 'код  к.л. контр.',
  `type_id` int(12) NOT NULL COMMENT 'код вида к.инф.',
  `type` varchar(100) NOT NULL COMMENT 'вид эл. почты',
  `email` varchar(100) NOT NULL COMMENT 'адрес эл. почты',
  `email_checked` varchar(100) NOT NULL,
  `comment` varchar(255) NOT NULL COMMENT 'комментарий',
  `update` int(1) NOT NULL,
  `delete` tinyint(4) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `a_1c_client_emails`
--
ALTER TABLE `a_1c_client_emails`
  ADD PRIMARY KEY (`client_id`,`contact_id`,`type_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
