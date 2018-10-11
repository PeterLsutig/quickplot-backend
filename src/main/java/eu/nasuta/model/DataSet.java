package eu.nasuta.model;

import eu.nasuta.model.table.VarTable;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

public class DataSet {

	@Id
	private UUID uuid;
	private String name;
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private String description;
	private User owner;
	private Visibility visibility;
	private LocalDateTime date;
	private String graphType;
	private VarTable data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getGraphType() {
		return graphType;
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}

	public VarTable getData() {
		return data;
	}

	public void setData(VarTable data) {
		this.data = data;
	}

	public enum Visibility {
		PUBLIC,
		PRIVATE
	}

	@Override
	@SuppressWarnings("MagicCharacter")
	public String toString() {
		return "{\"@class\":\"DataSet\""
				+ ", \"uuid\":" + uuid
				+ ", \"name\":\"" + name + '"'
				+ ", \"fileName\":\"" + fileName + '"'
				+ ", \"description\":\"" + description + '"'
				+ ", \"owner\":" + owner
				+ ", \"visibility\":\"" + visibility + '"'
				+ ", \"date\":" + date
				+ ", \"graphType\":\"" + graphType + '"'
				+ ", \"data\":" + data
				+ '}';
	}
}
