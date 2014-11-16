package br.furb.easyscore.websocket.domain;

import java.util.ArrayList;
import java.util.List;

public class Studio {

	private Integer id;
	private String name;
	private List<Score> scores;
	private Long start; //timestamp - milisegundos
	private Integer speed;

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public Studio() {
		scores = new ArrayList<Score>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
}
