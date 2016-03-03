package nc.project;

import java.io.File;
import java.util.List;

import nc.tool.FileUtil;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Project {

	public static ProjectVO getProject(String xmlfile) throws DocumentException {
		try {
			FileUtil.log("开始解析xml文件" + xmlfile);
			ProjectVO projectvo = new ProjectVO();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(xmlfile));
			// 获取根元素
			Element root = document.getRootElement();
			FileUtil.log("root:" + root.getName());
			// 获取所有子元素
			List<Element> childList = root.elements("task");
			FileUtil.log("total task count:" + childList.size());

			for (int i = 0; i < childList.size(); i++) {
				FileUtil.log("task start analysis no :" + i);
				Task task = new Task();
				Element e = childList.get(i);
				FileUtil.log("task type :" + e.attributeValue("type"));
				task.setType(e.attributeValue("type"));
				List<Element> paraList = e.elements("para");
				if (paraList != null && paraList.size() > 0) {
					for (int j = 0; j < paraList.size(); j++) {
						Element ej = paraList.get(j);
						String paraname = ej.attributeValue("name");
						FileUtil.log("paraname=" + paraname);
						String paravalue = ej.getStringValue();
						FileUtil.log("paravalue=" + paravalue);
						task.addPara(paraname, paravalue);
					}
				}
				projectvo.addTask(task);
				FileUtil.log("task end analysis no :" + i);
			}
			return projectvo;
		} catch (DocumentException e) {
			FileUtil.log(e);
			throw e;
		}
	}
}
