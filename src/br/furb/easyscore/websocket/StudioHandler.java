package br.furb.easyscore.websocket;

import java.io.StringReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParsingException;

import br.furb.easyscore.websocket.domain.Score;
import br.furb.easyscore.websocket.domain.Studio;
import br.furb.easyscore.websocket.exceptions.EasyScoreException;

public class StudioHandler {

	private static final String MESSAGE_TYPE = "type";

	private static Integer ID_SCORE = 0;

	private static Integer ID = 0;

	private List<Studio> studios = new ArrayList<Studio>();

	public Map<String, String> handleRequest(String sessionId, String msg) {
		/**
		 * Verificar o tipo da mensagem, se é uma mensagem de criação de grupo,
		 * ou notificação para os demais usuários do grupo.
		 * */
		Map<String, String> content = new HashMap<String, String>();
		String returningMessage = "";
		try {
			JsonObject json = Json.createReader(new StringReader(msg))
					.readObject();
			switch (MessageTypes.valueOf(json.getString(MESSAGE_TYPE)
					.toUpperCase())) {
			case CREATE:
				returningMessage = this.createStudio(sessionId, json);
				content.put(sessionId, returningMessage);
				return content;
			case JOIN:
				returningMessage = this.joinStudio(json);
				content.put(sessionId, returningMessage);
				return content;
			case LIST:
				returningMessage = this.listStudios(sessionId, json);
				content.put(sessionId, returningMessage);
				return content;
			case SEND_SCORE:
				returningMessage = this.createScore(sessionId, json);
				content.put(sessionId, returningMessage);
				return content;
			case GET_SCORE:
				returningMessage = this.getScore(sessionId, json);
				content.put(sessionId, returningMessage);
				return content;
			case SERVER_TIME:
				returningMessage = this.getServerTime(sessionId, json);
				content.put(sessionId, returningMessage);
				return content;
			case LIST_ALL_SCORES:
				returningMessage = this.getAllScores(sessionId, json);
				content.put(sessionId, returningMessage);
				return content;
			default:
				throw new EasyScoreException();
			}
		} catch (JsonParsingException | EasyScoreException jpe) {
			returningMessage = Json.createObjectBuilder()
					.add("message", "Formato Json Inválido").build().toString();
			content.put(sessionId, returningMessage);
			return content;

		}
	}

	private JsonObject addMessage(JsonObjectBuilder studio, String msg) {
		studio.add("message", msg);
		return studio.build();
	}

	public String createStudio(String sessionId, JsonObject studioJson) {
		JsonArray studioValues = studioJson.getJsonArray("values");
		Studio studio = new Studio();
		studio.setId(getNextId());
		studio.setName(studioValues.getJsonObject(0).getString("name"));
		// String start = studioValues.getJsonObject(0).getString("start");
		studio.setStart(studioValues.getJsonObject(0).getInt("start"));
		studio.setSpeed(studioValues.getJsonObject(0).getInt("speed"));
		studios.add(studio);
		JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
		responseBuilder.add("studio", createStudioObject(studio));
		return addMessage(responseBuilder, "OK").toString();
	}

	private JsonObjectBuilder createStudioObject(Studio studio) {
		JsonObjectBuilder studioBuilder = Json.createObjectBuilder();
		studioBuilder.add("id", studio.getId());
		studioBuilder.add("name", studio.getName());
		studioBuilder.add("start", studio.getStart());
		studioBuilder.add("speed", studio.getSpeed());
		return studioBuilder;
	}

	private String createScore(String sessionId, JsonObject json) {
		JsonArray studioValues = json.getJsonArray("values");
		Integer studioId = studioValues.getJsonObject(0).getInt("studio_id");
		
		String name = studioValues.getJsonObject(0).getString("name");
		String content = studioValues.getJsonObject(0).getString("content");
		Score score = new Score(getNextScoreId(), name, content);
		
		if (studioId != null) {
			Studio studio = getStudioById(studioId);
			studio.getScores().add(score);
			return addMessage(Json.createObjectBuilder(), "OK - Adicionado ao grupo").toString();
		} else {
			return addMessage(Json.createObjectBuilder(), "OK").toString();
		}
	}

	private JsonObjectBuilder createScoreObject(Score score, Studio studio, boolean sendContent) {
		JsonObjectBuilder studioBuilder = Json.createObjectBuilder();
		studioBuilder.add("id", score.getId());
		studioBuilder.add("name", score.getName());
		if (sendContent)
			studioBuilder.add("content", score.getContent());
			studioBuilder.add("speed", studio.getSpeed());
			studioBuilder.add("start", studio.getStart());
		return studioBuilder;
	}

