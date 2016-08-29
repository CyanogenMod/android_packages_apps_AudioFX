package org.cyanogenmod.audiofx.backends;

import android.media.AudioDeviceInfo;
import android.media.audiofx.Equalizer;
import android.util.Log;

import org.cyanogenmod.audiofx.eq.EqUtils;

/**
 * Created by roman on 3/1/16.
 */
public abstract class EffectSetWithAndroidEq extends EffectSet {
    /**
     * Session-specific equalizer
     */
    private Equalizer mEqualizer;

    private short mEqNumPresets = -1;
    private short mEqNumBands = -1;

    public EffectSetWithAndroidEq(int sessionId, AudioDeviceInfo deviceInfo) {
        super(sessionId, deviceInfo);
    }

    @Override
    protected void onCreate() {
        mEqualizer = new Equalizer(100, mSessionId);
        super.onCreate();

    }

    @Override
    public synchronized void release() {
        if (mEqualizer != null) {
            mEqualizer.release();
            mEqualizer = null;
        }
        super.release();
    }

    @Override
    public void setGlobalEnabled(boolean globalEnabled) {
        super.setGlobalEnabled(globalEnabled);

        enableEqualizer(globalEnabled);
    }

    @Override
    public void enableEqualizer(boolean enable) {
        try {
            mEqualizer.setEnabled(enable);
        } catch (Exception e) {
            Log.e(TAG, "enableEqualizer failed! enable=" + enable + " sessionId=" + mSessionId, e);
        }
    }

    @Override
    public void setEqualizerLevelsDecibels(float[] levels) {
        final short[] equalizerLevels = EqUtils.convertDecibelsToMillibelsInShorts(levels);
        for (short i = 0; i < equalizerLevels.length; i++) {
            setBandLevelSafe(i, equalizerLevels[i]);
        }
    }

    @Override
    public short getNumEqualizerBands() {
        if (mEqNumBands < 0) {
            mEqNumBands = mEqualizer.getNumberOfBands();
        }
        return mEqNumBands;
    }

    @Override
    public void setEqualizerBandLevel(short band, float level) {
        setBandLevelSafe(band, (short)level);
    }

    @Override
    public int getEqualizerBandLevel(short band) {
        return mEqualizer.getBandLevel(band);
    }

    @Override
    public String getEqualizerPresetName(short preset) {
        return mEqualizer.getPresetName(preset);
    }

    @Override
    public void useEqualizerPreset(short preset) {
        mEqualizer.usePreset(preset);
    }

    @Override
    public short getNumEqualizerPresets() {
        if (mEqNumPresets < 0) {
            mEqNumPresets = mEqualizer.getNumberOfPresets();
        }
        return mEqNumPresets;
    }

    @Override
    public short[] getEqualizerBandLevelRange() {
        return mEqualizer.getBandLevelRange();
    }

    @Override
    public int getCenterFrequency(short band) {
        return mEqualizer.getCenterFreq(band);
    }

    @Override
    public synchronized void setDevice(AudioDeviceInfo deviceInfo) {
        super.setDevice(deviceInfo);
    }

    private synchronized void setBandLevelSafe(short band, short level) {
        try {
            mEqualizer.setBandLevel(band, level);
        } catch (Exception e) {
            Log.e(TAG, "Unable to set eq band=" + band + " level=" + level, e);
        }
    }
}
