package dd.astolastudio.balochidictionary.data.wordbook;

import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan.Standard;
import android.text.style.StyleSpan;
import java.util.ArrayList;
import java.util.Iterator;

public class WordbookEntry {
	private String mNote;
	private String mOrth;
	private String mRef;
	private ArrayList<Sense> mSenseList;
	private String mWord;
	private String soundName;

	private static class Sense {
		private final int level;
		private final String n;
		private final SpannedString text;

		public Sense(String level, String n, SpannableString text) {
			this.level = Integer.parseInt(level);
			this.n = n;
			this.text = new SpannedString(text);
		}

		public int getLevel() {
			return this.level;
		}

		public String getN() {
			return this.n;
		}

		public SpannedString getText() {
			return this.text;
		}
	}

	public WordbookEntry() {
		this.mWord = null;
		this.mOrth = null;
		this.mNote = null;
		this.mRef = null;
		this.mSenseList = null;
		this.soundName = null;
		this.mSenseList = new ArrayList();
	}

	public SpannedString toSpanned(float textSize) {
		SpannableString refStr;
		SpannableString noteStr;
		SpannableString wordStr = new SpannableString(this.mOrth);
		wordStr.setSpan(new StyleSpan(1), 0, wordStr.length(), 33);
		if (this.mRef != null) {
			refStr = new SpannableString("\n\n" + this.mRef);
			refStr.setSpan(new StyleSpan(2), 0, refStr.length(), 33);
		} else {
			refStr = new SpannableString("");
		}
		if (this.mNote != null) {
			if (',' == this.mNote.charAt(this.mNote.length() - 1)) {
				this.mNote = this.mNote.substring(0, this.mNote.length() - 1);
			}
			noteStr = new SpannableString("\n\n" + this.mNote);
		} else {
			noteStr = new SpannableString("");
		}
		SpannedString senseListStr = new SpannedString("");
		Iterator it = this.mSenseList.iterator();
		while (it.hasNext()) {
			Sense sense = (Sense) it.next();
			String headingStr = "\n\n";
			int level = sense.getLevel();
			if (level > 0) {
				headingStr = headingStr + sense.getN() + ". ";
			}
			SpannableString headingSpanStr = new SpannableString(headingStr);
			headingSpanStr.setSpan(new StyleSpan(1), 0, headingSpanStr.length(), 33);
			SpannableString senseStr = new SpannableString(TextUtils.concat(new CharSequence[]{headingSpanStr, sense.getText()}));
			if (level > 1) {
				senseStr.setSpan(new Standard((int) (((float) (level - 1)) * textSize)), 0, senseStr.length(), 33);
			}
			senseListStr = (SpannedString) TextUtils.concat(new CharSequence[]{senseListStr, senseStr});
		}
		return new SpannedString(TextUtils.concat(new CharSequence[]{wordStr, refStr, senseListStr, noteStr}));
	}

	public void setWord(String word) {
		this.mWord = word;
	}

	public void setOrth(String orth) {
		this.mOrth = orth;
	}

	public String getWord() {
		return this.mWord;
	}

	public String getOrth() {
		return this.mOrth;
	}

	public void addNote(String note) {
		if (this.mNote == null) {
			this.mNote = note;
		} else {
			this.mNote += "\n\n" + note;
		}
	}

	public void setRef(String ref) {
		this.mRef = ref;
	}

	public void addSense(String level, String n, SpannableString text) {
		this.mSenseList.add(new Sense(level, n, text));
	}
}
