package cn.test.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @Description:
 * @author: HandSomeMaker
 * @date: 2020/2/13 4:01
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession= (HttpSession) request.getHttpSession();

        config.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}
