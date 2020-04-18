# client-sso-custom-one

oauth2-单点登录-客户端1

## 功能项

- 测试

> http://localhost:8084/test/index

- 解决"Possible CSRF detected - state parameter was required but no state could be found"异常

> 在application.properties中新增 server.servlet.session.cookie.name=OAUTH2SESSION

> 详情请见：https://www.cnblogs.com/xuzimian/p/11133545.html