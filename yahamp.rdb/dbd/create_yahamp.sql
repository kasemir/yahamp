-- Create YAHAMP Tables
--
-- Kay Kasemir

-- Table structure for table `calls`
DROP TABLE IF EXISTS `calls`;
CREATE TABLE `calls` (
  `callsign` varchar(20) NOT NULL,
  `name` varchar(100) default NULL,
  `addr` varchar(100) default NULL,
  `city` varchar(100) default NULL,
  `state` varchar(10) default NULL,
  `zip` varchar(10) default NULL,
  `grid` varchar(6) default NULL,
  `county` varchar(50) default NULL,
  `country` varchar(50) default NULL,
  `fists_nr` int(11) default NULL,
  `fists_cc` int(11) default NULL,
  `fp_nr` int(11) default NULL,
  UNIQUE KEY `callsign` (`callsign`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- Table structure for table `categories`
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(20) NOT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- In older database, there may be qsos with category_id=0.
-- After changing those to a different cat. id, the id=0 can be
-- removed from categories and then the table can be updated:
-- ALTER TABLE categories MODIFY id int(11) NOT NULL AUTO_INCREMENT;

INSERT INTO `categories` VALUES (1,'Default');

-- Table structure for table `qsos`
DROP TABLE IF EXISTS `qsos`;
CREATE TABLE `qsos` (
  `category_id` int(11) NOT NULL,
  `utc` datetime NOT NULL,
  `callsign` varchar(20) default NULL,
  `freq` varchar(10) default NULL,
  `rst_sent` varchar(10) default NULL,
  `rst_rcvd` varchar(10) default NULL,
  `mode` varchar(5) default NULL,
  `info` varchar(500) default NULL,
  `qsl` char(1) default NULL,
  UNIQUE KEY `utc` (`utc`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

