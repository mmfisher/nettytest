package cn.yu.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端
 * @author yu
 */
public class NettyServer {

    public static void main(String[] args) throws Exception {
        
        //创建一个线程组,接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建一个线程组,处理网络读写操作
        EventLoopGroup workGroup = new NioEventLoopGroup();

        //创建服务器端启动助手,配置参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //设置两个线程
        serverBootstrap.group(bossGroup, workGroup)
                //使用NioServerSocketChannel作为服务器端通道的实现
                .channel(NioServerSocketChannel.class)
                //设置列队中等待连接的个数
                .option(ChannelOption.SO_BACKLOG, 128)
                //保持活动连接状态
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //通道处理类
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });
        System.out.println("..................服务器配置完毕..................");
        //绑定端口非阻塞bind 是异步的 ,sync()同步的等待结果
        ChannelFuture channelFuture = serverBootstrap.bind(9999).sync();
        System.out.println("..................服务器启动..................");

        //关闭通道,关闭线程组
        channelFuture.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
