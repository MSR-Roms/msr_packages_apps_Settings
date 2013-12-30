/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.ListFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wimax.WimaxHelper;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.telephony.Phone;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SpaceWidget extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SpaceWidget";
    private static final String SEPARATOR = "OV=I=XseparatorX=I=VO";
    private static final String UI_EXP_WIDGET = "quicker_widget";
    private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "quicker_hide_onchange";
    private static final String UI_EXP_WIDGET_HIDE_SCROLLBAR = "quicker_hide_scrollbar";
    private static final String UI_EXP_WIDGET_HAPTIC_FEEDBACK = "quicker_haptic_feedback";
    private static final String SPACE_STATUS_BAR_QUICKER_PANEL = "status_bar_quicker_panel";

    private CheckBoxPreference mSpaceWidget;
    private CheckBoxPreference mUseQuickerPanel;
    private CheckBoxPreference mSpaceWidgetHideOnChange;
    private CheckBoxPreference mSpaceWidgetHideScrollBar;
    private ListPreference mSpaceWidgetHapticFeedback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {
            addPreferencesFromResource(R.xml.space_widget_settings);

            PreferenceScreen prefSet = getPreferenceScreen();

            mUseQuickerPanel = (CheckBoxPreference) prefSet.findPreference(SPACE_STATUS_BAR_QUICKER_PANEL);
            mSpaceWidget = (CheckBoxPreference) prefSet.findPreference(UI_EXP_WIDGET);
            mSpaceWidgetHideOnChange = (CheckBoxPreference) prefSet
                    .findPreference(UI_EXP_WIDGET_HIDE_ONCHANGE);
            mSpaceWidgetHideScrollBar = (CheckBoxPreference) prefSet
                    .findPreference(UI_EXP_WIDGET_HIDE_SCROLLBAR);

            mSpaceWidgetHapticFeedback = (ListPreference) prefSet
                    .findPreference(UI_EXP_WIDGET_HAPTIC_FEEDBACK);
            mSpaceWidgetHapticFeedback.setOnPreferenceChangeListener(this);
            mSpaceWidgetHapticFeedback.setSummary(mSpaceWidgetHapticFeedback.getEntry());

            mUseQuickerPanel.setChecked((Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(),
                    Settings.System.STATUS_BAR_QUICKER_PANEL, 1) == 1));
            mSpaceWidget.setChecked((Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(),
                    Settings.System.QUICKER_VIEW_WIDGET, 1) == 1));
            mSpaceWidgetHideOnChange.setChecked((Settings.System.getInt(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_HIDE_ONCHANGE, 0) == 1));
            mSpaceWidgetHideScrollBar.setChecked((Settings.System.getInt(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_HIDE_SCROLLBAR, 0) == 1));
            mSpaceWidgetHapticFeedback.setValue(Integer.toString(Settings.System.getInt(
                    getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_HAPTIC_FEEDBACK, 2)));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSpaceWidgetHapticFeedback) {
            int intValue = Integer.parseInt((String) newValue);
            int index = mSpaceWidgetHapticFeedback.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_HAPTIC_FEEDBACK, intValue);
            mSpaceWidgetHapticFeedback.setSummary(mSpaceWidgetHapticFeedback.getEntries()[index]);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mUseQuickerPanel) {
            value = mUseQuickerPanel.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_QUICKER_PANEL, value ? 1 : 0);
            return true;
        } else if (preference == mSpaceWidget) {
            value = mSpaceWidget.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_VIEW_WIDGET,
                    value ? 1 : 0);
        } else if (preference == mSpaceWidgetHideOnChange) {
            value = mSpaceWidgetHideOnChange.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_HIDE_ONCHANGE,
                    value ? 1 : 0);
        } else if (preference == mSpaceWidgetHideScrollBar) {
            value = mSpaceWidgetHideScrollBar.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QUICKER_HIDE_SCROLLBAR,
                    value ? 1 : 0);
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    public static class SpaceWidgetChooser extends SettingsPreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        public SpaceWidgetChooser() {
        }

        private static final String TAG = "SpaceWidgetActivity";

        private static final String BUTTONS_CATEGORY = "pref_space_buttons";
        private static final String BUTTON_MODES_CATEGORY = "pref_space_buttons_modes";
        private static final String SELECT_BUTTON_KEY_PREFIX = "pref_space_button_";

        private static final String EXP_BRIGHTNESS_MODE = "pref_space_brightness_mode";
        private static final String EXP_NETWORK_MODE = "pref_space_network_mode";
        private static final String EXP_SCREENTIMEOUT_MODE = "pref_space_screentimeout_mode";
        private static final String EXP_RING_MODE = "pref_space_ring_mode";
        private static final String EXP_FLASH_MODE = "pref_space_flash_mode";

        private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

        MultiSelectListPreference mBrightnessMode;
        ListPreference mNetworkMode;
        ListPreference mScreenTimeoutMode;
        MultiSelectListPreference mRingMode;
        ListPreference mFlashMode;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            addPreferencesFromResource(R.xml.space_widget);

            PreferenceScreen prefSet = getPreferenceScreen();
            PackageManager pm = getPackageManager();

            if (getActivity().getApplicationContext() == null) {
                return;
            }

            mBrightnessMode = (MultiSelectListPreference) prefSet
                    .findPreference(EXP_BRIGHTNESS_MODE);
            String storedBrightnessMode = Settings.System.getString(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_BRIGHTNESS_MODE);
            if (storedBrightnessMode != null) {
                String[] brightnessModeArray = TextUtils.split(storedBrightnessMode, SEPARATOR);
                mBrightnessMode.setValues(new HashSet<String>(Arrays.asList(brightnessModeArray)));
                updateSummary(storedBrightnessMode, mBrightnessMode, R.string.pref_brightness_mode_summary);
            }
            mBrightnessMode.setOnPreferenceChangeListener(this);
            mNetworkMode = (ListPreference) prefSet.findPreference(EXP_NETWORK_MODE);
            mNetworkMode.setOnPreferenceChangeListener(this);
            mScreenTimeoutMode = (ListPreference) prefSet.findPreference(EXP_SCREENTIMEOUT_MODE);
            mScreenTimeoutMode.setOnPreferenceChangeListener(this);
            mRingMode = (MultiSelectListPreference) prefSet.findPreference(EXP_RING_MODE);
            String storedRingMode = Settings.System.getString(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_RING_MODE);
            if (storedRingMode != null) {
                String[] ringModeArray = TextUtils.split(storedRingMode, SEPARATOR);
                mRingMode.setValues(new HashSet<String>(Arrays.asList(ringModeArray)));
                updateSummary(storedRingMode, mRingMode, R.string.pref_ring_mode_summary);
            }
            mRingMode.setOnPreferenceChangeListener(this);
            mFlashMode = (ListPreference) prefSet.findPreference(EXP_FLASH_MODE);
            mFlashMode.setOnPreferenceChangeListener(this);

            // TODO: set the default values of the items

            // Update the summary text
            mNetworkMode.setSummary(mNetworkMode.getEntry());
            mScreenTimeoutMode.setSummary(mScreenTimeoutMode.getEntry());
            mFlashMode.setSummary(mFlashMode.getEntry());

            // Add the available buttons to the list
            PreferenceCategory prefButtons = (PreferenceCategory) prefSet
                    .findPreference(BUTTONS_CATEGORY);

            // Add the available mode buttons, incase they need to be removed later
            PreferenceCategory prefButtonsModes = (PreferenceCategory) prefSet
                    .findPreference(BUTTON_MODES_CATEGORY);

            // empty our preference category and set it to order as added
            prefButtons.removeAll();
            prefButtons.setOrderingAsAdded(false);

            // emtpy our checkbox map
            mCheckBoxPrefs.clear();

            // get our list of buttons
            ArrayList<String> buttonList = SpaceWidgetUtil.getButtonListFromString(SpaceWidgetUtil
                    .getCurrentButtons(getActivity().getApplicationContext()));

            // Don't show WiMAX option if not supported
            boolean isWimaxEnabled = WimaxHelper.isWimaxSupported(getActivity());
            if (!isWimaxEnabled) {
                SpaceWidgetUtil.BUTTONS.remove(SpaceWidgetUtil.BUTTON_WIMAX);
            }

            // Don't show mobile data options if not supported
            boolean isMobileData = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
            if (!isMobileData) {
                SpaceWidgetUtil.BUTTONS.remove(SpaceWidgetUtil.BUTTON_MOBILEDATA);
                SpaceWidgetUtil.BUTTONS.remove(SpaceWidgetUtil.BUTTON_NETWORKMODE);
                SpaceWidgetUtil.BUTTONS.remove(SpaceWidgetUtil.BUTTON_WIFIAP);
                prefButtonsModes.removePreference(mNetworkMode);
            }

            // fill that checkbox map!
            for (SpaceWidgetUtil.ButtonInfo button : SpaceWidgetUtil.BUTTONS.values()) {
                // create a checkbox
                CheckBoxPreference cb = new CheckBoxPreference(getActivity()
                        .getApplicationContext());

                // set a dynamic key based on button id
                cb.setKey(SELECT_BUTTON_KEY_PREFIX + button.getId());

                // set vanity info
                cb.setTitle(button.getTitleResId());

                // set our checked state
                if (buttonList.contains(button.getId())) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }

                // add to our prefs set
                mCheckBoxPrefs.put(cb, button.getId());

                // specific checks for availability on some platforms
                if (SpaceWidgetUtil.BUTTON_FLASHLIGHT.equals(button.getId()) &&
                        !getResources().getBoolean(R.bool.has_led_flash)) {
                    // disable flashlight if it's not supported
                    cb.setEnabled(false);
                    mFlashMode.setEnabled(false);
                } else if (SpaceWidgetUtil.BUTTON_NETWORKMODE.equals(button.getId())) {
                    // some phones run on networks not supported by this button,
                    // so disable it
                    int network_state = -99;

                    try {
                        network_state = Settings.Secure.getInt(getActivity()
                                .getApplicationContext().getContentResolver(),
                                Settings.Secure.PREFERRED_NETWORK_MODE);
                    } catch (Settings.SettingNotFoundException e) {
                        Log.e(TAG, "Unable to retrieve PREFERRED_NETWORK_MODE", e);
                    }

                    switch (network_state) {
                    // list of supported network modes
                        case Phone.NT_MODE_WCDMA_PREF:
                        case Phone.NT_MODE_WCDMA_ONLY:
                        case Phone.NT_MODE_GSM_UMTS:
                        case Phone.NT_MODE_GSM_ONLY:
                            break;
                        default:
                            cb.setEnabled(false);
                            break;
                    }
                }

                // add to the category
                prefButtons.addPreference(cb);
            }
        }

        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                Preference preference) {
            // we only modify the button list if it was one of our checks that
            // was clicked
            boolean buttonWasModified = false;
            ArrayList<String> buttonList = new ArrayList<String>();
            for (Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs.entrySet()) {
                if (entry.getKey().isChecked()) {
                    buttonList.add(entry.getValue());
                }

                if (preference == entry.getKey()) {
                    buttonWasModified = true;
                }
            }

            if (buttonWasModified) {
                // now we do some wizardry and reset the button list
                SpaceWidgetUtil.saveCurrentButtons(getActivity().getApplicationContext(),
                        SpaceWidgetUtil.mergeInNewButtonString(
                                SpaceWidgetUtil.getCurrentButtons(getActivity()
                                        .getApplicationContext()), SpaceWidgetUtil
                                        .getButtonStringFromList(buttonList)));
                return true;
            }

            return false;
        }

        private class MultiSelectListPreferenceComparator implements Comparator<String> {
            private MultiSelectListPreference pref;

            MultiSelectListPreferenceComparator(MultiSelectListPreference p) {
                pref = p;
            }

            @Override
            public int compare(String lhs, String rhs) {
                return Integer.compare(pref.findIndexOfValue(lhs),
                        pref.findIndexOfValue(rhs));
            }
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mBrightnessMode) {
                ArrayList<String> arrValue = new ArrayList<String>((Set<String>) newValue);
                Collections.sort(arrValue, new MultiSelectListPreferenceComparator(mBrightnessMode));
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_BRIGHTNESS_MODE, TextUtils.join(SEPARATOR, arrValue));
                updateSummary(TextUtils.join(SEPARATOR, arrValue),
                        mBrightnessMode, R.string.pref_brightness_mode_summary);
            } else if (preference == mNetworkMode) {
                int value = Integer.valueOf((String) newValue);
                int index = mNetworkMode.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_NETWORK_MODE, value);
                mNetworkMode.setSummary(mNetworkMode.getEntries()[index]);
            } else if (preference == mScreenTimeoutMode) {
                int value = Integer.valueOf((String) newValue);
                int index = mScreenTimeoutMode.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_SCREENTIMEOUT_MODE, value);
                mScreenTimeoutMode.setSummary(mScreenTimeoutMode.getEntries()[index]);
            } else if (preference == mRingMode) {
                ArrayList<String> arrValue = new ArrayList<String>((Set<String>) newValue);
                Collections.sort(arrValue, new MultiSelectListPreferenceComparator(mRingMode));
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_RING_MODE, TextUtils.join(SEPARATOR, arrValue));
                updateSummary(TextUtils.join(SEPARATOR, arrValue), mRingMode, R.string.pref_ring_mode_summary);
            } else if (preference == mFlashMode) {
                int value = Integer.valueOf((String) newValue);
                int index = mFlashMode.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.EXPANDED_FLASH_MODE, value);
                mFlashMode.setSummary(mFlashMode.getEntries()[index]);
            }
            return true;
        }

        private void updateSummary(String val, MultiSelectListPreference pref, int defSummary) {
            // Update summary message with current values
            final String[] values = parseStoredValue(val);
            if (values != null) {
                final int length = values.length;
                final CharSequence[] entries = pref.getEntries();
                StringBuilder summary = new StringBuilder();
                for (int i = 0; i < (length); i++) {
                    CharSequence entry = entries[Integer.parseInt(values[i])];
                    if ((length - i) > 2) {
                        summary.append(entry).append(", ");
                    } else if ((length - i) == 2) {
                        summary.append(entry).append(" & ");
                    } else if ((length - i) == 1) {
                        summary.append(entry);
                    }
                }
                pref.setSummary(summary);
            } else {
                pref.setSummary(defSummary);
            }
        }

        public static String[] parseStoredValue(CharSequence val) {
            if (TextUtils.isEmpty(val)) {
                return null;
            } else {
                return val.toString().split(SEPARATOR);
            }
        }

    }

    public static class SpaceWidgetOrder extends ListFragment
    {
        private static final String TAG = "SpaceWidgetOrderActivity";

        private ListView mButtonList;
        private ButtonAdapter mButtonAdapter;
        View mContentView = null;
        Context mContext;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.order_space_widget_buttons_activity, null);
            return mContentView;
        }

        /** Called when the activity is first created. */
        // @Override
        // public void onCreate(Bundle icicle)
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mContext = getActivity().getApplicationContext();

            mButtonList = getListView();
            ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
            mButtonAdapter = new ButtonAdapter(mContext);
            setListAdapter(mButtonAdapter);
        }

        @Override
        public void onDestroy() {
            ((TouchInterceptor) mButtonList).setDropListener(null);
            setListAdapter(null);
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
            // reload our buttons and invalidate the views for redraw
            mButtonAdapter.reloadButtons();
            mButtonList.invalidateViews();
        }

        private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
                // get the current button list
                ArrayList<String> buttons = SpaceWidgetUtil.getButtonListFromString(
                        SpaceWidgetUtil.getCurrentButtons(mContext));

                // move the button
                if (from < buttons.size()) {
                    String button = buttons.remove(from);

                    if (to <= buttons.size()) {
                        buttons.add(to, button);

                        // save our buttons
                        SpaceWidgetUtil.saveCurrentButtons(mContext,
                                SpaceWidgetUtil.getButtonStringFromList(buttons));

                        // tell our adapter/listview to reload
                        mButtonAdapter.reloadButtons();
                        mButtonList.invalidateViews();
                    }
                }
            }
        };

        private class ButtonAdapter extends BaseAdapter {
            private Context mContext;
            private Resources mSystemUIResources = null;
            private LayoutInflater mInflater;
            private ArrayList<SpaceWidgetUtil.ButtonInfo> mButtons;

            public ButtonAdapter(Context c) {
                mContext = c;
                mInflater = LayoutInflater.from(mContext);

                PackageManager pm = mContext.getPackageManager();
                if (pm != null) {
                    try {
                        mSystemUIResources = pm.getResourcesForApplication("com.android.systemui");
                    } catch (Exception e) {
                        mSystemUIResources = null;
                        Log.e(TAG, "Could not load SystemUI resources", e);
                    }
                }

                reloadButtons();
            }

            public void reloadButtons() {
                ArrayList<String> buttons = SpaceWidgetUtil.getButtonListFromString(
                        SpaceWidgetUtil.getCurrentButtons(mContext));

                mButtons = new ArrayList<SpaceWidgetUtil.ButtonInfo>();
                for (String button : buttons) {
                    if (SpaceWidgetUtil.BUTTONS.containsKey(button)) {
                        mButtons.add(SpaceWidgetUtil.BUTTONS.get(button));
                    }
                }
            }

            public int getCount() {
                return mButtons.size();
            }

            public Object getItem(int position) {
                return mButtons.get(position);
            }

            public long getItemId(int position) {
                return position;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                final View v;
                if (convertView == null) {
                    v = mInflater.inflate(R.layout.order_space_widget_button_list_item, null);
                } else {
                    v = convertView;
                }

                SpaceWidgetUtil.ButtonInfo button = mButtons.get(position);

                final TextView name = (TextView) v.findViewById(R.id.name);
                final ImageView icon = (ImageView) v.findViewById(R.id.icon);

                name.setText(button.getTitleResId());

                // assume no icon first
                icon.setVisibility(View.GONE);

                // attempt to load the icon for this button
                if (mSystemUIResources != null) {
                    int resId = mSystemUIResources.getIdentifier(button.getIcon(), null, null);
                    if (resId > 0) {
                        try {
                            Drawable d = mSystemUIResources.getDrawable(resId);
                            icon.setVisibility(View.VISIBLE);
                            icon.setImageDrawable(d);
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving icon drawable", e);
                        }
                    }
                }

                return v;
            }
        }
    }

}