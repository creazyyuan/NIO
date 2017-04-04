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
		 
		 //�ر�
		 dchannel.close();
		 scanner.close();
	 }
	 
	 
	 @Test
	 public void receive() throws IOException{ 
		 DatagramChannel dchannel = DatagramChannel.open();
		 dchannel.configureBlocking(false);
		 //��
		 dchannel.bind(new InetSocketAddress(8888));
		 //ѡ����
		 Selector selector = Selector.open();
		 //��ͨ��ע�ᵽѡ������
		 dchannel.register(selector, SelectionKey.OP_READ);		 
		 while(selector.select()>0){
			 //��ȡ����ѡ����
			 Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			 while(it.hasNext()){
				 SelectionKey sk = it.next();
				 //�ж�sk��״̬
				 if(sk.isReadable()){
					 ByteBuffer buf = ByteBuffer.allocate(1024);
					 dchannel.receive(buf);
					 buf.flip();
					 System.out.println(new String(buf.array(),0,buf.limit()));	
					 buf.clear();
				 }	
			 }
			 //�ر�ѡ���,��ֹ�ظ����´ξ���ʱ�ٷ���
			 it.remove();
		 }
		 
		 
	 }
	 
	
	
}