package com.myapp.fibonacciapp.fragments;

import static com.myapp.fibonacciapp.utils.Utils.SIGNED_IN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.myapp.fibonacciapp.R;
import com.myapp.fibonacciapp.databinding.FragmentAuthenticationBinding;
import com.myapp.fibonacciapp.utils.Utils;

public class AuthenticationFragment extends Fragment {

  private FragmentAuthenticationBinding binding;
  private FibonacciFragment fibonacciFragment;
  private GoogleSignInOptions gso;
  private GoogleSignInClient googleSignInClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    binding = FragmentAuthenticationBinding.inflate(inflater,container,false);
    View view = binding.getRoot();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    instantiate();
    initialize();
    listen();
    load();
  }

  private void instantiate() {
    fibonacciFragment = new FibonacciFragment();

    checkIfSignedIn();

    gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();
    googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
  }

  private void initialize() {

  }

  private void listen() {
    binding.signInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Utils.RC_SIGN_IN);
      }
    });
  }

  private void load() {}

  private void checkIfSignedIn() {
    SharedPreferences authSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    String status = authSharedPref.getString(Utils.STATUS, "");
    if (!status.isEmpty()) {
      if (status.equals(SIGNED_IN)) {
        navigateToFibonacciFragment(fibonacciFragment);
      }
    }
  }

  private void navigateToFibonacciFragment(FibonacciFragment fibonacciFragment) {

    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.nav_host_fragment, fibonacciFragment);
    fragmentTransaction.addToBackStack(null);

    fragmentTransaction.commit();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Utils.RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleSignInResult(task);
    }
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);

      SharedPreferences authSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = authSharedPref.edit();

      editor.putString(Utils.EMAIL, account.getEmail());
      editor.putString(Utils.NAME, account.getDisplayName());
      editor.putString(Utils.ACCOUNT_ID, account.getId());
      editor.putString(Utils.STATUS, SIGNED_IN);

      editor.apply();

      navigateToFibonacciFragment(fibonacciFragment);

    } catch (ApiException e) {
      Log.w("Main", "signInResult:failed code=" + e);
      Toast.makeText(getContext(),"Not able to authenticate!!",Toast.LENGTH_LONG).show();
    }
  }
}