package dd.astolastudio.balochidictionary;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class SoundPlayer {
	private Context mContext;
	private MediaPlayer p = null;

	public SoundPlayer(Context context) {
		this.mContext = context;
		this.p = new MediaPlayer();
	}

	public void playSound(String fileName) {
		try {
			AssetFileDescriptor afd = this.mContext.getAssets().openFd("sound/" + fileName);
			this.p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			this.p.prepare();
		} catch (Exception e) {
		}
		this.p.start();
	}
}
