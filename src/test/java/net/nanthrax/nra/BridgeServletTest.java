package net.nanthrax.nra;

import org.apache.karaf.minho.boot.Minho;
import org.apache.karaf.minho.boot.service.ConfigService;
import org.apache.karaf.minho.boot.service.LifeCycleService;
import org.apache.karaf.minho.web.jetty.JettyWebContainerService;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class BridgeServletTest {

    @Test
    public void test() throws Exception {
        Minho minho = Minho.builder().loader(
                () -> Stream.of(
                        new ConfigService(),
                        new LifeCycleService(),
                        new BridgeServlet(),
                        new JettyWebContainerService())
        ).build().start();
    }

}
