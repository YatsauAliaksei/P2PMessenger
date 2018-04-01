package by.mrj.messenger.transport;

import by.mrj.messaging.network.transport.NetServerSocket;
import by.mrj.messaging.network.transport.NetSocket;
import by.mrj.messaging.network.transport.Transport;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import org.silvertunnel_ng.netlib.api.NetFactory;
import org.silvertunnel_ng.netlib.api.NetLayer;
import org.silvertunnel_ng.netlib.api.NetLayerIDs;
import org.silvertunnel_ng.netlib.api.util.TcpipNetAddress;
import org.silvertunnel_ng.netlib.layer.tor.TorHiddenServicePortPrivateNetAddress;
import org.silvertunnel_ng.netlib.layer.tor.TorHiddenServicePrivateNetAddress;
import org.silvertunnel_ng.netlib.layer.tor.TorNetLayerFactory;
import org.silvertunnel_ng.netlib.layer.tor.TorNetLayerUtil;
import org.silvertunnel_ng.netlib.layer.tor.TorNetServerSocket;
import org.silvertunnel_ng.netlib.layer.tor.common.TorConfig;

@Log4j2
public class TorTransport implements Transport {

    private TorNetServerSocket netServerSocket;
    private String netAddress;
    private int port;

    public void init() { // basic implementation.
        CompletableFuture.runAsync(() -> {
            System.setProperty(TorConfig.SYSTEMPROPERTY_TOR_MINIMUM_ROUTE_LENGTH, "2");
            System.setProperty(TorConfig.SYSTEMPROPERTY_TOR_MAXIMUM_ROUTE_LENGTH, "2");
            System.setProperty(TorConfig.SYSTEMPROPERTY_TOR_MINIMUM_IDLE_CIRCUITS, "2");
            TorConfig.reloadConfigFromProperties();

            NetLayer torNetLayer = new TorNetLayerFactory().getNetLayerById(NetLayerIDs.TOR);
            NetFactory.getInstance().registerNetLayer(NetLayerIDs.TOR, torNetLayer);
            torNetLayer.waitUntilReady();

            TorNetLayerUtil torNetLayerUtil = TorNetLayerUtil.getInstance();
            TorHiddenServicePrivateNetAddress netAddressWithoutPort = torNetLayerUtil.createNewTorHiddenServicePrivateNetAddress();

            TorHiddenServicePortPrivateNetAddress netAddress = new TorHiddenServicePortPrivateNetAddress(netAddressWithoutPort, 80);
            log.info("Onion address: [{}]", netAddress);
            this.netAddress = netAddress.getPublicOnionHostname();
            this.port = netAddress.getPort();

            // establish the hidden service
            netServerSocket = getNetServerSocket(torNetLayer, netAddress);
        });
    }

    @SneakyThrows
    private TorNetServerSocket getNetServerSocket(NetLayer torNetLayer, TorHiddenServicePortPrivateNetAddress netAddress) {
        return (TorNetServerSocket) torNetLayer.createNetServerSocket(null, netAddress);
    }

    @Override
    @SneakyThrows
    public InputStream sendWithResponse(byte[] bytes, String address) {
        String[] addr = address.split(":");
        TcpipNetAddress netAddress = new TcpipNetAddress(addr[0], Integer.valueOf(addr[1]));
        // create connection
        val netSocket = NetFactory.getInstance().getNetLayerById(NetLayerIDs.TOR).createNetSocket(null, null, netAddress);
        OutputStream os = netSocket.getOutputStream();
        os.write(bytes);
        os.flush();

        return netSocket.getInputStream();
    }

    @Override
    public NetServerSocket listening() {
        return new NetServerSocket() {

            @Override
            @SneakyThrows
            public NetSocket accept() {
                val netSocket = netServerSocket.accept();
                return new NetSocket() {

                    @Override
                    @SneakyThrows
                    public InputStream inputStream() {
                        return netSocket.getInputStream();
                    }

                    @Override
                    @SneakyThrows
                    public OutputStream outputStream() {
                        return netSocket.getOutputStream();
                    }

                    @Override
                    @SneakyThrows
                    public void close() throws Exception {
                        netSocket.close();
                    }
                };
            }

            @Override
            public void close() throws Exception {
                netServerSocket.close();
            }
        };
    }

    @Override
    public String netAddress() {
        return netAddress + ":" + port;
    }
}
