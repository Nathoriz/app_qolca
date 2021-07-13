package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

    public UserFragment() {

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

        binding.btnUserLogout.setOnClickListener(view ->{
            logout();
        });
        binding.lyUserEdit.setOnClickListener(view->{
            EditUserFragment fragment = new EditUserFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.lyUserEditpass.setOnClickListener(view->{
            EditPasswordFragment fragment = new EditPasswordFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return binding.getRoot();
    }

    private void logout(){
        SharedPreferenceManager.clearValues();
        startActivity(new Intent(getContext(),LoginActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}