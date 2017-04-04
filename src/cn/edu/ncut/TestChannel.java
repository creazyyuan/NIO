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
	// ����ͨ������ļ��ĸ���(��ֱ�ӻ�����)
	@Test
	public void test1() { // 24546
		long start = System.currentTimeMillis();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel inChannel = null;
		FileChannel outchannel = null;
		try {
			fis = new FileInputStream("D:\\BaiduYunDownload\\�й��\\java8.zip");
			fos = new FileOutputStream("D:\\BaiduYunDownload\\�й��\\java8_1.zip");
			// ��ȡͨ��
			inChannel = fis.getChannel();
			outchannel = fos.getChannel();
			// ����ָ����С�Ļ�����
			ByteBuffer buf = ByteBuffer.allocate(1024);

			// ��ͨ���е�����д�뻺����
			while (inChannel.read(buf) != -1) {
				// �л���ȡģʽ
				buf.flip();
				// �������������ݶ�ȡ��ͨ����
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
		System.out.println("�ķ�ʱ��Ϊ" + (end - start));

	}

	@Test
	// ʹ��ֱ�ӻ���������ļ��ĸ��ƣ��ڴ�ӳ���ļ���
	public void test2() throws IOException {
		long start = System.currentTimeMillis();
		try {
			FileChannel inChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\�й��\\nio.zip"),
					StandardOpenOption.READ);

			FileChannel outChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\�й��\\nio_1.zip"),
					StandardOpenOption.WRITE, StandardOpenOption.READ,
					StandardOpenOption.CREATE);

			// �ڴ�ӳ���ļ�
			MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0,
					inChannel.size());
			MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE,
					0, inChannel.size());

			// ֱ�ӶԻ����������ݽ��ж�д
			byte[] dst = new byte[inMappedBuf.limit()];
			inMappedBuf.get(dst);
			outMappedBuf.put(dst);

			inChannel.close();
			outChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("�ķ�ʱ��Ϊ" + (end - start));
	}

	// ͨ��֮������ݴ���(ֱ�ӻ�����)
	@Test
	public void test3() {  //10877
		long start = System.currentTimeMillis();
		try {
			FileChannel inChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\�й��\\nio.zip"),
					StandardOpenOption.READ);
			FileChannel outChannel = FileChannel.open(
					Paths.get("D:\\BaiduYunDownload\\�й��\\nio_1.zip"),
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
		System.out.println("�ķ�ʱ��Ϊ"+(end-start));
	}

	//��ɢ�;ۼ�
	@Test
	public void test4() throws IOException{
		RandomAccessFile raf1 = new RandomAccessFile("D:\\BaiduYunDownload\\�й��\\nio\\linux�ʼ�.txt", "rw");
		
		//1.��ȡͨ��
		FileChannel channel1 = raf1.getChannel();
		//2.����ָ����С�Ļ�����
		ByteBuffer buf1 = ByteBuffer.allocate(100);
		ByteBuffer buf2 = ByteBuffer.allocate(4096);
		
		//3.��ɢ��ȡ
		ByteBuffer[] bufs ={buf1,buf2};
		channel1.read(bufs);
		
		for (ByteBuffer byteBuffer : bufs) {
			//�л�����дģʽ
			byteBuffer.flip();
		}
		
		System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
		System.out.println("-----------------------------------------------------------------");
		System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));
		
		
		//4.�ۼ�д��
		RandomAccessFile raf2 = new RandomAccessFile("D:\\BaiduYunDownload\\�й��\\nio\\linux�ʼ�_1.txt", "rw");
		FileChannel channel2 = raf2.getChannel();
		
		channel2.write(bufs);
		
	}
	
	//��ȡ���е��ַ���
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
		//��ȡ������
		CharsetEncoder newEncoder = cs1.newEncoder();
		//��ȡ������
		CharsetDecoder newDecoder = cs1.newDecoder();
		
		CharBuffer buf = CharBuffer.allocate(1024);
	
		buf.put("������ҵ��ѧ���䣡��");	
		buf.flip();
			
		ByteBuffer byteBuf = newEncoder.encode(buf);
		//��֤�Ƿ����ɹ�
		byte[] array = byteBuf.array();
		for (byte b : array) {
			System.out.println(b);
		}
		
		//����  ���ֽ�=���ַ�
		CharBuffer chaBuf = newDecoder.decode(byteBuf);
		System.out.println("------------>"+chaBuf.toString());
		
		//��GBK���룬��UTF-8����
		Charset cs2 = Charset.forName("UTF-8");
		//��ȡ������
	//	CharsetDecoder newDecoder2 = cs2.newDecoder();	
	//	CharBuffer chaBuf2 = newDecoder2.decode(byteBuf);
		byteBuf.rewind();//�ظ���
		CharBuffer chaBuf2 = cs2.decode(byteBuf);
		System.out.println("----------------------------------------");
		System.out.println(chaBuf2.toString());
		
		
		
			
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
