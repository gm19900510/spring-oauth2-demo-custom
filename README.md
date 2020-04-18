# spring-oauth2-demo-custom

spring-oauth2-自定义配置-使用示例

## 项目说明
| 项目名称 | 项目说明 |端口 |
|--|--|--|
| authorization-server-custom-jdbc | 授权服务 |8080 |
| resource-server-custom-jdbc      | 资源服务 |8081 |
| client-credentials-custom        | 客户端模式的客户端|8082 |
| client-authorization-code-custom | 授权码模式的客户端|8083 |
| client-sso-custom-one | 授权码模式的客户端1|8084 |
| client-sso-custom-two | 授权码模式的客户端2|8085 |


## 认证核心流程
### 获取token的主要流程

> 详情请见：https://blog.csdn.net/bluuusea/article/details/80284458

1.用户发起获取token的请求。

2.过滤器会验证path是否是认证的请求/oauth/token，如果为false，则直接返回没有后续操作。

3.过滤器通过clientId查询生成一个Authentication对象。

4.然后会通过username和生成的Authentication对象生成一个UserDetails对象，并检查用户是否存在。

5.以上全部通过会进入地址/oauth/token，即TokenEndpoint的postAccessToken方法中。

6.postAccessToken方法中会验证Scope，然后验证是否是refreshToken请求等。

7.之后调用AbstractTokenGranter中的grant方法。

8.grant方法中调用AbstractUserDetailsAuthenticationProvider的authenticate方法，通过username和Authentication对象来检索用户是否存在。

9.然后通过DefaultTokenServices类从tokenStore中获取OAuth2AccessToken对象。

10.然后将OAuth2AccessToken对象包装进响应流返回。

### 刷新token（refresh token）的流程

刷新token（refresh token）的流程与获取token的流程只有⑨有所区别：

- 获取token调用的是AbstractTokenGranter中的getAccessToken方法，然后调用tokenStore中的getAccessToken方法获取token。

- 刷新token调用的是RefreshTokenGranter中的getAccessToken方法，然后使用tokenStore中的refreshAccessToken方法获取token。

### tokenStore特点

tokenStore通常情况为自定义实现，一般放置在缓存或者数据库中。此处可以利用自定义tokenStore来实现多种需求，如：

- 同已用户每次获取token，获取到的都是同一个token，只有token失效后才会获取新token。

- 同一用户每次获取token都生成一个完成周期的token并且保证每次生成的token都能够使用（多点登录）。

- 同一用户每次获取token都保证只有最后一个token能够使用，之前的token都设为无效（单点token）。

### 授权模式及应用范围 

- 授权码模式 Authorization Code:用在服务端应用之间，正宗方式，支持refresh token。

- 简化模式 Implicit:用在移动app或者web app(这些app是在用户的设备上的，如在手机上调起微信来进行认证授权)，不支持refresh token。

- 密码模式 Resource Owner Password Credentials(password):应用直接都是受信任的，为遗留系统设计，支持refresh token。

- 客户端模式 Client Credentials:用在应用API访问，为后台api服务消费者设计，不支持refresh token。


### Filter顺序

Spring Security的官方文档向我们提供了filter的顺序，无论实际应用中你用到了哪些，整体的顺序是保持不变的：

> 详情请见：https://blog.csdn.net/sinat_29899265/article/details/80747924

- ChannelProcessingFilter，重定向到其他协议的过滤器。也就是说如果你访问的channel错了，那首先就会在channel之间进行跳转，如http变为https。


- SecurityContextPersistenceFilter，请求来临时在SecurityContextHolder中建立一个SecurityContext，然后在请求结束的时候，清空SecurityContextHolder。并且任何对SecurityContext的改变都可以被copy到HttpSession。


- ConcurrentSessionFilter，因为它需要使用SecurityContextHolder的功能，而且更新对应session的最后更新时间，以及通过SessionRegistry获取当前的SessionInformation以检查当前的session是否已经过期，过期则会调用LogoutHandler。
认证处理机制，如UsernamePasswordAuthenticationFilter，CasAuthenticationFilter，BasicAuthenticationFilter等，以至于SecurityContextHolder可以被更新为包含一个有效的Authentication请求。


- SecurityContextHolderAwareRequestFilter，它将会把HttpServletRequest封装成一个继承自HttpServletRequestWrapper的SecurityContextHolderAwareRequestWrapper，同时使用SecurityContext实现了HttpServletRequest中与安全相关的方法。


- JaasApiIntegrationFilter，如果SecurityContextHolder中拥有的Authentication是一个JaasAuthenticationToken，那么该Filter将使用包含在JaasAuthenticationToken中的Subject继续执行FilterChain。


- RememberMeAuthenticationFilter，如果之前的认证处理机制没有更新SecurityContextHolder，并且用户请求包含了一个Remember-Me对应的cookie，那么一个对应的Authentication将会设给SecurityContextHolder。


- AnonymousAuthenticationFilter，如果之前的认证机制都没有更新SecurityContextHolder拥有的Authentication，那么一个AnonymousAuthenticationToken将会设给SecurityContextHolder。


- ExceptionTransactionFilter，用于处理在FilterChain范围内抛出的AccessDeniedException和AuthenticationException，并把它们转换为对应的Http错误码返回或者对应的页面。


- FilterSecurityInterceptor，保护Web URI，进行权限认证，并且在访问被拒绝时抛出异常。

## TODO

- 补充相关技术博客
- 添加JWT


 
