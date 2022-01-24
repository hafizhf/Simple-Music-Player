/*------------------------------------------------------------.
/                   Music Player (Kuis 1)                     /
'------------------------------------------------------------/

 >>> Nama 	        : Hafizh Firdaus Yuspriana
 >>> NIM            : 1905092
 >>> Kelas          : 5B
 >>> Mata Kuliah    : Pemrograman Mobile
 >>> Dosen Pengampu	: - Deden Pradeka, S.T., M.Kom.
                      - Muhammad Taufik, S.Tr.Kom., M.T.I.

/-------------------------------------------------------------.
/                   --------------------                      /
'-----------------------------------------------------------*/

package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button playpause, stop, next, prev;                     // Deklarasi tombol
    MediaPlayer mp;                                         // Deklarasi Media Player
    boolean play = true;                                    // Play
    SeekBar seekBar;                                        // Seekbar durasi lagu
    int currentIndex = 0;                                   // Untuk indexing posisi lagu

    Runnable runnable;                                      // Untuk objek yang berjalan setiap waktu
    Handler handler;                                        // Untuk handler runnable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get id dari layout ----------------------------------------------------------------------

        TextView artistDisplay = findViewById(R.id.artist);
        TextView titleDisplay = findViewById(R.id.title);
        playpause = (Button)findViewById(R.id.idplay);
        stop = (Button)findViewById(R.id.idstop);
        next = (Button)findViewById(R.id.idnext);
        prev = (Button)findViewById(R.id.idprev);
        seekBar = findViewById(R.id.seekbar);

        handler = new Handler();

        // ArrayList daftar lagu -------------------------------------------------------------------

        ArrayList<Integer> songs = new ArrayList<>();

        songs.add(0, R.raw.prologue);
        songs.add(1, R.raw.a_town_with_an_ocean_view_kiki_delivery_service);
        songs.add(2, R.raw.flower_dance);
        songs.add(3, R.raw.tsundere_labs_inc_tsundere_jazz);
        songs.add(4, R.raw.epilogue);

        // ArrayList Artist ------------------------------------------------------------------------

        ArrayList<String> artist = new ArrayList<>();

        artist.add(0, "Yoasobi");
        artist.add(1, "Joe Hisashi");
        artist.add(2, "DJ Okari");
        artist.add(3, "Tsundere Labs, Inc.");
        artist.add(4, "Yoasobi");

        // Arraylist Title -------------------------------------------------------------------------

        ArrayList<String> title = new ArrayList<>();

        title.add(0, "Prologue - The Book");
        title.add(1, "A Town with An Ocean View");
        title.add(2, "Flower Dance");
        title.add(3, "Tsundere Jazz");
        title.add(4, "Epilogue - The Book");

        // Memulai lagu berdasarkan index (Default = 0) --------------------------------------------

        mp = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
        artistDisplay.setText(artist.get(currentIndex));
        titleDisplay.setText(title.get(currentIndex));

        int duration = mp.getDuration();
        int currentDuration = mp.getCurrentPosition();

        // Progress Seekbar bisa digeser -----------------------------------------------------------

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mp.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Action tombol play-pause ----------------------------------------------------------------

        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play == true) {
                   mp.start();
                   playpause.setText("||");
                   play = false;
                } else {
                    mp.pause();
                    playpause.setText("▶");
                    play = true;
                }
            }
        });

        // Action tombol stop ----------------------------------------------------------------------

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mp.stop();
                    play = true;
                    mp.prepare();
                    mp.seekTo(0);
                    playpause.setText("▶");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Action tombol next ----------------------------------------------------------------------

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex < songs.size() - 1) {
                    currentIndex++;

                    if (mp.isPlaying()) {
                        mp.stop();
                    }

                    mp = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
                    artistDisplay.setText(artist.get(currentIndex));
                    titleDisplay.setText(title.get(currentIndex));

                    playpause.setText("||");
                    mp.start();

                }  else {
                    Toast.makeText(getApplicationContext(),"You're at the end of queue",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Action tombol prev ----------------------------------------------------------------------

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex > 0) {
                    currentIndex--;

                    if (mp.isPlaying()) {
                        mp.stop();
                    }

                    mp = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
                    artistDisplay.setText(artist.get(currentIndex));
                    titleDisplay.setText(title.get(currentIndex));

                    playpause.setText("||");
                    mp.start();

                } else {
                    Toast.makeText(getApplicationContext(),"You're at the first of queue",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Fungsi untuk Seekbar supaya panjangnya sesuai durasi dan progressnya berjalan -----------

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                updateSeekbar();
                updateDuration(songs, artist, title);
            }
        });
    }

    // Method yang digunakan -----------------------------------------------------------------------

    // Untuk Seekbar supaya terus ter-update -------------------------------------------------------

    private void updateSeekbar() {
        int currPos = mp.getCurrentPosition();
        seekBar.setMax(mp.getDuration());
        seekBar.setProgress(currPos);

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable, 10);
    }

    // Untuk teks waktu durasi ---------------------------------------------------------------------

    private void updateDuration(ArrayList<Integer> songs, ArrayList<String> artist, ArrayList<String>title) {

        // Durasi Lagu -----------------------------------------------------------------------------

        TextView readDuration = findViewById(R.id.durasi);

        int duration = mp.getDuration();

        String durasi = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );

        readDuration.setText(durasi);


        // Durasi Sekarang (Current) ---------------------------------------------------------------

        TextView readCurrentDuration = findViewById(R.id.durasiSekarang);

        int currentDuration = mp.getCurrentPosition();

        String durasiSekarang = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentDuration),
                TimeUnit.MILLISECONDS.toSeconds(currentDuration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentDuration))
        );

        readCurrentDuration.setText(durasiSekarang);

        // Otomatis next ketika lagu selesai -------------------------------------------------------

        TextView artistDisplay = findViewById(R.id.artist);
        TextView titleDisplay = findViewById(R.id.title);

        if (currentDuration + 10 > duration) {
            if (currentIndex < songs.size() - 1) {
                currentIndex++;

                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
                artistDisplay.setText(artist.get(currentIndex));
                titleDisplay.setText(title.get(currentIndex));
                mp.start();

            } else {
                mp.stop();
                mp.seekTo(0);
                playpause.setText("▶");
            }
        }

        // Supaya bisa berjalan terus --------------------------------------------------------------

        runnable = new Runnable() {
            @Override
            public void run() {
                updateDuration(songs, artist, title);
            }
        };
        handler.postDelayed(runnable, 10);

    }
}