2026.04.14: 连接了数据库, 并跑通了使用python添加用户和查询所有用户信息; 使用@EnableScheduling开启异步, @EnableAsync开启多线程
Problem: 1. mapper中想使用@Mapper注解需要先加入Mybatis启动依赖
         2. 使用Lombok依赖可以用@Data(免写get&set)和@RequiredArgsConstructor(免手写构造器)优化代码
         3. 数据库中id为自增时, 如果mapper中SQL语句没有insert id, 那么message中id应为Integer型, 否则会因为没传id而int接收不了null
         4. 如果数据库中的时间为自动填充, 那么后端mapper中SQL语句不需操作time, 否则会因为不传time报错
         5. controller中用于验证/查询的简单的辅助参数(URL参数)@RequstParam, 存信息(复杂的业务数据(JSON数据))用@RequstBody

2026.04.15: 加入通过id更新&删除用户功能; 加入用户注册&登录系统, 通过"BCrypt加密存储依赖"加密了数据库中的明文密码, 登录时通过LoginRequest的DTO, 对账号和密码
            进行校验; 注册时确保用户名和密码非空且用户名&邮箱&手机号唯一, 最后默认为"普通用户", 状态为true, 管理员身份则需要数据库操作, 或者通过管理员更新身份;
Problem: 1. mapper中校验唯一性用COUNT, 校验后还需使用该对象用SELECT *
         2. status为Boolean类型才能进行 == null 非空性检验
         3. StringUtils.hashText() 字符串(!null & length > 0 & 至少一个非空字符 -> 返回true)
         4. Result标准应为Integer code & String message & Object data

2026.04.16: 加入全局异常捕获与专门服务于业务层的BusinessException, 登录方面加入JWT, 登录后24h过期需重新登录, 后台管理的用户列表加入分页功能, 登录成功返回VO类
            并使用DesensitizationUtil工具对部分信息进行脱敏处理