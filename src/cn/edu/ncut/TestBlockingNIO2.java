package cn.edu.ncut;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestBlockingNIO2 {
	
	@Test //客户端
	public void client(){
		
		 //1.获取通道
    	SocketChannel sChannel=null;
    	 FileChannel  inChannel=null;
		try {
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
    	    inChannel = FileChannel.open(Paths.get("1.png"),StandardOpenOption.READ);
    	 
    	 //2.分配制定大小的缓冲区
    	 ByteBuffer buf = ByteBuffer.allocate(1024);
    	 
    	 //3.读取本地文件发送到客户端
    	 while(inChannel.read(buf)!=-1){
    		 buf.flip();
    		 sChannel.write(buf);
    		 buf.clear();
    	  } 
    	 //告诉服务端程序我已经发送完毕，不然服务端程序就会一直处于阻塞状态。
    	 sChannel.shutdownOutput();
    	 
    	 //接收服务端的反馈
    	 int len = 0;
    	 while((len = sChannel.read(buf))!=-1){
    		 buf.flip();
    		 System.out.println(new String(buf.array(),0,len));
    		 buf.clear();
    	 }
    	 
    		 
		 } catch (IOException e) {
			e.printStackTrace();
		}finally{
			 //4.关闭通道
			if(sChannel!=null){
				try {
					sChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inChannel!=null){
				 try {
					inChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
     }
	
	@Test //服务端
	public void server() throws IOException{
		//1.获取通道
   	 ServerSocketChannel ssChannel = ServerSocketChannel.open();
   	 
   	FileChannel outChannel = FileChannel.open(Paths.get("3.png"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

      //2.绑定连接
   	ssChannel.bind(new InetSocketAddress(8888));
      //3.获取客户端连接的通道
      SocketChannel sChannel = ssChannel.accept();
      
      //4.分配指定大小的缓冲区
      ByteBuffer buf = ByteBuffer.allocate(1024);
      
      //5.接收客户端的数据,并保存到本地
      while(sChannel.read(buf)!=-1){
   	   //切换成读取模式
   	   buf.flip();
   	   outChannel.write(buf);
   	   buf.clear();
      }
      //发送数据反馈给客户端
      buf.put("服务端接收数据成功".getBytes());
      buf.flip();
      sChannel.write(buf);
      
      
      //6.关闭通道
      sChannel.close();
      ssChannel.close();
      outChannel.close();
	}
}