	private String getAllScores(String sessionId, JsonObject json) {
		JsonArray studioValues = json.getJsonArray("values");
		Integer studioId = null;
		if (studioValues != null) {
			studioId = studioValues.getJsonObject(0).getInt("studio_id");
		}
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		List<Studio> studioToList = new ArrayList<Studio>();
		if (studioId != null) {
			Studio studio = getStudioById(studioId);
			if (studio == null) {
				return addMessage(Json.createObjectBuilder(),
						"Estúdio não encontrado").toString();
			}
			studioToList.add(studio);
		} else {
			studioToList = studios;
		}
		for (Studio studio : studioToList) {
			for (Score score : studio.getScores()) {
				jsonArrayBuilder.add((createScoreObject(score, studio, true).build()));
			}
		}

		return addMessage(jsonArrayBuilder.build(), "scores", "OK").toString();
	}

	private String getServerTime(String sessionId, JsonObject json) {
		JsonObjectBuilder response = Json.createObjectBuilder().add("type",
				"server_time");
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder objBuilder = Json.createObjectBuilder();
		Calendar calendar = Calendar.getInstance();

		//eeeeu
		Date dt_send = new Date(calendar.getTimeInMillis());
		Date dt_start = new Date(1415136329828L);
		
		objBuilder.add("tempo_send", sdf.format(dt_send.getTime()));
		objBuilder.add("tempo_start", sdf.format(dt_start.getTime()));
		objBuilder.add("diferença", dt_start.getTime() - dt_send.getTime());
		objBuilder.add("tempoogettime", dt_send.getTime());
		//acabou
		
		jsonArrayBuilder.add(objBuilder);
		response.add("values", jsonArrayBuilder);
		
		return response.build().toString();
	}

	private String getScore(String sessionId, JsonObject json) {
		JsonArray studioValues = json.getJsonArray("values");
		Integer studioId = studioValues.getJsonObject(0).getInt("studio_id");
		Integer scoreId = studioValues.getJsonObject(0).getInt("score_id");
		Studio studio = getStudioById(studioId);
		if (studio == null) {
			return addMessage(Json.createObjectBuilder(),
					"Estúdio não encontrado").toString();
		}
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (Score score : studio.getScores()) {
			if (score.getId().equals(scoreId)) {
				jsonArrayBuilder.add((createScoreObject(score, studio, true).build()));
			}
		}
		return addMessage(jsonArrayBuilder.build(), "scores", "OK").toString();
	}

	private String listStudios(String sessionId, JsonObject json) {
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (Studio studio : studios) {
			jsonArrayBuilder.add((createStudioObject(studio).build()));
		}

		return addMessage(jsonArrayBuilder.build(), "studios", "OK").toString();
	}

	private JsonObject addMessage(JsonArray objects, String listName, String msg) {
		JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
		responseBuilder.add(listName, objects);
		responseBuilder.add("message", msg);
		return responseBuilder.build();
	}

	// Usuário acessa o estúdio e seleciona partitura desejada - RETORNO com
	// partitura, tempo e velocidade
	private String joinStudio(JsonObject json) {
		JsonArray studioValues = json.getJsonArray("values");
		Integer studioId = studioValues.getJsonObject(0).getInt("studio_id");
		Integer scoreId = studioValues.getJsonObject(0).getInt("score_id");
		// Map<String, String> ret = new HashMap<String, String>();
		Studio studio = getStudioById(studioId);
		
		if (studio != null) {
			JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
			for (Score score : studio.getScores()) {
				if (score.getId() == scoreId) {
					jsonArrayBuilder.add((createScoreObject(score, studio, true).build()));
					//sair do for
				}
			}

			JsonObjectBuilder objBuilder = Json.createObjectBuilder();
			JsonObjectBuilder notifyBuilder = Json.createObjectBuilder();
			notifyBuilder.add("type", "notify");
			//JsonArray msgValues = json.getJsonArray("values");

			objBuilder.add("studio_id",studioId.toString());
			objBuilder.add("score_id",scoreId.toString());
			Calendar calendar = Calendar.getInstance(); // gets a calendar using
														// the
														// default time zone and
														// locale.
			//https://github.com/braziljs/add2weekly/issues/1
			//calendar.set(Calendar.SECOND, 30);
			
			objBuilder.add("tempo", String.valueOf(studio.getStart() - calendar.getTimeInMillis()));
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			arrayBuilder.add(objBuilder);
			notifyBuilder.add("values", arrayBuilder.build());

			return addMessage(jsonArrayBuilder.build(), "join", "OK")
					.toString();
		} else {
			return addMessage(Json.createObjectBuilder(),
					"Estúdio não encontrado").toString();
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Integer getNextId() {
		ID++;
		return ID;
	}

	private Integer getNextScoreId() {
		ID_SCORE++;
		return ID_SCORE;
	}

	public Studio getStudioById(Integer id) {
		for (Studio studio : studios) {
			if (studio.getId().equals(id)) {
				return studio;
			}
		}
		return null;
	}
}
