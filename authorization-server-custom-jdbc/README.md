# authorization-server-custom-jdbc

oauth2-授权服务-自定义示例-JDBC Token存储

## 功能项

- 自定义登录页面
- 自定义授权页面
- Oauth2后台管理

> 应用接入管理

> 用户管理

> 已授权应用管理

- 自定义异常处理
- 自定义图片验证码
- 自定义登记日志存储
- 自定义登录成功、失败处理
- 支持多授权模式
- Token JDBC存储
- Token 可执行刷新token
- 资源访问权限控制
- RememberMe使用
- 自定义错误页面

## TODO

- 单点登录
- jwt
- 多种登录方式：二维码、手机验证码
- social使用
- 与网端联合使用实现鉴权

## 参考

> https://github.com/BUG9/spring-security

> https://gitee.com/lvhaibao/spring-lhbauth/tree/9a99e050b685df563825493bff5a4694e1f12eaf/

> https://github.com/metallbv/oauth-security/tree/45cf568674ad42c23eaab63bbb7e9ac455ccb4cd

> https://github.com/tenstone/spring-boot-oauth2/tree/f88d645fc8ad5e2c51ace6622e2bd19394f32b6e/oauth2-server

> https://github.com/fp2952/spring-cloud-base

## 获取授权码模式
- 获取授权码

> http://localhost:8080/oauth/authorize?client_id=client_one&response_type=code

admin/admin
user/user

- 获取access_token

> http://localhost:8080/oauth/token?grant_type=authorization_code&code=gsNDpJ&client_id=client_one&client_secret=secret

- 刷新token

> http://localhost:8080/oauth/token?grant_type=refresh_token&refresh_token=2fb73a99-def8-49c4-a51f-8d962491aa01&client_id=client_one&client_secret=secret

- 使用说明文档

> https://blog.csdn.net/qq_33542154/article/details/89851150

## 客户端模式
- 获取access_token

> http://localhost:8080/oauth/token?grant_type=client_credentials&client_id=client_one&client_secret=secret







 
