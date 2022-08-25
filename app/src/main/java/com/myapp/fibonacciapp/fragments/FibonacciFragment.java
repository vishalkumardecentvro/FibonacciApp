package com.myapp.fibonacciapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.myapp.fibonacciapp.MainActivity;
import com.myapp.fibonacciapp.R;
import com.myapp.fibonacciapp.databinding.FragmentFibonacciBinding;
import com.myapp.fibonacciapp.utils.Utils;


public class FibonacciFragment extends Fragment {
  private FragmentFibonacciBinding binding;
  private GoogleSignInOptions gso;
  private GoogleSignInClient googleSignInClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    binding = FragmentFibonacciBinding.inflate(inflater, container, false);
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

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    getActivity().getMenuInflater().inflate(R.menu.logout_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.logOut:
        logout();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void instantiate() {
    binding.tvFibonacciSeries.setMovementMethod(new ScrollingMovementMethod());

    gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();
    googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

  }

  private void initialize() {

  }

  private void listen() {
    binding.btnCalculate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String firstNumber = binding.etFirstNumber.getText().toString();
        String secondNumber = binding.etSecondNumber.getText().toString();
        String range = binding.etRange.getText().toString();

        if (firstNumber.isEmpty()) {
          binding.etFirstNumber.setError("Please enter first digit!");
        } else if (secondNumber.isEmpty()) {
          binding.etSecondNumber.setError("Please enter second digit!");
        } else if (range.isEmpty()) {
          binding.etRange.setError("Please enter range!");
        } else {
          binding.tvFibonacciBanner.setVisibility(View.VISIBLE);
          calculateFibonacci(Integer.parseInt(firstNumber), Integer.parseInt(secondNumber), Integer.parseInt(range));
        }
      }
    });

  }

  private void load() {
  }

  private void calculateFibonacci(int first, int second, int range) {

    StringBuilder fibonacciSeries = new StringBuilder(first + " " + second);
    int temp;
    for (int i = 2; i < range; i++) {
      temp = first + second;
      fibonacciSeries.append(" ").append(temp);
      first = second;
      second = temp;
    }

    binding.tvFibonacciSeries.setText(fibonacciSeries.toString());

  }

  private void logout() {

    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        SharedPreferences authSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = authSharedPref.edit();
        editor.putString(Utils.STATUS, Utils.SIGNED_OUT).apply();

        startActivity(new Intent(getContext(), MainActivity.class));
      }
    });
  }
}