package cn.labzen.meta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Bootstrap {

  public static void main(String[] args) {
    SpringApplication.run(Bootstrap.class, args);
  }

  @PostConstruct
  public void test() {
    MetaConfiguration mc = Labzens.INSTANCE.configurationWith(MetaConfiguration.class);
    System.out.println(mc);
    System.out.println("meta test: " + mc.test());
    System.out.println("meta log level: " + mc.defaultLogLevel());
    System.out.println("meta sub function: " + mc.subFun());
    System.out.println("int number: " + mc.number());
    System.out.println("double number: " + mc.doubleNumber());
    System.out.println("boolean value: " + mc.bool());
  }
}
