# 权限管理

### 数据库设计
权限分为菜单和资源
* http://www.macrozheng.com/#/technology/permission_back

#### 动态权限决策管理器 最后判断权限需要Authentication, 所以需要知道Authentication 的用处
权限对比，一般是字符串的对比
```java
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 当接口未被配置资源时直接放行
        if (CollUtil.isEmpty(configAttributes)) {
            return;
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            //将访问所需资源或用户拥有资源进行比对
            String needAuthority = configAttribute.getAttribute();
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("抱歉，您没有访问权限");
    }
```
DynamicSecurityFilter 就是 FilterSecurityInterceptor

#### 理论与原理学习
#####1.[Spring Security 实战干货：图解Spring Security中的Servlet过滤器体系](https://segmentfault.com/a/1190000023102733?utm_source=sf-related)
#####2.[过滤器链的形成过程](https://felord.cn/spring-security-filter-chain.html)
该文章的2.1 章节
#####3.[Spring Security 实战干货：内置 Filter 全解析](https://www.felord.cn/spring-security-filters.html)
Spring Security 以一个单 Filter（FilterChainProxy） 存在于整个过滤器链中，而这个 FilterChainProxy 实际内部代理着众多的 Spring Security Filter，这些就是内置过滤器
内置过滤器，有一些默认已经启用，有一些需要引入特定的包并且对 HttpSecurity 进行配置才会生效 。而且它们的顺序是既定的。 只有你了解这些过滤器你才能基于业务深度定制 Spring Security ，需要时具体看
#####4.[Spring Security 中的身份认证](https://blog.gaoyuexiang.cn/2020/06/07/spring-security-authentication/)
介绍Spring Security 自带的身份认证方式和添加自己的身份认证方式的方法

**总结**：

这篇文章比较详细的梳理了 AbstractAuthenticationProcessingFilter 及其子类 UsernamePasswordAuthenticationFilter 的实现和 BasicAuthenticationFilter 的实现，了解了需要实现自定义身份验证的 Filter 时应该选择哪种方式：

只是进行身份验证，完成后进行重定向，而不调用业务方法，那么就继承 AbstractAuthenticationProcessFilter

需要调用业务方法，身份验证是为了保护业务，那么就继承 OncePerRequestFilter，完全控制认证的流程

当然，这不是一个强制的限制，你仍然可以通过重写 AbstractAuthenticationProcessFilter.successfulAuthentication() 方法来修改重定向的行为。

另外，也了解到了实现完 Filter 后，需要实现 WebSecurityConfigurerAdapter，将 Filter 加入到 SecurityFilterChain 中。
#####5.权限认证
* [基于编程的静态方式-基于配置的接口角色访问控制](https://www.felord.cn/spring-security-javaconfig-rbac.html)
* [基于编程的静态方式-基于注解的接口角色访问控制](https://www.felord.cn/spring-security-annotation-rbac.html)
* [动态权限控制-上](https://www.felord.cn/spring-security-dynamic-rbac-a.html)
* [动态权限控制-下](https://www.felord.cn/spring-security-dynamic-rbac-b.html)

静态方式就是，用户的用户名、密码以及角色表配置在数据库里。但是角色访问的路径在程序里写死了，如下：
```java
// 基于配置的
.antMatchers("/root/**").hasRole("root")//"/root/**"只有root能访问  

//基于注解的
@PreAuthorize("hasAuthority('pms:product:create')")
```

动态权限控制：现在将角色的访问权限也写入数据库，就可以动态的改变用户的权限了。

一句话概括： 访问时，在数据库内检索你访问的url需要什么角色，判断是否与自己的登陆角色相同，相同则可以访问，不相同则禁止访问。

#####6.本demo的思路原理: [SpringSecurity+JWT认证流程解析](https://juejin.cn/post/6846687598442708999#heading-13)
#####7.本demo的动态鉴权采用方案1：[SpringSecurity动态鉴权流程解析 ](https://juejin.cn/post/6847902222668431368)
那我们要做到这一步可以想些方案，比如：
* 1.直接重写一个AccessDecisionManager，将它用作默认的AccessDecisionManager，并在里面直接写好鉴权逻辑。
* 2.再比如重写一个投票器，将它放到默认的AccessDecisionManager里面，和之前一样用投票器鉴权。
* 3.我看网上还有些博客直接去做FilterSecurityInterceptor的改动。

### 使用及介绍

仅需四步，整合SpringSecurity+JWT实现登录认证！

整合步骤

第一步，给需要登录认证的模块添加mall-security依赖：
```xml
<dependency>
    <groupId>com.macro.mall</groupId>
    <artifactId>mall-security</artifactId>
</dependency>
```
第二步，添加MallSecurityConfig配置类，继承mall-security中的SecurityConfig配置，并且配置一个UserDetailsService接口的实现类，用于获取登录用户详情，
及创建一个DynamicSecurityService对象，用于加载资源ANT通配符和资源对应MAP：
```java
/**
 * mall-security模块相关配置
 * Created by macro on 2019/11/9.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MallSecurityConfig extends SecurityConfig {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsResourceService resourceService;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> adminService.loadUserByUsername(username);
    }

    @Bean
    public DynamicSecurityService dynamicSecurityService() {
        return new DynamicSecurityService() {
            @Override
            public Map<String, ConfigAttribute> loadDataSource() {
                Map<String, ConfigAttribute> map = new ConcurrentHashMap<>();
                List<UmsResource> resourceList = resourceService.listAll();
                for (UmsResource resource : resourceList) {
                    map.put(resource.getUrl(), new org.springframework.security.access.SecurityConfig(resource.getId() + ":" + resource.getName()));
                }
                return map;
            }
        };
    }
}
```

第三步，在application.yml中配置下不需要安全保护的资源路径：
```xml
secure:
  ignored:
    urls: #安全路径白名单
      - /swagger-ui.html
      - /swagger-resources/**
      - /swagger/**
      - /**/v2/api-docs
      - /**/*.js
      - /**/*.css
      - /**/*.png
      - /**/*.ico
      - /webjars/springfox-swagger-ui/**
      - /druid/**
      - /actuator/**
      - /sso/**
      - /home/**
```

第四步，在UmsMemberController中实现登录和刷新token的接口：
```java
/**
 * 会员登录注册管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Api(tags = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("会员登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password) {
        String token = memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }
}
```

目录结构
```lua
mall-security
├── component
|    ├── JwtAuthenticationTokenFilter -- JWT登录授权过滤器
|    ├── RestAuthenticationEntryPoint -- 自定义返回结果：未登录或登录过期
|    ├── RestfulAccessDeniedHandler -- 自定义返回结果：没有权限访问时
| 	 ├──DynamicAccessDecisionManager -- 自定义动态权限决策管理器，用于判断用户是否有访问权限
| 	 ├──DynamicSecurityFilter -- 自定义动态权限过滤器，用于实现基于路径的动态权限过滤
| 	 ├──DynamicSecurityService -- DynamicSecurityMetadataSource中用到，需要实现接口
| 	 └──DynamicSecurityMetadataSource -- 动态权限数据源，获取动态权限规则（即访问的URL需要什么权限）

├── config
|    ├── IgnoreUrlsConfig -- 用于配置不需要安全保护的资源路径
|    ├── RedisConfig -- redis配置
|    └── SecurityConfig -- SpringSecurity通用配置

├── annotation
|    ├── CacheException -- 

├── aspect
|    ├── RedisCacheAspect -- 
└── util
     └── JwtTokenUtil -- JWT的token处理工具类
```