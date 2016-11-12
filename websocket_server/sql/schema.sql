CREATE DATABASE IF NOT EXISTS `csm117` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `csm117`;

CREATE TABLE IF NOT EXISTS `restaurants` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `open` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `restaurants`
 ADD PRIMARY KEY (`id`), ADD KEY `open` (`open`);

ALTER TABLE `restaurants`
MODIFY `id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=0;
