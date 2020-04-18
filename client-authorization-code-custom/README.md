# client-authorization-code-custom

oauth2-授权码模式-访问资源-自定义示例

## 功能项

- 测试

> http://localhost:8083/test/query

- 解决"Possible CSRF detected - state parameter was required but no state could be found"异常

> 在application.properties中新增 server.servlet.session.cookie.name=OAUTH2SESSION

> 详情请见：https://www.cnblogs.com/xuzimian/p/11133545.html