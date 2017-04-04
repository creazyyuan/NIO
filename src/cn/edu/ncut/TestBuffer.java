package cn.edu.ncut;

import java.nio.ByteBuffer;

import org.junit.Test;

public class TestBuffer {
	//	0 <= mark <= position <= limit <= capacity
	@Test
	public void test1() {

		String str = "hello world!!";

		// 1.����һ��ָ����С�Ļ�����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		System.out
				.println("------------------allocate()---------------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.position());
		System.out.println(buf.limit());

		// 2.����put()�������ݵ�������
		buf.put(str.getBytes());

		System.out.println("------------------put()--------------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.position());
		System.out.println(buf.limit());

		// 3.�л��ɶ�ȡ����ģʽ
		buf.flip();

		System.out.println("------------------flip()-------------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.position());
		System.out.println(buf.limit());

		// 4.����get()��ȡ�������е�����
		byte[] dst = new byte[buf.limit()];

		buf.get(dst);
		System.out.println(new String(dst, 0, dst.length));

		System.out.println("------------------get()-------------------------");
		System.out.println(buf.capacity());// 1024
		System.out.println(buf.position());// 13
		System.out.println(buf.limit());// 13

		// 5.rewind():���ظ�����
		//��position���0������������ض�Buffer�е��������ݡ�limit���ֲ��䣬��Ȼ��ʾ�ܴ�Buffer�ж�ȡ���ٸ�Ԫ��
		buf.rewind();

		System.out
				.println("------------------rewind()-------------------------");
		System.out.println(buf.capacity());// 1024
		System.out.println(buf.position());// 0
		System.out.println(buf.limit());// 13

		// 6.clear():��ջ����������ǻ������е�������Ȼ���ڣ����ǳ��ڱ���������״̬
		buf.clear();

		System.out
				.println("------------------clear()-------------------------");
		System.out.println(buf.capacity());// 1024
		System.out.println(buf.position());// 0
		System.out.println(buf.limit());// 1024

		System.out.println((char) buf.get());
	}

	@Test
	public void test2() {
		String str = "hello world!!";
		//����һ��ָ����С�Ļ�����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		buf.put(str.getBytes());
		
		//�л��ɶ�ģʽ
		buf.flip();
		
		byte[] dst =new byte[buf.limit()];
		buf.get(dst, 0, 2);
		System.out.println(new String(dst,0,2));
		System.out.println(buf.position());
		
		//mark():���,��¼��ǰposition��λ��
		buf.mark();
		
		buf.get(dst,2,dst.length-2);
		System.out.println(new String(dst,2,dst.length-2));
		System.out.println(buf.position());
		
		//reset():�ָ���mark��λ��
		buf.reset();
        System.out.println(buf.position());
        
        //�жϻ������Ƿ���ʣ������
        if(buf.hasRemaining()){
        	//��ȡ�������п��Բ���������
        	System.out.println(buf.remaining());
        }
	}
	
	@Test
	public void test3(){
		//����ֱ�ӻ�������ֱ���������ڴ���
		ByteBuffer buf =ByteBuffer.allocateDirect(1024);
		System.out.println(buf.isDirect());
	}

}
