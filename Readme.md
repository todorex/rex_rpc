# rex_rpc
a lightweight rpc framework demo based on netty, zookeeper, spring

一个基于Netty，Zookeeper，Spring的一个轻量级框架。

参考 [轻量级分布式 RPC 框架](https://my.oschina.net/huangyong/blog/361751?p=13#h1_2)

## 设计

![rpc](https://github.com/todorex/rex_rpc/raw/master/image/rpc.png)

##  模块组成

1. rpc-common：通用工具包
2. rpc-registry: 注册中心接口
3. rpc-registry-zookeeper: 注册中心Zookeeper实现
4. rpc-server: 生产者服务端
5. rpc-client: 消费者客户端

## 案例

1. 定义生产者接口（rpc-sample-api）

   ```java
   public interface HelloService {
   
       String hello(String name);
   
       String hello(Person person);
   }
   ```

2. 生产者实现（rpc-sample-server）

   ```java
   // 该注解标志这是一个生产者的接口实现
   @RpcService(HelloService.class)
   public class HelloServiceImpl implements HelloService{
       @Override
       public String hello(String name) {
           return "hello! " + name;
       }
   
       @Override
       public String hello(Person person) {
           return "Hello! " + person.getFirstName() + " " + person.getLastName();
       }
   }
   
   // 启动类
   public class RpcBootstrap {
       public static void main(String[] args) {
           log.debug("start server");
           new ClassPathXmlApplicationContext("spring.xml");
       }
   }
   ```

   

3. 消费者实现（rpc-sample-client）

   ```java
   public class HelloClient {
       public static void main(String[] args) {
           ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
           RpcProxy rpcProxy = context.getBean(RpcProxy.class);
           HelloService helloService = rpcProxy.create(HelloService.class);
           String result = helloService.hello("world");
           System.out.println(result);
       }
   }
   ```

   

