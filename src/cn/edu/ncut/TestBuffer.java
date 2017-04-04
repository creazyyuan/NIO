package cn.edu.ncut;

import java.nio.ByteBuffer;

import org.junit.Test;

public class TestBuffer {
	//	0 <= mark <= position <= limit <= capacity
	@Test
	public void test1() {

		String str = "hello world!!";

		// 1.分配一个指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		System.out
				.println("------------------allocate()---------------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.position());
		System.out.println(buf.limit());

		// 2.利用put()存入数据到缓冲区
		buf.put(str.getBytes());

		System.out.println("------------------put()--------------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.position());
		System.out.println(buf.limit());

		// 3.切换成读取数据模式
		buf.flip();

		System.out.println("------------------flip()-------------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.position());
		System.out.println(buf.limit());

		// 4.利用get()读取缓冲区中的数据
		byte[] dst = new byte[buf.limit()];

		buf.get(dst);
		System.out.println(new String(dst, 0, dst.length));

		System.out.println("------------------get()-------------------------");
		System.out.println(buf.capacity());// 1024
		System.out.println(buf.position());// 13
		System.out.println(buf.limit());// 13

		// 5.rewind():可重复读，
		//将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素
		buf.rewind();

		System.out
				.println("------------------rewind()-------------------------");
		System.out.println(buf.capacity());// 1024
		System.out.println(buf.position());// 0
		System.out.println(buf.limit());// 13

		// 6.clear():清空缓冲区，但是缓冲区中的数据依然存在，但是出于被‘遗忘‘状态
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
		//分配一个指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		buf.put(str.getBytes());
		
		//切换成读模式
		buf.flip();
		
		byte[] dst =new byte[buf.limit()];
		buf.get(dst, 0, 2);
		System.out.println(new String(dst,0,2));
		System.out.println(buf.position());
		
		//mark():标记,记录当前position的位置
		buf.mark();
		
		buf.get(dst,2,dst.length-2);
		System.out.println(new String(dst,2,dst.length-2));
		System.out.println(buf.position());
		
		//reset():恢复到mark的位置
		buf.reset();
        System.out.println(buf.position());
        
        //判断缓冲区是否还有剩余数据
        if(buf.hasRemaining()){
        	//获取缓冲区中可以操作的数量
        	System.out.println(buf.remaining());
        }
	}
	
	@Test
	public void test3(){
		//分配直接缓冲区，直接在物理内存中
		ByteBuffer buf =ByteBuffer.allocateDirect(1024);
		System.out.println(buf.isDirect());
	}

}
