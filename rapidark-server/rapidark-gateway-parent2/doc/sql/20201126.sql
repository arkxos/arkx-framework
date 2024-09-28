-- 此为增量升级脚本，适用于snapshot.v1.0版本
USE `gateway`;

/*Table structure for table `apidoc` */

CREATE TABLE `apidoc` (
  `id` varchar(40) NOT NULL COMMENT '主键，同route_id',
  `content` text COMMENT '内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
