-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 27, 2019 at 05:33 PM
-- Server version: 5.7.19
-- PHP Version: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `exams`
--

-- --------------------------------------------------------

--
-- Table structure for table `answer`
--

DROP TABLE IF EXISTS `answer`;
CREATE TABLE IF NOT EXISTS `answer` (
  `id` int(11) NOT NULL,
  `idquestion` int(11) DEFAULT NULL,
  `name` varchar(196) DEFAULT NULL,
  `correct` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idquestion` (`idquestion`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assignedquestion`
--

DROP TABLE IF EXISTS `assignedquestion`;
CREATE TABLE IF NOT EXISTS `assignedquestion` (
  `id` int(11) NOT NULL,
  `idexam` int(11) DEFAULT NULL,
  `idstudent` int(11) DEFAULT NULL,
  `idquestion` int(11) DEFAULT NULL,
  `correctletter` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idexam` (`idexam`),
  KEY `idstudent` (`idstudent`),
  KEY `idquestion` (`idquestion`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `credentials`
--

DROP TABLE IF EXISTS `credentials`;
CREATE TABLE IF NOT EXISTS `credentials` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idstudent` int(11) DEFAULT NULL,
  `login` varchar(32) DEFAULT NULL,
  `password` char(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idstudent` (`idstudent`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `credentials`
--

INSERT INTO `credentials` (`id`, `idstudent`, `login`, `password`) VALUES
(2, 3, 'rm', '58466ebdd352f801198118e294e38715f864985fd87977f348bfcd7db62e7c76');

-- --------------------------------------------------------

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
CREATE TABLE IF NOT EXISTS `exam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `questions` int(11) DEFAULT NULL,
  `start` datetime DEFAULT NULL,
  `end` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `exam`
--

INSERT INTO `exam` (`id`, `name`, `description`, `questions`, `start`, `end`) VALUES
(1, 'Random test', 'A test.', 30, '2019-05-31 00:00:00', '2019-09-30 00:00:00'),
(2, 'Random test 2', 'A test.', 30, '2019-05-31 00:00:00', '2019-09-30 00:00:00'),
(3, 'Test for tercia', 'A test.', 30, '2019-05-31 00:00:00', '2019-09-30 00:00:00'),
(4, 'Random test 3', 'A test.', 30, '2018-05-31 00:00:00', '2019-09-30 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
CREATE TABLE IF NOT EXISTS `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`id`, `name`) VALUES
(1, 'prima'),
(2, 'sekunda'),
(3, 'tercia'),
(4, 'kvarta');

-- --------------------------------------------------------

--
-- Table structure for table `grouptoexam`
--

DROP TABLE IF EXISTS `grouptoexam`;
CREATE TABLE IF NOT EXISTS `grouptoexam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idgroup` int(11) DEFAULT NULL,
  `idexam` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idgroup` (`idgroup`),
  KEY `idexam` (`idexam`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `grouptoexam`
--

INSERT INTO `grouptoexam` (`id`, `idgroup`, `idexam`) VALUES
(1, 4, 1),
(2, 4, 2),
(3, 3, 3),
(4, 4, 4);

-- --------------------------------------------------------

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
CREATE TABLE IF NOT EXISTS `question` (
  `id` int(11) NOT NULL,
  `name` varchar(196) DEFAULT NULL,
  `idexam` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idexam` (`idexam`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `result`
--

DROP TABLE IF EXISTS `result`;
CREATE TABLE IF NOT EXISTS `result` (
  `id` int(11) NOT NULL,
  `idstudent` int(11) DEFAULT NULL,
  `idexam` int(11) DEFAULT NULL,
  `correct` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idexam` (`idexam`),
  KEY `idstudent` (`idstudent`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(32) DEFAULT NULL,
  `lastname` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`id`, `firstname`, `lastname`) VALUES
(3, 'Richard', 'Miscik');

-- --------------------------------------------------------

--
-- Table structure for table `studenttogroup`
--

DROP TABLE IF EXISTS `studenttogroup`;
CREATE TABLE IF NOT EXISTS `studenttogroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idstudent` int(11) DEFAULT NULL,
  `idgroup` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idstudent` (`idstudent`),
  KEY `idgroup` (`idgroup`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `studenttogroup`
--

INSERT INTO `studenttogroup` (`id`, `idstudent`, `idgroup`) VALUES
(2, 3, 4);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
