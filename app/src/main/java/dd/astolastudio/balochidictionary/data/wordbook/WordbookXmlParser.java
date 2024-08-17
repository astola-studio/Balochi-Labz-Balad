package dd.astolastudio.balochidictionary.data.wordbook;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class WordbookXmlParser {
	private static final String NAMESPACE = null;
	private static final String TAG = "WordbookXmlParser";

	public WordbookEntry parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
			parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-docdecl", true);
			parser.setInput(in, null);
			WordbookEntry readXml = readXml(parser);
			return readXml;
		} finally {
			in.close();
		}
	}

	private WordbookEntry readXml(XmlPullParser parser) throws XmlPullParserException, IOException {
		WordbookEntry entry = null;
		while (parser.next() != 3) {
			if (4 != parser.getEventType()) {
				String name = parser.getName();
				
				int i=-1;
				switch (name.hashCode()) {
					case 3124227:
						if (name.equals("etym")) {
							i = 3;
							break;
						}
						break;
					case 3148996:
						if (name.equals("form")) {
							i = 1;
							break;
						}
						break;
					case 3387378:
						if (name.equals("note")) {
							i = 2;
							break;
						}
						break;
					case 96667762:
						if (name.equals("entry")) {
							i = 0;
							break;
						}
						break;
					case 109323182:
						if (name.equals("sense")) {
							i = 4;
							break;
						}
						break;
				}
				switch (i) {
					case 0:
						entry = readEntry(parser);
						break;
					case 1:
						readForm(parser, entry);
						break;
					case 2:
						readNote(parser, entry);
						break;
					case 3:
						readEtym(parser, entry);
						break;
					case 4:
						readSense(parser, entry);
						break;
					default:
						skip(parser);
						break;
				}
			}
		}
		return entry;
	}

	private WordbookEntry readEntry(XmlPullParser parser) {
		String word = parser.getAttributeValue(NAMESPACE, "key");
		WordbookEntry entry = new WordbookEntry();
		entry.setWord(word);
		return entry;
	}

	private void readForm(XmlPullParser parser, WordbookEntry entry) throws XmlPullParserException, IOException {
		while (parser.next() != 3) {
			if (parser.getName().equals("orth")) {
				entry.setOrth(readText(parser));
			} else {
				skip(parser);
			}
		}
	}

	private void readNote(XmlPullParser parser, WordbookEntry entry) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder();
		while (parser.next() != 3) {
			String name = parser.getName();
			if (name == null || !name.equals("foreign")) {
				text.append(parser.getText());
			} else {
				text.append(readText(parser));
			}
		}
		entry.addNote(text.toString());
	}

	private void readEtym(XmlPullParser parser, WordbookEntry entry) throws XmlPullParserException, IOException {
		StringBuilder text = new StringBuilder();
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case 2:
					depth++;
					break;
				case 3:
					depth--;
					break;
			}
			if (4 == parser.getEventType()) {
				text.append(parser.getText());
			} else if (2 == parser.getEventType()) {
				String name = parser.getName();
				if (name.equals("ref") || name.equals("foreign")) {
					parser.next();
					text.append(parser.getText());
				}
			}
		}
		entry.setRef(text.toString());
	}

	private void readSense(XmlPullParser parser, WordbookEntry entry) throws XmlPullParserException, IOException {
		int depth = 1;
		SpannableString text = new SpannableString("");
		String level = parser.getAttributeValue(NAMESPACE, "level");
		String n = parser.getAttributeValue(NAMESPACE, "n");
		while (depth != 0) {
			switch (parser.next()) {
				case 2:
					depth++;
					break;
				case 3:
					depth--;
					break;
			}
			if (4 == parser.getEventType()) {
				text = new SpannableString(TextUtils.concat(new CharSequence[]{text, parser.getText()}));
			} else if (2 == parser.getEventType()) {
				String name = parser.getName();
				SpannableString newStr;
				if (name.equals("tr") && parser.getText() != null) {
					parser.next();
					newStr = new SpannableString(parser.getText());
					newStr.setSpan(new StyleSpan(2), 0, newStr.length(), 33);
					text = new SpannableString(TextUtils.concat(new CharSequence[]{text, newStr}));
				} else if (name.equals("foreign")) {
					parser.next();
					newStr = new SpannableString(parser.getText());
					newStr.setSpan(new StyleSpan(2), 0, newStr.length(), 33);
					text = new SpannableString(TextUtils.concat(new CharSequence[]{text, newStr}));
				} else if (name.equals("usg")) {
					parser.next();
					newStr = new SpannableString(parser.getText());
					newStr.setSpan(new StyleSpan(1), 0, newStr.length(), 33);
					text = new SpannableString(TextUtils.concat(new CharSequence[]{text, newStr}));
				}
			}
		}
		entry.addSense(level, n, text);
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
}
