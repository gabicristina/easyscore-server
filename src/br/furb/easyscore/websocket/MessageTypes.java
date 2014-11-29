package br.furb.easyscore.websocket;

/**
 * Define o tipo de mensagens que podem ser trocadas.
 * 
 * @author Gabriela
 * 
 */
public enum MessageTypes {

	CREATE("create"), JOIN("join"), LIST("list"), SEND_SCORE("send_score"), ADD_SCORE("add_score"), GET_SCORE(
			"list_score"), SERVER_TIME("server_time"), LIST_ALL_SCORES(
			"list_all_score"), CLEAN_SERVER("clean_server");

	private String value;

	private MessageTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
