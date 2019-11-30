import java.io.*;
import java.net.*;
import java.net.Proxy.Type;

/**
 * @author Jiang Yinzuo
 */
public class Server {
        private static final int PORT = 20000;
        private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = createServerSocket();
        initServerSocket(serverSocket);

        System.out.println("服务器准备就绪");
        System.out.println("服务器信息: " + serverSocket.getInetAddress() + " P: " + serverSocket.getLocalPort());

        // 等待客户端连接
        while (true) {
            // 得到客户端
            Socket client = serverSocket.accept();
            // 客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            // 启动线程
            clientHandler.start();
        }


    }

    private static ServerSocket createServerSocket() throws IOException {
        ServerSocket serverSocket = new ServerSocket();

        serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);

        return serverSocket;
    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {
        serverSocket.setReuseAddress(true);
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);
//        serverSocket.setSoTimeout(2000);
        serverSocket.setPerformancePreferences(1, 1, 1);
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private boolean flag = true;
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接: " + socket.getInetAddress() + " p: " + socket.getPort());
            try {
                // 得到打印流，用于数据输出，服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());

                // 得到输入流, 用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                boolean flag = true;
                do {
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                    } else {
                        // 打印到屏幕, 并回送数据长度
                        System.out.println(str);
                        socketOutput.println("回送" + str.length());
                    }
                } while (flag);
                socketInput.close();
                socketOutput.close();
            } catch (Exception e) {
                System.out.println("连接异常");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已退出：" + socket.getInetAddress() + " P: " + socket.getPort());
        }
    }


}
