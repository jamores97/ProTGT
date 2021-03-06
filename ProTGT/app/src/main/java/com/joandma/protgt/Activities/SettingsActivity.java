package com.joandma.protgt.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.joandma.protgt.Constant.PreferenceKeys;
import com.joandma.protgt.Fragments.DialogLogOut;
import com.joandma.protgt.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.opciones, menu);
        return true;
    }


    //Este método vuelve para el HomeActivity pulsando en la flecha de atrás
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;

            // Este case vuelve al login de la aplicación borrando el token del usuario

            case R.id.action_logout:

                FragmentManager fragmentManager = getFragmentManager();
                DialogLogOut dialogLogOut = new DialogLogOut();

                dialogLogOut.show(fragmentManager, "tagLogOut");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("RestrictedApi")
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        SharedPreferences prefs;
        EditTextPreference etNombre, etApellidos, etEmail, etTelefono;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_datos_personales);
            setHasOptionsMenu(true);

            prefs = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);

            String nombre = prefs.getString(PreferenceKeys.USER_NAME, null);
            String apellidos = prefs.getString(PreferenceKeys.USER_SURNAME, null);
            String email = prefs.getString(PreferenceKeys.USER_EMAIL, null);
            String telefono = prefs.getString(PreferenceKeys.USER_TELEFONO, null);
            String pais = prefs.getString(PreferenceKeys.USER_PAIS, null);

            etNombre = (EditTextPreference) findPreference("etpNombre");
            etApellidos = (EditTextPreference) findPreference("etpApellidos");
            etEmail = (EditTextPreference) findPreference("etpEmail");
            etTelefono = (EditTextPreference) findPreference("etpTelefono");

            etNombre.setText(nombre);
            etApellidos.setText(apellidos);
            etEmail.setText(email);
            etTelefono.setText(telefono);


            //La key que hay que poner es la que se le da dentro del EditTextPreference
            bindPreferenceSummaryToValue(findPreference("etpNombre"));
            bindPreferenceSummaryToValue(findPreference("etpApellidos"));
            bindPreferenceSummaryToValue(findPreference("etpEmail"));
            bindPreferenceSummaryToValue(findPreference("etpTelefono"));

            //Esto es para saber que pais está eligiendo en las preferencias
            final ListPreference listPreference = (ListPreference) findPreference("lpPais");


            if(listPreference.getEntry()==null) {
                listPreference.setValueIndex(0);
            }

            listPreference.setSummary(pais);
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    listPreference.setValue(newValue.toString());
                    preference.setSummary(listPreference.getEntry());
                    return false;
                }
            });


        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        SharedPreferences prefs;
        EditTextPreference etProvincia, etLocalidad, etCalle, etNumero, etPiso, etBloque, etPuerta;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_domicilio);
            setHasOptionsMenu(true);

            prefs = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);

            String provincia = prefs.getString(PreferenceKeys.ADDRESS_PROVINCIA, null);
            String localidad = prefs.getString(PreferenceKeys.ADDRESS_LOCALIDAD, null);
            String calle = prefs.getString(PreferenceKeys.ADDRESS_CALLE, null);
            int numero = prefs.getInt(PreferenceKeys.ADDRESS_NUMERO, 0);
            String piso = prefs.getString(PreferenceKeys.ADDRESS_PISO, null);
            String bloque = prefs.getString(PreferenceKeys.ADDRESS_BLOQUE, null);
            String puerta = prefs.getString(PreferenceKeys.ADDRESS_PUERTA, null);

            etProvincia = (EditTextPreference) findPreference("etpProvincia");
            etLocalidad = (EditTextPreference) findPreference("etpLocalidad");
            etCalle = (EditTextPreference) findPreference("etpCalle");
            etNumero = (EditTextPreference) findPreference("etpNumero");
            etPiso = (EditTextPreference) findPreference("etpPiso");
            etBloque = (EditTextPreference) findPreference("etpBloque");
            etPuerta = (EditTextPreference) findPreference("etpPuerta");

            etProvincia.setText(provincia);
            etLocalidad.setText(localidad);
            etCalle.setText(calle);
            etNumero.setText(String.valueOf(numero));

            if (piso != null)
                etPiso.setText(piso);
            else
                etPiso.setText("");
            if (bloque != null)
                etBloque.setText(bloque);
            else
                etBloque.setText("");
            if (puerta != null)
                etPuerta.setText(puerta);
            else
                etPuerta.setText("");


            bindPreferenceSummaryToValue(findPreference("etpProvincia"));
            bindPreferenceSummaryToValue(findPreference("etpLocalidad"));
            bindPreferenceSummaryToValue(findPreference("etpCalle"));
            bindPreferenceSummaryToValue(findPreference("etpNumero"));
            bindPreferenceSummaryToValue(findPreference("etpPiso"));
            bindPreferenceSummaryToValue(findPreference("etpBloque"));
            bindPreferenceSummaryToValue(findPreference("etpPuerta"));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_contacto);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
