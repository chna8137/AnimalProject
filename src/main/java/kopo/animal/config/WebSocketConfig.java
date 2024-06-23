
package kopo.animal.config;

import kopo.animal.chat.ChatHandler;
import kopo.animal.util.CmmUtil;
import kopo.animal.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Slf4j
@EnableWebSocket
@RequiredArgsConstructor
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("WebSocket Execute!!!");

        registry.addHandler(chatHandler, "/ws/*/*")
                .setAllowedOrigins("*")
                .addInterceptors(
                        new HttpSessionHandshakeInterceptor() {

                            @Override
                            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                           WebSocketHandler wsHandler, Map<String, Object> attributes)
                                    throws Exception {

                                String path = CmmUtil.nvl(request.getURI().getPath());
                                log.info("path : " + path);

                                String[] urlInfo = path.split("/");

                                String roomName = CmmUtil.nvl(urlInfo[2]); // URI Path를 통해 채팅방 이름 가져오기
                                String nickname = CmmUtil.nvl(urlInfo[3]); // URI Path를 통해 사용자 이름 가져오기
//

                                // 채팅룸 이름이 한글 및 특수문자로 입력될 수 있기에
                                // 채팅룸 이름은 데이터 처리에 문제없는 영문이나 숫자로 변환해야 함
                                // 채팅룸 이름은 해시 함수를 이용하여 영문명으로 변경함
                                String roomNameHash = EncryptUtil.encHashSHA256(roomName);

                                log.info("roomName : " + roomName + " / nickname : + " + nickname);

                                attributes.put("roomName", roomName);
                                attributes.put("nickname", nickname);
                                attributes.put("roomNameHash", roomNameHash);
//

                                return super.beforeHandshake(request, response, wsHandler, attributes);
                            }
                        }
                );
    }
}
