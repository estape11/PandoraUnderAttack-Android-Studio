package com.example.adrian.pandoraunderattack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView nombre;
    private EditText cuadronombre;
    private Button boton;
    private TextView resp;
    private TextView clave;
    private EditText textclave;
    private Button registrar;
    Socket sockete = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;
    Gson gson=new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre = (TextView) findViewById(R.id.lblnombre);
        cuadronombre = (EditText) findViewById(R.id.txtnombre);
        boton = (Button) findViewById(R.id.btnenviar);
        clave = (TextView) findViewById(R.id.lblclave);
        textclave = (EditText) findViewById(R.id.txtclave);
        registrar = (Button) findViewById(R.id.btnregistrarse);

        Thread principal = new Thread(new Runnable() {
            public void run() {
                try {
                    sockete = new Socket("172.26.34.133", 8080);
                    leer();
                    escribir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        principal.start();
        findViewById(R.id.btnregistrarse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registrar.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void leer(){
        Thread leer_hilo=new Thread(new Runnable(){
            public void run(){
                try{
                    lector=new BufferedReader(new InputStreamReader(sockete.getInputStream()));
                    while(true){

                        JsonParser parser = new JsonParser();
                        String mensaje= lector.readLine();
                        JsonElement elemento = parser.parse(mensaje);
                        String mensaje_in=elemento.getAsJsonObject().get("mensaje").getAsString();

                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }

        });
        leer_hilo.start();
    }
    public void escribir(){
        Thread escribir_hilo=new Thread(new Runnable(){
            public void run(){
                try{
                    escritor= new PrintWriter(sockete.getOutputStream(), true);
                    boton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JsonObject o = new JsonObject();
                            o.addProperty("nombre", String.valueOf(cuadronombre.getText()));
                            String enviar_mensaje = gson.toJson(o);
                            escritor.println(enviar_mensaje);
                            System.out.println(enviar_mensaje);
                            cuadronombre.setText("");
                        }

                    });
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        escribir_hilo.start();
    }

}