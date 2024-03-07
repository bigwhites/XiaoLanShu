# 常用代码块: <br>

## JAVA <br>

* 去除字符串的所有空格
    ``` java
    String originalStr = " Hello, World! ";
    String noSpacesStr = originalStr.replaceAll("\\s", "");
    System.out.println(noSpacesStr);  // 输出: "Hello,World!"
    ```
* rabbitmq相关
  ``` java
           if (data.contains("success")) {
             // RabbitMQ的ack机制中，第二个参数返回true，表示需要将这条消息投递给其他的消费者重新消费
              channel.basicAck(deliveryTag, false);
          } else {
              // 第三个参数true，表示这个消息会重新进入队列
              channel.basicNack(deliveryTag, false, true);
          }
  ```