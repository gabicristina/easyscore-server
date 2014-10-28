package br.furb.easyscore.websocket;

/**
 * Define o tipo de mensagens que podem ser trocadas.
 * 
 * @author ITEN
 * 
 */
public enum MessageTypes {

	CREATE("create"), JOIN("join"), LIST("list"), NOTIFY("notify"), SEND_SCORE(
			"send_score"), GET_SCORE("list_score"), SERVER_TIME("server_time"), LIST_ALL_SCORES("list_all_score");

	private String value;

	private MessageTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
