package edu.cmu.pocketsphinx.demo;
/**
 * Created by Steven ,Angelo, Kihaen, Nayeem
 * Delta Point
 */

import static junit.framework.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioRecord;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;

public class PracticeActivity extends Activity implements AdapterView.OnItemSelectedListener {
    protected static final String TAG = SpeechRecognizer.class.getSimpleName();
    protected static Decoder d;
    protected static File assetsDir;
    Button myButton;
    Button audioButton;
    Spinner spinner;
    private String selectedFile;
    Thread recordingThread;

    String outputFileString = null;
    String wavOutputFileString = null;
    private AudioRecord recorder = null;
    private static String fileName = null;
    private static int fileCounter = 0;
    private static File outputFile;
    File wavOutputFile = null;
    private static byte[] buffer;
    int bufferSize;
    private static boolean recording = false;

    @Override
    public void onCreate(Bundle state) {
        //Please forgive the mess of an onCreate function this turned out to be
        //First attempt at initializing the main view
        super.onCreate(state);
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Setting up the recognizer, one moment...");

        //Initial setup of the PocketSphinx Decoder for use within the application
        setupTask();

        //file path to the directory in which the recordings will be stored
        fileName = "/storage/emulated/0/Android/data/edu.cmu.sphinx.pocketsphinx/files/sync"+File.separator;

        //Add spinner to the main View to allow user feature slection
        spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.option_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //adds an action listener to the spinner to handle user inteaction
        spinner.setOnItemSelectedListener(this);

        ////disables button when app boots up
        findViewById(R.id.button_send).setEnabled(false);
        myButton = (Button) findViewById(R.id.button_send);
        //add click listener to handle user interaction
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButton("Processing File");
                //passes the user selectied file into text extraction method
                convertFileToSpeech(v, d, assetsDir);
            }
        });

        //recoding button is disabled when app is booted up
        findViewById(R.id.audio_button).setEnabled(false);
        audioButton = (Button) findViewById(R.id.audio_button);
        audioButton.setVisibility(View.INVISIBLE);
        //add event listener to handle user interaction
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Begins process of recording
                if (!recording) {
                    switch (v.getId()) { // erics code ----------------
                        case R.id.audio_button: {
                            Intent Intent = new Intent(PracticeActivity.this, soundrecording.SoundRecording.class);
                            startActivity(Intent);
                            //RecognitionActivity.this.finish();
                        }
                    }
                    //audioRecord(v);
                    CharSequence str = "Start Recording"; // originally End Recording adapted for Sippa implementation
                    ((Button) findViewById(R.id.audio_button)).setText(str);
                }
                //ends the recording process
                /*else {
                    switch (v.getId()) { // erics code -----------------
                        case R.id.audio_button: {
                            Intent Intent = new Intent(PracticeActivity.this, soundrecording.SoundRecording.class);
                            startActivity(Intent);
                            //RecognitionActivity.this.finish();
                        }
                    }
                    //audioRecord(v);
                    CharSequence str = "Start Recording";
                    ((Button) findViewById(R.id.audio_button)).setText(str);
                }
                */
            }
        });
    }

    //convert an inputstream to text
    static {
        System.loadLibrary("pocketsphinx_jni");
    }

   /* private void audioRecord(View view) {
        final View v = view;

        //final AudioRecord recorder;
        if (!recording) {
            //Get unique timestamp for file naming
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            String uniqueFileID = formatter.format(now);

            //Give the matching raw and wav file the same unique ID, just with different file types.
            outputFileString = String.format("%s%s.raw", fileName, uniqueFileID);
            wavOutputFileString = String.format("%s%s.wav", fileName, uniqueFileID);
            Log.i(TAG, outputFileString);
            Log.d(TAG, wavOutputFileString);

            outputFile = new File(outputFileString);
            int sampleRate = (int) this.d.getConfig().getFloat("-samprate");
            bufferSize = Math.round((float) sampleRate * 0.4F);
            recorder = new AudioRecord(6, sampleRate, 16, 2, bufferSize * 2);
            if (recorder.getState() == 0)
                this.recorder.release();

            buffer = new byte[bufferSize];
        }
        //Thread is created to check whether user is recording or not and starts/ends the recording
        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                //if user is NOT recording, begin recording and write audio buffer to the output file
                if (!recording) {
                    recording = true;
                    //Signals recorder object to open mic and begin recoding
                    recorder.startRecording();
                    FileOutputStream fos = null;
                    try {
                        //opens output file
                        fos = new FileOutputStream(outputFile.getPath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    int read = 0;
                    //will continue to write audio buffer to file until recoding is ended by user
                    while (recording) {
                        read = recorder.read(buffer, 0, buffer.length);
                        Log.i(TAG, "FINISHED BUFFER");
                        try {
                            assert fos != null;
                            //once buffer is full write it to the output file
                            fos.write(buffer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //close output file
                    try {
                        assert fos != null;
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //once done the thread is block and waits to be terminated by parent process
                    try {
                        recordingThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //if the user IS recording, stop recording close output file and convert raw file
                //into a file of .wav format
                else {
                    recording = false;
                    //signals recorder to end recording and recorder object is released
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    if (wavOutputFileString != null) {
                        wavOutputFile = new File(wavOutputFileString);
                        //convert resulting .raw file into .wav format of the same name
                        rawToWav(outputFile.getAbsolutePath(), wavOutputFile.getAbsolutePath());
                        Log.i(TAG, wavOutputFile.getAbsolutePath());
                        //final .wav file is passed into convertToSpeech wher the text will be extracted
                        convertVoiceToSpeech(v, d, assetsDir);
                        buffer = null;
                    }
                    //once done the thread is block and waits to be terminated by parent process
                    try {
                        recordingThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "Recorder Thread");

        //thread is started
        recordingThread.start();

    } */

    private void setupTask() {
        //Setup is done as an asynchronous task to allow user to pick recoding/file option as the
        //decoder is being setup
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Assets assets = null;
                try {
                    assets = new Assets(PracticeActivity.this);
                    assetsDir = assets.syncAssets();
                    Config c = Decoder.defaultConfig();
                    c.setString("-hmm", new File(assetsDir, "en-us-ptm").getPath());
                    c.setString("-dict", new File(assetsDir, "digits.dict").getPath());
                    //c.setString("-dict", new File(assetsDir, "cmudict-en-us.dict").getPath());
                    c.setBoolean("-allphone_ci", true);
                    c.setFloat("-kws_threshold", 1e-45f);
                    c.setString("-lm", new File(assetsDir, "en-us.lm.dmp").getPath());
                    d = new Decoder(c);
                    Log.i(TAG, "Done.....");
                    return "Recognizer Setup Complete.";

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            //once setup is complete inform the user
            protected void onPostExecute(String result) {
                updateView(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //changes the audio button to display appropriate text
    private void updateView(String phrase) {
        ((TextView) findViewById(R.id.caption_text))
                .setText(phrase);
        findViewById(R.id.audio_button).setEnabled(true);
    }

    //disables main button in home view
    private void changeButton(String action) {
        ((Button) findViewById(R.id.button_send)).setText(action);
        findViewById(R.id.button_send).setEnabled(false);
    }

    //Method that takes a recorded voice sample and parses it into text. It should be noted that
    //this is different than live speech processing. A voice sample is recorded in the app, and then saved to a file.
    //That previously saved file is what gets processed in this method.
    private void convertVoiceToSpeech(View v, final Decoder d, final File assetsDir) {
        //We create a new asynchronous task to asynchronously process speach (lessens load on main thread)
        new AsyncTask<Void, Void, String>() {

            //The process of actually parsing the text from the voice file is done in background, as to
            //not significantly slow down the app
            @Override
            protected String doInBackground(Void... params) {

                String output = null; //Our result string.
                Log.i(TAG, assetsDir.getAbsolutePath()); //For the purposes of debugging
                InputStream stream = null; //Input stream that will be used to get contents out of recorded file

                try {
                    stream = new FileInputStream(wavOutputFile); //We take the stream from our wavOutputFile
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                d.startUtt(); //Tells Decoder to start the voice processing

                byte[] buf = new byte[4096]; //A buffer that will be used to hold chunks of our input file

                try {
                    int nbytes; //The number of bytes read into the buffer

                    assert stream != null; //Ensure that stream isn't null

                    //Reading process for the input file. Fills buffer, processes buffer and repeats
                    while ((nbytes = stream.read(buf)) >= 0) {

                        ByteBuffer bb = ByteBuffer.wrap(buf, 0, nbytes); //Instantiates a ByteBuffer, used to change byte order and create short array

                        bb.order(ByteOrder.LITTLE_ENDIAN); //Orders the bytes using Little endian notation (this is required on android)
                        short[] s = new short[nbytes / 2]; //Creates a new, empty short array
                        bb.asShortBuffer().get(s); //Takes the now Little endian sorted bytes and puts them in our recently created short array

                        d.processRaw(s, nbytes / 2, false, false); //Call the decoder to process out short array

                    }

                } catch (IOException e) {
                    fail("Error when reading inputstream" + e.getMessage());
                }

                d.endUtt(); //Tells Decoder to end the voice processing

                String text = d.hyp().getHypstr(); //Gets the hypothesis(The decoder's guess at the utterance from the voice file)
                Log.i(TAG, d.hyp().getHypstr()); //For debugging purposes
                return text; //Returns our result string.
            }

            //This will be executed after the thread finishes executing. Takes the return of doInBackground as parameter
            protected void onPostExecute(String result) {
                //If a result was obtained, adjust caption, and report the result string.
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Extracted Text:");
                    ((TextView) findViewById(R.id.result_text)).setText(result);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //method that tales a pre-recorded voice file and extracts the text. This file format must
    //be in .wav format.
    private void convertFileToSpeech(View v, final Decoder d, final File assetsDir) {

        //Parsing the file is done asynchronously and delegated to a new thread to keep the load
        // on the main thread minimal and speed up the extraction process.
        new AsyncTask<Void, Void, String>() {

            @Override
            //This task will be performed in the background
            protected String doInBackground(Void... params) {
                String output = null;
                try {

                    //user selected file is added to to an inputStream from which it will be read.
                    File wavFile = new File(assetsDir, selectedFile);
                    InputStream stream = new FileInputStream(wavFile);
                    /////////////////////////////////////////////////
                    d.startUtt();//Decoder set to start

                    //A buffer that will be used to hold chunks of our input file stream
                    byte[] b = new byte[4096];
                    try {
                        int nbytes;
                        while ((nbytes = stream.read(b)) >= 0) {
                            ByteBuffer bb = ByteBuffer.wrap(b, 0, nbytes);
                            // Not needed on desktop but required on android
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            short[] s = new short[nbytes / 2];
                            bb.asShortBuffer().get(s);
                            d.processRaw(s, nbytes / 2, false, false);
                        }
                    } catch (IOException e) {
                        fail("Error when reading inputstream" + e.getMessage());
                    }
                    d.endUtt();//Decoder ends processing the input stream
                    ///////////////////////////////////////////////////////
                    //Hypothesis string is the text that the decoder extracted from the file
                    String text = d.hyp().getHypstr();
                    Log.i(TAG, text);
                    return d.hyp().getHypstr();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            //Task performed after execution on main activity.
            //Method updated the View to display the resulting string from the file.
            protected void onPostExecute(String result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Extracted Text:");
                    ((TextView) findViewById(R.id.result_text)).setText(result);
                    findViewById(R.id.button_send).setEnabled(true);
                    String str1 = "Process File";
                    ((Button) findViewById(R.id.button_send)).setText(str1);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    //Method handles user interaction witht the spinner and adapts the View accordingly with
    //the selected feature.
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //if "Record Your Voice" is selected show only the rcord button
        if (position == 1) {
            ((TextView) findViewById(R.id.result_text)).setText("");
            Log.i(TAG, "Option 0");
            audioButton.setVisibility(View.VISIBLE);
            findViewById(R.id.button_send).setVisibility(View.INVISIBLE);
        }
        //if "Select file" option is chosen open alert window for user to select file from the sync
        //directory within the application
        else if (position == 2) {
            //Processing button is made visible
            findViewById(R.id.button_send).setEnabled(true);
            findViewById(R.id.button_send).setVisibility(View.VISIBLE);
            audioButton.setVisibility(View.INVISIBLE);
            Log.i(TAG, "Option 1");
            String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/edu.cmu.sphinx.pocketsphinx/files/sync";
            Log.d("Files", "Path: " + path);
            File directory = new File(path);
            //Populates array with all files in the directory
            File[] files = directory.listFiles();
            CharSequence[] temp = new CharSequence[files.length];
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                temp[i] = files[i].getName();
                Log.d("Files", "FileName:" + files[i].getName());
            }
            final CharSequence[] items = temp;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //set alert window title
            builder.setTitle("Pick your .wav file:").setItems(items, new DialogInterface.OnClickListener() {
                //add listener to allow user to slect a file within the alert window and assign it to
                //the selected File variable which will be passed into the fileToSpeech method.
                public void onClick(DialogInterface dialog, int item) {
                    selectedFile = items[item].toString();
                    Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                }
            });
            //Displays all files in the directory in the popup alert window
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //Method facilitates the conversion from raw to wav. This conversion is done, by initiating an output file with
    //the required (as defined by pocketsphinx - Mono sound, 16khz sample rate, etc.) specifications. This leaves us with
    //a file with just a wav header. After that, the contents of the raw file are copied over to the initiated wav file,
    //giving us a complete wav file, with header and data.
    private void rawToWav(String inFilename, String outFilename) {
        FileInputStream in = null; //The input stream for the raw file
        FileOutputStream out = null; //The output stream for the wav file
        long audioLength = 0; //Initialization of our audio length (In reference to WAV format: Subchunk2Size)
        long dataLength = audioLength + 36; //Initialization of our data length (In reference to WAV format: SubchunkSize)
        long longSampleRate = 16000; //Our Sample rate, PocketSphinx requires 16khz for accuracy
        int numChannels = 1; //The number of channels used, PocketSphinx requires mono-sound
        long byteRate = 16 * 16000 * numChannels / 8; //Initialization of our byteRate (For byteRate in WAV header)

        //Creates new buffer that will be used to transfer data from raw file to wav file
        byte[] data = new byte[bufferSize];
        try {
            in = new FileInputStream(inFilename); //Assign our raw Inputstream to the inputStream prev. declared
            out = new FileOutputStream(outFilename); //Assign our wav Outputstream to the outputStream prev. declared
            audioLength = in.getChannel().size(); //Obtains the audio length(basically the size of the raw file)
            dataLength = audioLength + 36; //Obtains what will be the chunksize of the wav file (raw length + length of wav header)

            //Calls method that writes the header of a wav file with our previously defined wav header specifications
            WriteWaveFileHeader(out, audioLength, dataLength,
                    longSampleRate, numChannels, byteRate);
            //Note, after this method call, our output file now has the wav header with nothing else

            //Writes the data of the raw file to the wav file, creating a complete wav file (header + data)
            while (in.read(data) != -1) {
                out.write(data);
            }

            //Close streams
            in.close();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method writes a wav header to a file. This is used to iniitate our wav file, before filling it up with
    //data from the raw file. The specifications for the header are obtained through the method parameters.
    private void WriteWaveFileHeader(
            FileOutputStream out, long audioLength,
            long dataLength, long longSampleRate, int channels,
            long byteRate) throws IOException {

        //The byte array that will temporarily hold the header
        byte[] header = new byte[44];

        header[0] = 'R'; //ChunkID
        header[1] = 'I'; //ChunkID
        header[2] = 'F'; //ChunkID
        header[3] = 'F'; //ChunkID
        header[4] = (byte) (dataLength & 0xff); //ChunkSize
        header[5] = (byte) ((dataLength >> 8) & 0xff); //ChunkSize
        header[6] = (byte) ((dataLength >> 16) & 0xff); //ChunkSize
        header[7] = (byte) ((dataLength >> 24) & 0xff); //ChunkSize
        header[8] = 'W'; //Format
        header[9] = 'A'; //Format
        header[10] = 'V'; //Format
        header[11] = 'E'; //Format
        header[12] = 'f'; //Subchunk1ID
        header[13] = 'm'; //Subchunk1ID
        header[14] = 't'; //Subchunk1ID
        header[15] = ' '; //Subchunk1ID
        header[16] = 16; //Subchunk1Size
        header[17] = 0; //Subchunk1Size
        header[18] = 0; //Subchunk1Size
        header[19] = 0; //Subchunk1Size
        header[20] = 1; //AudioFormat
        header[21] = 0; ////AudioFormat
        header[22] = (byte) channels; //NumChannels
        header[23] = 0; //NumChannels
        header[24] = (byte) (longSampleRate & 0xff); //SampleRate
        header[25] = (byte) ((longSampleRate >> 8) & 0xff); //SampleRate
        header[26] = (byte) ((longSampleRate >> 16) & 0xff); //SampleRate
        header[27] = (byte) ((longSampleRate >> 24) & 0xff); //SampleRate
        header[28] = (byte) (byteRate & 0xff); //ByteRate
        header[29] = (byte) ((byteRate >> 8) & 0xff); //ByteRate
        header[30] = (byte) ((byteRate >> 16) & 0xff); //ByteRate
        header[31] = (byte) ((byteRate >> 24) & 0xff); //ByteRate
        header[32] = (byte) (2 * 16 / 8); //BlockAlign
        header[33] = 0; //BlockAlign
        header[34] = 16; //BitsPerSamples
        header[35] = 0; //BitsPerSamples
        header[36] = 'd'; //Subchunk2ID
        header[37] = 'a'; //Subchunk2ID
        header[38] = 't'; //Subchunk2ID
        header[39] = 'a'; //Subchunk2ID
        header[40] = (byte) (audioLength & 0xff); //Subchunk2Size
        header[41] = (byte) ((audioLength >> 8) & 0xff); //Subchunk2Size
        header[42] = (byte) ((audioLength >> 16) & 0xff); //Subchunk2Size
        header[43] = (byte) ((audioLength >> 24) & 0xff); //Subchunk2Size

        //Writes our recently created header to the output file.
        out.write(header, 0, 44);
    }
}
