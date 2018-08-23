package com.moron.arely.control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

// Elaborado por Arely M.

public class control extends AppCompatActivity
{
    private Button ON;
    private Button OFF;
    private FloatingActionButton Volver;
    private TextView Led;
    private TextView Aviso;
    private SeekBar ServoControl;
    private static Bluetooth bluetooth;
    private static BluetoothDevice Dispositivo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ON = findViewById(R.id.btn_on);
        OFF = findViewById(R.id.btn_off);
        Volver = findViewById(R.id.btn_volver);
        Aviso = findViewById(R.id.tv_aviso);
        Led = findViewById(R.id.tv_led);
        ServoControl = findViewById(R.id.sb_servo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Iniciar();
        Intent intent = getIntent();
        bluetooth = new Bluetooth();
        Dispositivo = bluetooth.getSocket().getRemoteDevice(); // obtengo el dispositivo al que se conecto
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Conectar(Dispositivo); // me vuelvo a conectar al dispositivo cuando el usuario vuelve a entrar a la app
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        bluetooth.Desconectar(); // me desconecto del dispositivo cuando el usuario sale de la app
        unregisterReceiver(broadcastReceiver); // cancelo el Broadcast Receiver
    }

    private void Iniciar() // inicio los componentes que voy a utilizar (eventos, atributos etc..)
    {
        Led.setText("Led apagado");

        ON.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Enviar_onClick(2);
            }
        });

        OFF.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Enviar_onClick(1);
            }
        });

        Volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Volver_onClick();
            }
        });

        ServoControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                Enviar_onClick(progress + 5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    private void Enviar_onClick(int dato)
    {
        if(dato == 1)
            Led.setText("Led apagado");
        else if(dato == 2)
            Led.setText("Led encendido");
        bluetooth.Enviar(dato);
    }

    private void Volver_onClick() // vuelvo al activity anterior
    {
        bluetooth.Desconectar();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish(); // finalizo esta activity con el fin de que el usuario no pueda volver a ella presionando el boton atras
    }

    private void Conectar(final BluetoothDevice dispositivo) // en este metodo me reconecto al dispositivo
    {
        if(bluetooth.getSocket().isConnected()) // si ya esta conectado solo muestro a que dispositivo se esta conectado
        {

            Aviso.setText("Conectado a " + dispositivo.getName());
        }

        else
        {
            Aviso.setText("Reconectando con " + dispositivo.getName());
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(bluetooth.Conectar(dispositivo))
                    {
                        Aviso.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Aviso.setText("Conectado a " + dispositivo.getName());
                            }
                        });
                    }

                    else
                    {
                        Aviso.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Aviso.setText("No se pudo reconectar con " + dispositivo.getName());
                            }
                        });
                    }
                }
            }).start();

        }
    }

    /*
    creo que no seria buena practica usar 2  Broadcast Receiver pero lo hice asi porque en los eventos uso algunos atributos
    de la clase o el activity en el que estoy. No estoy segura pero seria mejor usar el  Broadcast Receiver en una clase
    aparte y usar solo 1  Broadcast Receiver para todos los activity.

    el  Broadcast Receiver tambien se puede registrar en el android manifest en vez de hacerlo en tiempo real por asi decirlo que
    fue como lo hize aqui usando los eventos onStart y onStop.
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            switch (action)
            {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if(state == BluetoothAdapter.STATE_ON)
                    {
                        Conectar(Dispositivo);
                    }
                    if(state == BluetoothAdapter.STATE_OFF)
                    {
                        bluetooth.Desconectar();
                        Toast.makeText(context, "Debe mantener el bluetooth encendido", Toast.LENGTH_LONG).show();
                        Aviso.setText("Desconectado");
                    }
                    break;
                }

                case BluetoothDevice.ACTION_ACL_DISCONNECTED: // este evento se produce cuando se desconecta del dispositivo
                {
                    Toast.makeText(context, "Se desconecto el dispositivo", Toast.LENGTH_LONG).show();
                    Aviso.setText("Desconectado");
                    break;
                }
            }
        }
    };
}
