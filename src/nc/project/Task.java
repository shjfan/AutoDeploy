package nc.project;

import java.util.HashMap;
import java.util.Map;

public class Task{

	private String type;
	private Map<String, Object> para = new HashMap<String, Object>();

	public void addPara(String name, Object value) {
		para.put(name, value);
	}

	public Object getPara(String name) {
		return para.get(name);
	}
	
	public Map<String, Object> getPara(){
		return para;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}