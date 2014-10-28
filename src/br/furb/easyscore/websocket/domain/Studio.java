package br.furb.easyscore.websocket.domain;

import java.util.ArrayList;
import java.util.List;

public class Studio {

	private Integer id;
	private String name;
	private String owner;
	private List<String> members; //grupo de membros da partitura
	private List<Score> scores;

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public Studio() {
		members = new ArrayList<String>();
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

}
