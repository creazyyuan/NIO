package cn.edu.ncut;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import org.junit.Test;
public class TestNonBlockingNIO2 {
	
	 @Test
	 public void send() throws IOException{
		 DatagramChannel dchannel = DatagramChannel.open();
		 
		 dchannel.configureBlocking(false);
		 
		 ByteBuffer buf = ByteBuffer.allocate(1024);
		 
		 Scanner scanner = new Scanner(System.in);
		 
		 while(scanner.hasNext()){
			String str =scanner.next();
			buf.put((new Date().toLocaleString()+":\n"+str).getBytes());
			buf.flip();
			dchannel.send(buf,new InetSocketAddress("127.0.0.1",8888));
			buf.clear();
		 }
		 
		 //关闭
		 dchannel.close();
		 scanner.close();
	 }
	 
	 
	 @Test
	 public void receive() throws IOException{ 
		 DatagramChannel dchannel = DatagramChannel.open();
		 dchannel.configureBlocking(false);
		 //绑定
		 dchannel.bind(new InetSocketAddress(8888));
		 //选择器
		 Selector selector = Selector.open();
		 //将通道注册到选择器上
		 dchannel.register(selector, SelectionKey.OP_READ);		 
		 while(selector.select()>0){
			 //获取所有选择器
			 Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			 while(it.hasNext()){
				 SelectionKey sk = it.next();
				 //判断sk的状态
				 if(sk.isReadable()){
					 ByteBuffer buf = ByteBuffer.allocate(1024);
					 dchannel.receive(buf);
					 buf.flip();
					 System.out.println(new String(buf.array(),0,buf.limit()));	
					 buf.clear();
				 }	
			 }
			 //关闭选择键,防止重复，下次就绪时再放入
			 it.remove();
		 }
		 
		 
	 }
	 
	
	
}
