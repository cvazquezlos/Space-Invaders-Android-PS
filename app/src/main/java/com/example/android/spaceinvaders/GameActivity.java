package com.example.android.spaceinvaders;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    ImageView municion, nave, fondoJuego, enemigo, asteroide1, asteroide2;
    Button botonDisparo;
    int rotacion = 0;
    int iteracion = 0;
    RelativeLayout activity_main, tablero_enemigo, tablero_aliado;
    Handler manejaDisparo = new Handler(), manejaEnemigo = new Handler();
    final int movimiento = 30;
    final int movimientoEnemigo = 20;
    boolean inicioAFin = false;
    int ladeadoIzq, ladeadoDer, frontal, disparo;
    int ladeadoIzqEnemigo, ladeadoDerEnemigo, frontalEnemigo, disparoEnemigo, idEnemigo;
    TextView puntosVida;
    MediaPlayer sonidoDisparoNave;
    int puntuacion = 0;
    MediaPlayer musicaFondo;
    Boolean sonido, accionEnemigo;
    int puntosSaludJugador = 6;
    int saludObstaculo1 = 3, saludObstaculo2 = 3;
    ImageView[][] naves;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.game_activity);
        municion = (ImageView) findViewById(R.id.municion);
        nave = (ImageView) findViewById(R.id.nave);
        enemigo = (ImageView) findViewById(R.id.enemigo);
        fondoJuego = (ImageView) findViewById(R.id.fondo_juego);
        asteroide1 = (ImageView) findViewById(R.id.asteroide_1);
        asteroide2 = (ImageView) findViewById(R.id.asteroide_2);
        botonDisparo = (Button) findViewById(R.id.disparo);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        tablero_enemigo = (RelativeLayout) findViewById(R.id.tablero_enemigo);
        tablero_aliado = (RelativeLayout) findViewById(R.id.tablero_aliado);
        puntosVida = (TextView) findViewById(R.id.ptos_vida);
        Intent i = getIntent();
        if (i != null) {
            String data = i.getStringExtra("arg");
            introduceCambios(data);
            musicaFondo = MediaPlayer.create(this, R.raw.musicafondo);
            accionEnemigo=true;
            if (sonido)
                musicaFondo.start();
            if (iteracion==0) {
                manejaEnemigo.postDelayed(accionMovimiento, 0);
                lanzaEnemigos();
            }
        }
    }

    private void introduceCambios(String data) {
        String[] info = data.split(" ");
        int idFondo = getResources().getIdentifier(info[0], "drawable", getPackageName());
        fondoJuego.setImageResource(idFondo);
        int idNave = getResources().getIdentifier(info[1], "drawable", getPackageName());
        cambiosMovilidad(idNave);
        nave.setImageResource(frontal);
        int idEnemigo = getResources().getIdentifier(info[2], "drawable", getPackageName());
        cambiosMovilidadEnemigo(idEnemigo);
        System.out.println(idEnemigo);
        enemigo.setImageResource(frontalEnemigo);
        this.idEnemigo = idEnemigo;
        sonido = Boolean.valueOf(info[3]);
    }

    public void actualizaPosicion(View v) {
        switch (v.getId()) {
            case (R.id.control_derecha):
                if (!seSale("der", "CU")) {
                    nave.setImageResource(ladeadoIzq);
                    nave.setX(nave.getX() - movimiento);
                }
                break;
            case R.id.control_izquierda:
                if (!seSale("izq", "CU")) {
                    nave.setImageResource(ladeadoDer);
                    nave.setX(nave.getX() + movimiento);
                }
                break;
        }
    }

    private void cambiosMovilidadEnemigo(int idEnemigo) {
        switch (idEnemigo) {
            case 2130837601:
                frontalEnemigo = R.drawable.enemigodiseno21;
                break;
            case 2130837598:
                frontalEnemigo = R.drawable.enemigodiseno11;
                ladeadoIzqEnemigo = R.drawable.enemigodiseno12;
                ladeadoDerEnemigo = R.drawable.enemigodiseno13;
                break;
        }
    }

    private void cambiosMovilidad(int idNave) {
        switch (idNave) {
            case 2130837589:
                frontal = R.drawable.diseno11;
                ladeadoDer = R.drawable.diseno13;
                ladeadoIzq = R.drawable.diseno12;
                disparo = R.drawable.municion;
                sonidoDisparoNave = MediaPlayer.create(this, R.raw.disparonavezanahoria);
                break;
            case 2130837592:
                frontal = R.drawable.diseno21;
                ladeadoDer = R.drawable.diseno23;
                ladeadoIzq = R.drawable.diseno22;
                disparo = R.drawable.municion1;
                sonidoDisparoNave = MediaPlayer.create(this, R.raw.disparodragon);
                break;
            case 2130837595:
                frontal = R.drawable.diseno31;
                ladeadoDer = R.drawable.diseno33;
                ladeadoIzq = R.drawable.diseno32;
                disparo = R.drawable.municion2;
                sonidoDisparoNave = MediaPlayer.create(this, R.raw.disparonavenormal);
                break;
        }
    }

    public void dispara(View v) {
        if (sonido)
            sonidoDisparoNave.start();
        nave.setImageResource(frontal);
        municion.setImageResource(disparo);
        municion.setX(nave.getX() + (((nave.getWidth()) / 2) - 5));
        municion.setY(activity_main.getHeight() - nave.getHeight());
        municion.setVisibility(View.VISIBLE);
        botonDisparo.setEnabled(false);
        manejaDisparo.postDelayed(accionDisparo, 0);
    }

    Runnable accionDisparo = new Runnable() {
        @Override
        public void run() {
            municion.setY(municion.getY() - 50);
            if (llegaAlFinal()) {
                resetBala();
            }
            manejaDisparo.postDelayed(this, 80);
            if (colisionaConEnte(enemigo)) {
                reseteaNaveEnemiga();
                puntuacion += 20;
                actualizaPuntosVida();
                resetBala();
            } else if (colisionaConAsteroide(asteroide1) || colisionaConAsteroide(asteroide2)) {
                if (colisionaConAsteroide(asteroide1))
                    actualizaRecurso(asteroide1, saludObstaculo1 -= 1);
                else
                    actualizaRecurso(asteroide2, saludObstaculo2 -= 1);
            }
        }
    };

    Runnable accionMovimiento = new Runnable() {
        @Override
        public void run() {
            iteracion++;
            if (inicioAFin) {
                if (idEnemigo == 2130837601) {
                    rotacion += 20;
                    enemigo.setRotation(rotacion);
                } else
                    enemigo.setImageResource(ladeadoIzqEnemigo);
                enemigo.setX(enemigo.getX() + movimientoEnemigo);
            } else {
                if (idEnemigo == 2130837601) {
                    rotacion -= 20;
                    enemigo.setRotation(rotacion);
                } else
                    enemigo.setImageResource(ladeadoDerEnemigo);
                enemigo.setX(enemigo.getX() - movimientoEnemigo);
            }
            if (seSale("izq", "IA") || seSale("der", "IA")) {
                rotacion = 0;
                enemigo.setY(enemigo.getY() + 70);
                inicioAFin = !inicioAFin;
            }
            if (invadeMitad()) {
                reseteaNaveEnemiga();
                puntosSaludJugador--;
                actualizaSalud();
                actualizaPuntosVida();
            }
            if (accionEnemigo)
                manejaEnemigo.postDelayed(this, 80);
        }
    };

    private boolean llegaAlFinal() {
        return (municion.getY() <= 20);
    }

    private boolean seSale(String direccion, String jugador) {
        switch (direccion) {
            case "izq":
                switch (jugador) {
                    case "CU":
                        return (nave.getX() + movimiento + nave.getWidth()) > tablero_aliado.getWidth();
                    case "IA":
                        return (enemigo.getX() + movimiento + enemigo.getWidth()) > tablero_enemigo.getWidth();
                }
            case "der":
                switch (jugador) {
                    case "CU":
                        return (nave.getX() - movimiento) < 0;
                    case "IA":
                        return (enemigo.getX() - movimiento) < 0;
                }
        }
        return true;
    }

    private boolean invadeMitad() {
        return ((enemigo.getY() + enemigo.getHeight()) >= tablero_enemigo.getHeight());
    }

    private boolean colisionaConEnte(ImageView view) {
        return estaEnRegionX(view) && estaEnRegionY(view);
    }

    private boolean estaEnRegionX(ImageView view) {
        return (municion.getX() > view.getX()) && (municion.getX() < (view.getX() + view.getWidth()));
    }

    private boolean estaEnRegionY(ImageView view) {
        return (municion.getY() > view.getY() && (municion.getY() < (view.getY() + view.getHeight())));
    }

    private boolean colisionaConAsteroide(ImageView view) {
        return estaEnRegionX(view) && asteroideRegionY(view);
    }

    private boolean asteroideRegionY(ImageView view) {
        return (municion.getY() > (view.getY() + tablero_aliado.getHeight()) && (municion.getY() < ((view.getY() + tablero_aliado.getHeight()) + view.getHeight())));
    }

    private void actualizaRecurso(ImageView view, int salud) {
        if (salud >= 0)
            switch (salud) {
                case 2:
                    view.setImageResource(R.drawable.zobjectasteroiefase2);
                    resetBala();
                    break;
                case 1:
                    view.setImageResource(R.drawable.zobjectasteroiefase3);
                    resetBala();
                    break;
                case 0:
                    view.setVisibility(View.GONE);
                    break;
            }
    }

    private void resetBala() {
        municion.setVisibility(View.INVISIBLE);
        manejaDisparo.removeCallbacks(accionDisparo);
        botonDisparo.setEnabled(true);
    }

    private void reseteaNaveEnemiga() {
        enemigo.setY(0);
        enemigo.setX((tablero_enemigo.getWidth() / 2) - (enemigo.getWidth() / 2));
    }

    private void actualizaPuntosVida() {
        try {
            puntosVida.setText(Integer.toString(puntuacion));
        } catch (Exception e) {
        }
    }

    private void actualizaSalud() {
        int idAEsconder = 2131492962 - puntosSaludJugador;
        findViewById(idAEsconder).setVisibility(View.INVISIBLE);
        if (puntosSaludJugador == 0) {
            accionEnemigo=false;
            manejaEnemigo.removeCallbacks(accionMovimiento);
            try {
                accionMovimiento.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            botonDisparo.setEnabled(false);
            findViewById(R.id.control_derecha).setEnabled(false);
            findViewById(R.id.control_izquierda).setEnabled(false);
            ((TextView) (findViewById(R.id.puntuacion_final))).setText("Puntuación: "+puntuacion);
            findViewById(R.id.pantalla_game_over).setVisibility(View.VISIBLE);
        }
    }

    public void volverMenuPrincipal(View v) {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (musicaFondo != null) {
            musicaFondo.pause();
            if (isFinishing()) {
                musicaFondo.stop();
                musicaFondo.release();
            }
        }
    }

    private void lanzaEnemigos() {
        naves = new ImageView[2][3];
    }
}
