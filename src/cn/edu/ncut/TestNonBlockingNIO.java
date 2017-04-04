package cn.edu.ncut;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import org.junit.Test;
public class TestNonBlockingNIO {   	
	@Test  //客户端
	public void client() throws IOException{
		//1.获取通道
		SocketChannel sChannnel= SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
		
		//2.切换非阻塞模式
		sChannnel.configureBlocking(false);
		
		//3.分配制定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		//4.发送数据给客户端
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNext()){
			String str = scanner.next();
			buf.put((new Date().toLocaleString()+"\n"+str).getBytes());
			buf.flip();
			sChannnel.write(buf);
			buf.clear();
		}
				
		//5.关闭通道
		sChannnel.close();
		scanner.close();
	}
		
	@Test  //服务端
	public void server() throws IOException{
		//1.获取通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//2.切换非阻塞模式
		ssChannel.configureBlocking(false);
		//3.绑定连接
		ssChannel.bind(new InetSocketAddress(8888));
		//4.获取选择器
		Selector selector = Selector.open();
		//5.将通道注册到选择器上，并且指定“监听接收事件”
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//6.轮询式的获取选择器上已经注册的“选择键（已就绪的监听事件）”
		
		while(selector.select()>0){
			//7.获取当前选择器中所有注册的“选择键（已就绪的监听事件）”
			  Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			  
			  while(it.hasNext()){
				  //8.获取准备就绪的事件
				  SelectionKey sk = it.next();
				  //9.判断具体是什么事件准备就绪
				 if(sk.isAcceptable()){
					 //10.若接收就绪，就获取客户端连接
					 SocketChannel sChannel = ssChannel.accept();
					 
					 //11.切换非阻塞模式
					 sChannel.configureBlocking(false);
					 //12.将通道注册到选择器上
					 sChannel.register(selector, SelectionKey.OP_READ);
				 }else if(sk.isReadable()){
					 //13.获取当前选择器上“读就绪”状态的通道
					 SocketChannel sChannel = (SocketChannel) sk.channel();
					 //读取数据
					 ByteBuffer buf = ByteBuffer.allocate(1024);
					 int len =0;
					 while((len = sChannel.read(buf))>0){
						 buf.flip();
						 System.out.println(new String(buf.array(),0,len));
						 buf.clear();
					 }				 
				 }				
				 // //移除选择键,防止重复，下次就绪时再放入
				 it.remove();
			  }
		}		
		
	}	
	
}
