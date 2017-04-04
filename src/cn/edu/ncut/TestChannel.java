package cn.edu.ncut;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Test;

public class TestChannel {
	// 利用通道完成文件的复制(非直接缓冲区)
	@Test
	public void test1() { // 24546
		long start = System.currentTimeMillis();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel inChannel = null;
		FileChannel outchannel = null;
		try {
			fis = new FileInputStream("D:\\BaiduYunDownload\\尚硅谷\\java8.zip");
			fos = new FileOutputStream("D:\\BaiduYunDownload\\尚硅谷\\java8_1.zip");
			// 获取通道
			inChannel = fis.getChannel();
			outchannel = fos.getChannel();
			// 分配指定大小的缓冲区
			ByteBuffer buf = ByteBuffer.allocate(1024);

			// 将通道中的数据写入缓冲区
			while (inChannel.read(buf) != -1) {
				// 切换读取模式
				buf.flip();
				// 将缓冲区的数据读取到通道中
				outchannel.write(buf);
				buf.clear();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inChannel != null) {
				try {
					inChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outchannel != null) {
				try {
					outchannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("耗费时间为" + (end - start));

	}

	@Test
	// 使用直接缓冲区完成文件的复制（内存映射文件）
	public void test2() throws IOException {
		long start = System.currentTimeMillis();
		try {
			FileChannel inChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\尚硅谷\\nio.zip"),
					StandardOpenOption.READ);

			FileChannel outChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\尚硅谷\\nio_1.zip"),
					StandardOpenOption.WRITE, StandardOpenOption.READ,
					StandardOpenOption.CREATE);

			// 内存映射文件
			MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0,
					inChannel.size());
			MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE,
					0, inChannel.size());

			// 直接对缓冲区的数据进行读写
			byte[] dst = new byte[inMappedBuf.limit()];
			inMappedBuf.get(dst);
			outMappedBuf.put(dst);

			inChannel.close();
			outChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("耗费时间为" + (end - start));
	}

	// 通道之间的数据传输(直接缓冲区)
	@Test
	public void test3() {  //10877
		long start = System.currentTimeMillis();
		try {
			FileChannel inChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\尚硅谷\\nio.zip"),
					StandardOpenOption.READ);
			FileChannel outChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\尚硅谷\\nio_1.zip"),
					StandardOpenOption.WRITE, StandardOpenOption.READ,
					StandardOpenOption.CREATE);
			
			//inChannel.transferTo(0, inChannel.size(), outChannel);
			
			outChannel.transferFrom(inChannel, 0, inChannel.size());
			inChannel.close();
			outChannel.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("耗费时间为"+(end-start));
	}

	//分散和聚集
	@Test
	public void test4() throws IOException{
		RandomAccessFile raf1 = new RandomAccessFile("D:\\BaiduYunDownload\\尚硅谷\\nio\\linux笔记.txt", "rw");
		
		//1.获取通道
		FileChannel channel1 = raf1.getChannel();
		//2.分配指定大小的缓冲区
		ByteBuffer buf1 = ByteBuffer.allocate(100);
		ByteBuffer buf2 = ByteBuffer.allocate(4096);
		
		//3.分散读取
		ByteBuffer[] bufs ={buf1,buf2};
		channel1.read(bufs);
		
		for (ByteBuffer byteBuffer : bufs) {
			//切换到读写模式
			byteBuffer.flip();
		}
		
		System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
		System.out.println("-----------------------------------------------------------------");
		System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));
		
		
		//4.聚集写入
		RandomAccessFile raf2 = new RandomAccessFile("D:\\BaiduYunDownload\\尚硅谷\\nio\\linux笔记_1.txt", "rw");
		FileChannel channel2 = raf2.getChannel();
		
		channel2.write(bufs);
		
	}
	
	//获取所有的字符集
	@Test
	public void test5(){
		 SortedMap<String, Charset> map = Charset.availableCharsets();
		 Set<Entry<String, Charset>> entrySet = map.entrySet();
		 for (Entry<String, Charset> entry : entrySet) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
	
	@Test
	public void test6(){
		try {
		Charset cs1 = Charset.forName("GBK");
		//获取编码器
		CharsetEncoder newEncoder = cs1.newEncoder();
		//获取解码器
		CharsetDecoder newDecoder = cs1.newDecoder();
		
		CharBuffer buf = CharBuffer.allocate(1024);
	
		buf.put("北方工业大学威武！！");	
		buf.flip();
			
		ByteBuffer byteBuf = newEncoder.encode(buf);
		//验证是否编码成功
		byte[] array = byteBuf.array();
		for (byte b : array) {
			System.out.println(b);
		}
		
		//解码  将字节=》字符
		CharBuffer chaBuf = newDecoder.decode(byteBuf);
		System.out.println("------------>"+chaBuf.toString());
		
		//用GBK编码，用UTF-8解码
		Charset cs2 = Charset.forName("UTF-8");
		//获取解码器
	//	CharsetDecoder newDecoder2 = cs2.newDecoder();	
	//	CharBuffer chaBuf2 = newDecoder2.decode(byteBuf);
		byteBuf.rewind();//重复读
		CharBuffer chaBuf2 = cs2.decode(byteBuf);
		System.out.println("----------------------------------------");
		System.out.println(chaBuf2.toString());
		
		
		
			
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
