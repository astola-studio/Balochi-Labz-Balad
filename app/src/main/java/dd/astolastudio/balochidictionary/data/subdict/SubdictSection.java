package dd.astolastudio.balochidictionary.data.subdict;

import java.util.Iterator;
import java.util.LinkedList;

public class SubdictSection {
	private String mHeading = "heading";
	private String mIntro = "intro";
	private final LinkedList<String> mList = new LinkedList();

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<em>");
		buffer.append(this.mHeading);
		buffer.append("</em><br><br>");
		buffer.append(this.mIntro);
		Iterator it = this.mList.iterator();
		while (it.hasNext()) {
			String item = (String) it.next();
			buffer.append("&#8226 ");
			buffer.append(item);
		}
		return buffer.toString();
	}

	public void setHeading(String heading) {
		this.mHeading = heading;
	}

	public void setIntro(String intro) {
		this.mIntro = intro;
	}

	public void addListItem(String item) {
		this.mList.add(item);
	}

	public String getHeading() {
		return this.mHeading;
	}
}
