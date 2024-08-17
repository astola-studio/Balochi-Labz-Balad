package dd.astolastudio.balochidictionary.data.subdict;

import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SubdictXmlParser {
	private static final String TAG = "SubdictXmlParser";
	private int mParticipleListNumber = 0;
	private final SubdictSection mSection = new SubdictSection();
	private String mText = "";

	public SubdictSection parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
			parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-docdecl", true);
			parser.setInput(in, null);
			readXml(parser);
			return this.mSection;
		} finally {
			in.close();
		}
	}

	private void readXml(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != 3) {
			String name = parser.getName();
			int i = -1;
			switch (name.hashCode()) {
				case 112:
					if (name.equals("p")) {
						i = 2;
						break;
					}
					break;
				case 3198432:
					if (name.equals("head")) {
						i = 1;
						break;
					}
					break;
				case 1345112207:
					if (name.equals("listBibl")) {
						i = 3;
						break;
					}
					break;
				case 1970241253:
					if (name.equals("section")) {
						i = 0;
						break;
					}
					break;
			}
			switch (i) {
				case 0:
					break;
				case 1:
					readHead(parser);
					break;
				case 2:
					this.mText += "<p>";
					readBody(parser);
					this.mText += "</p>";
					break;
				case 3:
					readListBibl(parser);
					break;
				default:
					skip(parser);
					break;
			}
		}
		this.mSection.setIntro(this.mText);
	}

	private void readHead(XmlPullParser parser) throws XmlPullParserException, IOException {
		this.mSection.setHeading(readText(parser));
	}

	private void readBody(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				this.mText += parser.getText();
			} else if (parser.getName().equals("emph")) {
				this.mText += "<b>" + readText(parser) + "</b>";
			} else if (parser.getName().equals("list")) {
				readList(parser);
			}
		}
	}

	private void readListBibl(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != 3) {
			if (parser.getName().equals("bibl")) {
				StringBuilder text = new StringBuilder();
				while (parser.next() != 3) {
					if (parser.getEventType() == 4) {
						text.append(parser.getText());
					} else if (parser.getName().equals("title")) {
						text.append("<u>").append(readText(parser)).append("</u>");
					} else {
						throw new XmlPullParserException("Unrecognized element: " + parser.getName());
					}
				}
				text.append("<br><br>");
				this.mSection.addListItem(text.toString());
			} else {
				throw new XmlPullParserException("Invalid <listBibl> child");
			}
		}
	}

	private void readList(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != 3) {
			if (!parser.getName().equals("item")) {
				throw new IllegalStateException("<list> element must contain only <item> elements.");
			} else if (!this.mSection.getHeading().equals("Participle") || this.mParticipleListNumber >= 3) {
				readItem(parser);
			} else {
				readParticipleList(parser);
			}
		}
		this.mParticipleListNumber++;
	}

	private String readQuote(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder("\"<em>");
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				text.append(parser.getText());
			} else if (parser.getName().equals("hi") || parser.getName().equals("emph")) {
				text.append("<b>").append(readText(parser)).append("</b>");
			} else if (parser.getName().equals("note")) {
				text.append(readNote(parser));
			} else {
				throw new XmlPullParserException("Unrecognized element: " + parser.getName());
			}
		}
		text.append("</em>\"");
		return text.toString();
	}

	private void readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder();
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				text.append(parser.getText());
			} else if (parser.getName().equals("p")) {
				text.append(readItemParagraph(parser));
			} else {
				throw new IllegalStateException("<item> must contain only <p> elements or plain text.");
			}
		}
		this.mSection.addListItem(text.toString());
	}

	private void readParticipleList(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != 3) {
			this.mText += "<p>";
			if (parser.getEventType() == 4) {
				this.mText += parser.getText();
			} else if (parser.getName().equals("p")) {
				this.mText += readItemParagraph(parser);
			} else {
				throw new IllegalStateException("<item> must contain only <p> elements or plain text.");
			}
			this.mText += "</p>";
		}
	}

	private String readItemParagraph(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder para = new StringBuilder();
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				para.append(parser.getText());
			} else if (parser.getName().equals("hi") || parser.getName().equals("emph")) {
				para.append("<b>").append(readText(parser)).append("</b> ");
			} else if (parser.getName().equals("bibl")) {
				para.append(readBibl(parser));
			} else if (parser.getName().equals("gloss")) {
				para.append(readGloss(parser));
			} else if (parser.getName().equals("quote")) {
				para.append(readQuote(parser));
			} else if (parser.getName().equals("foreign")) {
				para.append(readText(parser));
			} else if (parser.getName().equals("cit")) {
				para.append(readCit(parser));
			}
		}
		para.append("<br><br>");
		return para.toString();
	}

	private String readCit(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder cit = new StringBuilder();
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				cit.append(parser.getText());
			} else if (parser.getName().equals("hi") || parser.getName().equals("emph")) {
				cit.append("<b>").append(readText(parser)).append("</b> ");
			} else if (parser.getName().equals("bibl")) {
				cit.append(readBibl(parser));
			} else if (parser.getName().equals("gloss")) {
				cit.append(readGloss(parser));
			} else if (parser.getName().equals("quote")) {
				cit.append(readQuote(parser));
			} else if (parser.getName().equals("foreign")) {
				cit.append(readText(parser));
			} else if (parser.getName().equals("cit")) {
				cit.append(readItemParagraph(parser));
			}
		}
		return cit.toString();
	}

	private String readGloss(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder();
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				text.append(parser.getText());
			} else if (parser.getName().equals("emph")) {
				text.append("<b>").append(readText(parser)).append("</b>");
			} else {
				throw new XmlPullParserException("Unrecognized element: " + parser.getName());
			}
		}
		return text.toString();
	}

	private String readBibl(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder("<b>");
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				text.append(parser.getText());
			} else if (parser.getName().equals("title")) {
				text.append("<u>").append(readText(parser)).append("</u>");
			} else {
				throw new XmlPullParserException("Unrecognized element: " + parser.getName());
			}
		}
		text.append("</b>");
		return text.toString();
	}

	private String readNote(XmlPullParser parser) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder();
		while (parser.next() != 3) {
			if (parser.getEventType() == 4) {
				text.append(parser.getText());
			} else if (parser.getName().equals("foreign")) {
				text.append(readText(parser));
			} else {
				throw new XmlPullParserException("Unrecognized element: " + parser.getName());
			}
		}
		return text.toString();
	}

	private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
		String result = "";
		if (parser.next() == 4) {
			result = parser.getText();
			parser.nextTag();
			return result;
		}
		Log.e(TAG, "No text found in readText().");
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != 2) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case 2:
					depth++;
					break;
				case 3:
					depth--;
					break;
				default:
					break;
			}
		}
	}
}
