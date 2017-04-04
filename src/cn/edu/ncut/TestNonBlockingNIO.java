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
	@Test  //�ͻ���
	public void client() throws IOException{
		//1.��ȡͨ��
		SocketChannel sChannnel= SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
		
		//2.�л�������ģʽ
		sChannnel.configureBlocking(false);
		
		//3.�����ƶ���С�Ļ�����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		//4.�������ݸ��ͻ���
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNext()){
			String str = scanner.next();
			buf.put((new Date().toLocaleString()+"\n"+str).getBytes());
			buf.flip();
			sChannnel.write(buf);
			buf.clear();
		}
				
		//5.�ر�ͨ��
		sChannnel.close();
		scanner.close();
	}
		
	@Test  //�����
	public void server() throws IOException{
		//1.��ȡͨ��
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//2.�л�������ģʽ
		ssChannel.configureBlocking(false);
		//3.������
		ssChannel.bind(new InetSocketAddress(8888));
		//4.��ȡѡ����
		Selector selector = Selector.open();
		//5.��ͨ��ע�ᵽѡ�����ϣ�����ָ�������������¼���
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//6.��ѯʽ�Ļ�ȡѡ�������Ѿ�ע��ġ�ѡ������Ѿ����ļ����¼�����
		
		while(selector.select()>0){
			//7.��ȡ��ǰѡ����������ע��ġ�ѡ������Ѿ����ļ����¼�����
			  Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			  
			  while(it.hasNext()){
				  //8.��ȡ׼���������¼�
				  SelectionKey sk = it.next();
				  //9.�жϾ�����ʲô�¼�׼������
				 if(sk.isAcceptable()){
					 //10.�����վ������ͻ�ȡ�ͻ�������
					 SocketChannel sChannel = ssChannel.accept();
					 
					 //11.�л�������ģʽ
					 sChannel.configureBlocking(false);
					 //12.��ͨ��ע�ᵽѡ������
					 sChannel.register(selector, SelectionKey.OP_READ);
				 }else if(sk.isReadable()){
					 //13.��ȡ��ǰѡ�����ϡ���������״̬��ͨ��
					 SocketChannel sChannel = (SocketChannel) sk.channel();
					 //��ȡ����
					 ByteBuffer buf = ByteBuffer.allocate(1024);
					 int len =0;
					 while((len = sChannel.read(buf))>0){
						 buf.flip();
						 System.out.println(new String(buf.array(),0,len));
						 buf.clear();
					 }				 
				 }				
				 // //�Ƴ�ѡ���,��ֹ�ظ����´ξ���ʱ�ٷ���
				 it.remove();
			  }
		}		
		
	}	
	
}
