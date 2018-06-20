package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.lightbend.akka.sample.Greeter.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AkkaQuickstart {
  public static final String IP_ADDR = "localhost";//server address
  public static final int PORT = 12345;// server port
  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("helloakka");
    try {
      //#create-actors
      final ActorRef printerActor = 
        system.actorOf(Printer.props(), "printerActor");
      final ActorRef howdyGreeter = 
        system.actorOf(Greeter.props("Howdy", printerActor), "howdyGreeter");
      final ActorRef helloGreeter = 
        system.actorOf(Greeter.props("Hello", printerActor), "helloGreeter");
      final ActorRef goodDayGreeter = 
        system.actorOf(Greeter.props("Good day", printerActor), "goodDayGreeter");
      //#create-actors

      //#main-send-messages
      howdyGreeter.tell(new WhoToGreet("Akka"), ActorRef.noSender());
      howdyGreeter.tell(new Greet(), ActorRef.noSender());

      howdyGreeter.tell(new WhoToGreet("Lightbend"), ActorRef.noSender());
      howdyGreeter.tell(new Greet(), ActorRef.noSender());

      helloGreeter.tell(new WhoToGreet("Java"), ActorRef.noSender());
      helloGreeter.tell(new Greet(), ActorRef.noSender());

      goodDayGreeter.tell(new WhoToGreet("Play"), ActorRef.noSender());
      goodDayGreeter.tell(new Greet(), ActorRef.noSender());
      //#main-send-messages

      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ioe) {
    } finally {
      system.terminate();
    }

    System.out.println("Client is starting ...");
    System.out.println("当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n");
    while (true) {
      Socket socket = null;
      try {
        //创建一个流套接字并将其连接到指定主机上的指定端口号
        socket = new Socket(IP_ADDR, PORT);

        //读取服务器端数据
        DataInputStream input = new DataInputStream(socket.getInputStream());
        //向服务器端发送数据
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.print("Please input : \t");
        String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
        out.writeUTF(str);

        String ret = input.readUTF();
        System.out.println("Server answer : " + ret);
        // 如接收到 "OK" 则断开连接
        if ("OK".equals(ret)) {
          System.out.println("Client will be closed ");
          Thread.sleep(500);
          break;
        }

        out.close();
        input.close();
      } catch (Exception e) {
        System.out.println("Client has exception:" + e.getMessage());
      } finally {
        if (socket != null) {
          try {
            socket.close();
          } catch (IOException e) {
            socket = null;
            System.out.println("客户端 finally 异常:" + e.getMessage());
          }
        }
      }
    }
  }
}
