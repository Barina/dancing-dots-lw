package droid.barina.dotslw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.Toast;
import com.google.ads.AdView;

public class DancingDotsLiveWallSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static float[] HSVtoRGB(float[] hsv)
	{
		float[] rgb = new float[3];
		int i;
		float f, p, q, t, h = hsv[0], s = hsv[1], v = hsv[2], r = rgb[0], g = rgb[1], b = rgb[2];
		if(s == 0)
		{
			// achromatic (grey)
			r = g = b = v;
			return rgb;
		}
		h /= 60; // sector 0 to 5
		i = (int)Math.floor(h);
		f = h - i; // factorial part of h
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));
		switch (i)
		{
			case 0:
				r = v;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = v;
				b = p;
				break;
			case 2:
				r = p;
				g = v;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = v;
				break;
			case 4:
				r = t;
				g = p;
				b = v;
				break;
			default: // case 5:
				r = v;
				g = p;
				b = q;
				break;
		}
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
		return rgb;
	}

	private AdView adView;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(getString(R.string.shared_prefs_name));
		addPreferencesFromResource(R.xml.wallpaper_settings);
		this.settings = PreferenceManager.getDefaultSharedPreferences(this);
		this.settings.registerOnSharedPreferenceChangeListener(this);
		EditTextPreference gapPref = (EditTextPreference)findPreference(getString(R.string.key_gap_setting));
		gapPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		gapPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				int gap = Integer.parseInt(newValue.toString());
				if(!newValue.toString().equals("") && newValue.toString().matches("\\d*") && (gap >= 25 && gap < DancingDotsLiveWall.CAMERA_WIDTH * .5f))
					return true;
				Toast.makeText(DancingDotsLiveWallSettings.this, getString(R.string.corrent_value) + " " + (DancingDotsLiveWall.CAMERA_WIDTH * .5f) + ".", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
		EditTextPreference radiusPref = (EditTextPreference)findPreference(getString(R.string.key_radius_setting));
		radiusPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		radiusPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				return !newValue.toString().equals("") && newValue.toString().matches("\\d*");
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy()
	{
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		if(adView != null)
			adView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
//		if(key.equals(getString(R.string.key_bgcolor_setting)))
//		{
//			int color = ((AmbilWarnaPreference)findPreference(key)).value;
//			// LiveWallScene.getSharedInstance().SetBackgroundColor(color);
////			Editor editor = settings.edit();
////			editor.putInt(key, color);
////			editor.commit();
//		}
//		else
//			if(key.equals(getString(R.string.key_gap_setting)))
//			{
//				String gapString = ((EditTextPreference)findPreference(key)).getText();
//				// sharedPreferences.getString(key,
//				// getResources().getString(R.string.default_gap));
//				int gap = Integer.parseInt(gapString);
//				// LiveWallScene.getSharedInstance().setGap(gap);
////				Editor editor = settings.edit();
////				editor.putInt(key, gap);
////				editor.commit();
//			}
//			else
//				if(key.equals(getString(R.string.key_radius_setting)))
//				{
//					String radString = ((EditTextPreference)findPreference(key)).getText();
//					int radius = Integer.parseInt(radString);
////					int def = getResources().getInteger(R.integer.default_radius);
////					Log.d("DancingDots", "def = " + def);
////					int rad = sharedPreferences.getInt(key, def);
////					Log.d("DancingDots", "rad = " + rad);
////					int radius = Integer.parseInt(rad);
////					Log.d("DancingDots", "radius = " + radius);
//					// LiveWallScene.getSharedInstance().setDistance(radius);
////					Editor editor = settings.edit();
////					editor.putInt(key, radius);
////					editor.commit();
//				}
	}
}