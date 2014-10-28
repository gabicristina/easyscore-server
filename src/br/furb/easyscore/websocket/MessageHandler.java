package br.furb.easyscore.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Classe criada para gerenciar todas as mensagens recebidas pelo servidor.
 * 
 * @author Gabriela
 * 
 */
@ServerEndpoint("/websocket/easy")
public class MessageHandler {

	/**
	 * Guarda todas as sessões criadas.
	 */
	private static final Map<String, Session> sessions = Collections
			.synchronizedMap(new HashMap<String, Session>());
	private static final StudioHandler handler = new StudioHandler();

	/**
	 * Ao criar a sessão, adicionar a lista de sessões abertas.
	 * 
	 * @param session
	 */
	@OnOpen
	public void open(Session session) {
		sessions.put(session.getId(), session);
	}

	/**
	 * Receber e tratar mensagem do cliente.
	 * 
	 * @param session
	 * @param msg
	 * @param last
	 */
	@OnMessage
	public void onMessage(Session session, String msg, boolean last) {

		Map<String, String> response = handler.handleRequest(session.getId(),
				msg);
		for (String sessionId : response.keySet()) {
			sendMessage(sessionId, response, last);
		}

	}

	@OnMessage
	public void echoBinaryMessage(Session session, ByteBuffer bb, boolean last) {

		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendBinary(bb, last);
			}
		} catch (IOException e) {
			try {
				session.close();
			} catch (IOException ie) {

			}
		}
	}

	/**
	 * Ao encerrar uma sessão, excluir da mapa de sessões abertas.
	 * 
	 * @param session
	 * @param closeReason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		sessions.remove(session);
	}

	/**
	 * Envia a mensagem a uma das sessões ativas.
	 * 
	 * @param sessionId
	 * @param response
	 * @param last
	 */
	public void sendMessage(String sessionId, Map<String, String> response,
			boolean last) {
		Session session = sessions.get(sessionId);
		try {
			if (session.isOpen()) {
				session.getBasicRemote()
						.sendText(response.get(sessionId), last);
			}
		} catch (IOException e) {
			try {
				session.close();
			} catch (IOException ie) {

			}
		}
	}

}
