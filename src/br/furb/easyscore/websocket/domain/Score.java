package br.furb.easyscore.websocket.domain;

public class Score {

	private Integer id;
	private String name;
	private String content;
	private Studio studio;

	public Score(Integer id, String name, String content, Studio studio) {
		this.id = id;
		this.name = name;
		this.content = content;
		this.studio = studio;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Studio getStudio() {
		return studio;
	}

	public void setStudio(Studio studio) {
		this.studio = studio;
	}
}
