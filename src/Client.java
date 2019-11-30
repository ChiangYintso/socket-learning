import java.io.*;
import java.net.*;

/**
 * @author Jiang Yinzuo
 */
public class Client {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    private static Socket createSocket() throws IOException {
//        Socket socket1 = new Socket(Proxy.NO_PROXY);
//
//        Proxy.Type type;
//        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(Inet4Address.getByName("www.baidu.com"),8800));
//        Socket socket2 = new Socket(proxy);
//
//        // 新建一个套接字, 并直接连接到本地20000的服务器上
//        Socket socket3 = new Socket("localhost", PORT);
//        Socket socket4 = new Socket(Inet4Address.getLocalHost(), PORT);

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));
        return socket;
    }

    private static void initSocket(Socket socket) throws SocketException {
        // 是否复用未完全关闭的Socket地址
        socket.setSoTimeout(3000);

        socket.setReuseAddress(true);

        socket.setTcpNoDelay(false);
        socket.setKeepAlive(true);
        socket.setSoLinger(true, 20);
        socket.setOOBInline(true);

        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);
        socket.setPerformancePreferences(1, 1, 0);
    }

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);

        socket.setSoTimeout(300);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);
        System.out.println("已发起服务器连接, 并进入后续流程");
        System.out.println("客户端信息：" + socket.getInetAddress() + " p: " + socket.getLocalPort());
        System.out.println("服务器信息: " + socket.getInetAddress() + " p: " + socket.getPort());
        todo(socket);
        socket.close();
        System.out.println("客户端已退出");
    }

    private static void todo(Socket client) {
        // 构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        try {
            // 得到Socket输出流，并转换为打印流
            OutputStream outputStream = client.getOutputStream();
            PrintStream socketPrintStream = new PrintStream(outputStream);

            // 得到Socket输入流
            InputStream inputStream = client.getInputStream();
            BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            boolean flag = true;
            do {
                // 键盘读取一行
                String str = input.readLine();

                // 发送到服务器
                socketPrintStream.println(str);

                // 从服务器读取一行
                String echo = socketBufferedReader.readLine();
                if ("bye".equalsIgnoreCase(echo)) {
                    flag = false;
                } else {
                    System.out.println(echo);
                }
            } while (flag);

            socketPrintStream.close();
            socketBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
