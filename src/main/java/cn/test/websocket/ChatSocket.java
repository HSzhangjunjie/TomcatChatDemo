package cn.test.websocket;

import cn.test.util.MessageUtil;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: HandSomeMaker
 * @date: 2020/2/13 3:34
 */

@ServerEndpoint(value = "/websocket",configurator = GetHttpSessionConfigurator.class)
public class ChatSocket {

    private Session session;
    private HttpSession httpSession;
    /**
     * 保存当前系统中登陆用户的HttpSession信息及对应的EndPoint信息。
     */
    private static Map<HttpSession, ChatSocket> onlineUsers = new HashMap<>();
    private static int onlineCount = 0;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        //记录webSocket的会话信息Session
        this.session = session;
        //获取当前用户HttpSession信息
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;

        System.out.println("当前登录用户：" + httpSession.getAttribute("username") + "Endpoint" + hashCode());
        //记录当前登录用户信息，及对应的EndPoint实例
        if (httpSession.getAttribute("username") != null) {
            onlineUsers.put(httpSession, this);
        }
        //获取当前所有登录用户
        String names = getNames();
        //组装消息
        String messages = MessageUtil.getContext(MessageUtil.TYPE_USER, "", "", names);
        //广播发送
        broadcastAllUsers(messages);
        //记录当前用户登陆数
        increasedCount();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("OnMessage:name=" + httpSession.getAttribute("username") + ",message:" + message);
        //获取客户端信息内容并解析
        Map<String, String> messageMap = JSON.parseObject(message, Map.class);
        String fromName = messageMap.get("fromName");
        String toName = messageMap.get("toName");
        String content = messageMap.get("content");
        System.err.println(content);
        //判定是否有接收人
        if (toName == null || toName.isEmpty()) {
            return;
        }
        //组装消息内容
        String messageContent = MessageUtil.getContext(MessageUtil.TYPE_MESSAGE, fromName, toName, content);
        System.out.println("服务器发送消息，消息内容：" + messageContent);
        //如果接收人是广播，则发送广播消息
        if ("all".equals(toName)) {
            broadcastAllUsers(messageContent);
        }
        //如果不是则给指定用户发送消息
        else {
            singlePushMessage(messageContent, fromName, toName);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        decreasedCount();
        System.out.println("客户端关闭了一个链接，当前在线人数：" + getOnlineCount());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
        System.out.println("服务异常");
    }

    private void singlePushMessage(String messageContent, String fromName, String toName) throws IOException {
        boolean isOnline = false;
        //判定接收人是否在线
        for (HttpSession session : onlineUsers.keySet()) {
            if (toName.equals(session.getAttribute("username"))) {
                isOnline = true;
            }
        }
        //如果在线发送消息
        if (isOnline) {
            for (HttpSession session : onlineUsers.keySet()) {
                if (session.getAttribute("username").equals(fromName) || session.getAttribute("username").equals(toName)) {
                    onlineUsers.get(session).session.getBasicRemote().sendText(messageContent);
                }
            }
        }
    }

    private void broadcastAllUsers(String messages) throws IOException {

        for (HttpSession session : onlineUsers.keySet()) {
            onlineUsers.get(session).session.getBasicRemote().sendText(messages);
        }
    }

    private String getNames() {
        String names = "";
        if (onlineUsers.size() > 0) {
            for (HttpSession session : onlineUsers.keySet()) {
                String username = (String) session.getAttribute("username");
                names += username + ",";
            }
        } else {
            throw new NullPointerException("onlineUsers 为空");
        }
        return names.substring(0, names.length() - 1);
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    private void increasedCount() {
        onlineCount++;
    }

    private void decreasedCount() {
        onlineCount--;
    }
}
