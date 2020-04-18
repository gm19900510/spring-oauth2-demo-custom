# resource-server-custom-jdbc

oauth2-资源服务-自定义示例-JDBC Token存储

## 功能项

在ResourceServerConfig配置中，只有/test/hello不需要授权，其他都需要授权。

1.直接访问 http://localhost:8081/test/hello ，无需授权，所以正常响应，输出hello字符串。

2.访问 http://localhost:8081/test/meet

可以看到正如我们预期一样，返回了401错误以及错误信息，下面我们来获取access_token。

3.Spring Security OAuth2默认提供的四个URL：

- /oauth/authorize ： 授权AuthorizationEndpoint
- /oauth/token ： 令牌TokenEndpoint
- /oauth/check_token ： 令牌校验CheckTokenEndpoint
- /oauth/confirm_access ： 授权页面WhitelabelApprovalEndpoint
- /oauth/error ： 错误页面WhitelabelErrorEndpoint
在获取token之前需要在数据库表oauth_client_details添加对应的数据 ，见上方的初始化数据，初始化了一条客户端配置信息。 

4.获得令牌，参见outh2-authorization-server-jdbc-custom项目文档
scope：客户端的接口操作权限（read：读，write：写）

5.带上授权服务器返回的access_token发访问 http://localhost:8081/test/meet?access_token=7afa7ff0-2e17-4388-b8c7-47355de57537

成功输出。

6.访问http://localhost:8081/test/welcome?access_token=7afa7ff0-2e17-4388-b8c7-47355de57537

成功打印出了用户的账号。

7.访问http://localhost:8081/test/project?access_token=7afa7ff0-2e17-4388-b8c7-47355de57537

成功输出。

从代码上到，project方法使用了PreAuthorize注解，要求用户具有`ROLE_SUPER`角色才能访问，如果使用一个不具有`ROLE_SUPER`角色的账号的`access_token`访问，将出现下列403提示access_denied：

8.访问 http://localhost:8081/logout?access_token=7afa7ff0-2e17-4388-b8c7-47355de57537 ，token将被从数据库中删除

再使用该token将提示invalid_token： 




 