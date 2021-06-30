package edu.pe.idat.app_qolca.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentHomeBinding;
import edu.pe.idat.app_qolca.databinding.FragmentUserBinding;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public UserFragment() {

    }


    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater,container,false);

        String nombre =SharedPreferenceManager.getSomeStringValue("PREF_NOMBRE");
        String apellido = SharedPreferenceManager.getSomeStringValue("PREF_APELLIDO");

        binding.tvUserNombre.setText(nombre);
        binding.tvUserApellido.setText(apellido);
        binding.tvUserInciales.setText(Character.toString(nombre.charAt(0))+Character.toString(apellido.charAt(0)));


        binding.btnUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceManager.clearValues();
//                mensaje("Adiós " + nombre+", vuelva pronto :)");
                startActivity(new Intent(getContext(),LoginActivity.class));
            }
        });

        return binding.getRoot();
    }

    private void mensaje(String m){
        Toast.makeText(getContext(),
                m,Toast.LENGTH_LONG).show();
    }
}