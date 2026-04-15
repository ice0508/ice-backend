2026.04.14: 连接了数据库，并跑通了使用python添加用户和查询所有用户信息
Problem: 1. mapper中想使用@Mapper注解需要先加入Mybatis启动依赖
         2. 使用Lombok依赖可以用@Data(免写get&set)和@RequiredArgsConstructor(免手写构造器)优化代码
         3. 数据库中id为自增时, 如果mapper中SQL语句没有insert id, 那么message中id应为Integer型, 否则会因为没传id而int接收不了null
         4. 如果数据库中的时间为自动填充, 那么后端mapper中SQL语句不需操作time, 否则会因为不传time报错
         5. controller中用于验证/查询的简单的辅助参数(URL参数)@RequstParam, 存信息(复杂的业务数据(JSON数据))用@RequstBody