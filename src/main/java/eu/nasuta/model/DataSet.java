package eu.nasuta.model;

import eu.nasuta.model.table.VarTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSet {

	@Id
	private UUID uuid;
	private String name;
	private String fileName;
	private String fileType;
	private String description;
	private User owner;
	private Visibility visibility;
	private LocalDateTime date;
	private String graphType;
	private VarTable data;

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
