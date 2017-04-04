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
	
	@Test //�ͻ���
	public void client(){
		
		 //1.��ȡͨ��
    	SocketChannel sChannel=null;
    	 FileChannel  inChannel=null;
		try {
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
    	    inChannel = FileChannel.open(Paths.get("1.png"),StandardOpenOption.READ);
    	 
    	 //2.�����ƶ���С�Ļ�����
    	 ByteBuffer buf = ByteBuffer.allocate(1024);
    	 
    	 //3.��ȡ�����ļ����͵��ͻ���
    	 while(inChannel.read(buf)!=-1){
    		 buf.flip();
    		 sChannel.write(buf);
    		 buf.clear();
    	  } 
    	 //���߷���˳������Ѿ�������ϣ���Ȼ����˳���ͻ�һֱ��������״̬��
    	 sChannel.shutdownOutput();
    	 
    	 //���շ���˵ķ���
    	 int len = 0;
    	 while((len = sChannel.read(buf))!=-1){
    		 buf.flip();
    		 System.out.println(new String(buf.array(),0,len));
    		 buf.clear();
    	 }
    	 
    		 
		 } catch (IOException e) {
			e.printStackTrace();
		}finally{
			 //4.�ر�ͨ��
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
	
	@Test //�����
	public void server() throws IOException{
		//1.��ȡͨ��
   	 ServerSocketChannel ssChannel = ServerSocketChannel.open();
   	 
   	FileChannel outChannel = FileChannel.open(Paths.get("3.png"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

      //2.������
   	ssChannel.bind(new InetSocketAddress(8888));
      //3.��ȡ�ͻ������ӵ�ͨ��
      SocketChannel sChannel = ssChannel.accept();
      
      //4.����ָ����С�Ļ�����
      ByteBuffer buf = ByteBuffer.allocate(1024);
      
      //5.���տͻ��˵�����,�����浽����
      while(sChannel.read(buf)!=-1){
   	   //�л��ɶ�ȡģʽ
   	   buf.flip();
   	   outChannel.write(buf);
   	   buf.clear();
      }
      //�������ݷ������ͻ���
      buf.put("����˽������ݳɹ�".getBytes());
      buf.flip();
      sChannel.write(buf);
      
      
      //6.�ر�ͨ��
      sChannel.close();
      ssChannel.close();
      outChannel.close();
	}
}
