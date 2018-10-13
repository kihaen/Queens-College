package soundrecording;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.cmu.pocketsphinx.demo.R;

public class SoundRecording extends Activity {
        private static final int RECORDER_BPP = 16;
        private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
        private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
        private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
        //private static final int RECORDER_SAMPLERATE = 44100;
        //private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
        //private static final int RECORDER_SAMPLERATE = 8000;
        private static final int RECORDER_SAMPLERATE = 16000;
        private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
        private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
         
        private AudioRecord recorder = null;
        private int bufferSize = 0;
        private Thread recordingThread = null;
        private boolean isRecording = false;
         
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sound_recording);
         
        setButtonHandlers();
        enableButtons(false);
         
        bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }
 
        private void setButtonHandlers() {
                ((Button)findViewById(R.id.btnStart)).setOnClickListener(btnClick);
        ((Button)findViewById(R.id.btnStop)).setOnClickListener(btnClick);
        }
         
        private void enableButton(int id,boolean isEnable){
                ((Button)findViewById(id)).setEnabled(isEnable);
        }
         
        private void enableButtons(boolean isRecording) {
                enableButton(R.id.btnStart,!isRecording);
                enableButton(R.id.btnStop,isRecording);
        }
 
        private String setup_audio_signature(){
        	String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath(); // here is where it saves it.
            File audio_file = new File(baseDir+ "/Android/data/edu.cmu.sphinx.pocketsphinx/files/sync", "audio_signature.wav"); //saving directory changed
             
            return (audio_file.toString());
        }
        
        
        private String getFilename(){
                String filepath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filepath,AUDIO_RECORDER_FOLDER);
                 
                if(!file.exists()){
                        file.mkdirs();
                }
                 
                return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
        }
         
        private String getTempFilename(){
                String filepath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filepath,AUDIO_RECORDER_FOLDER);
                 
                if(!file.exists()){
                        file.mkdirs();
                }
                 
                File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
                 
                if(tempFile.exists())
                        tempFile.delete();
                 
                return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
        }
         
        private void startRecording(){
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
                 
                int i = recorder.getState();
                if(i==1)
                  recorder.startRecording();
                 
                isRecording = true;
                 
                recordingThread = new Thread(new Runnable() {
                         
                        @Override
                        public void run() {
                                writeAudioDataToFile();
                        }
                },"AudioRecorder Thread");
                 
                recordingThread.start();
        }
         
        private void writeAudioDataToFile(){
                byte data[] = new byte[bufferSize];
                String filename = getTempFilename();
                FileOutputStream os = null;
                 
                try {
                        os = new FileOutputStream(filename);
                } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                 
                int read = 0;
                 
                if(null != os){
                        while(isRecording){
                                read = recorder.read(data, 0, bufferSize);
                                 
                                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                                        try {
                                                os.write(data);
                                        } catch (IOException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                         
                        try {
                                os.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
         
        private void stopRecording(){
                if(null != recorder){
                        isRecording = false;
                         
                        int i = recorder.getState();
                        if(i==1)
                          recorder.stop();
                        recorder.release();
                         
                        recorder = null;
                        recordingThread = null;
                }
                 
                copyWaveFile(getTempFilename(),getFilename());
                copyWaveFile(getTempFilename(),setup_audio_signature());
                
    	        // Load the sounds         
                try {
                    FileDescriptor fd = null;
                    FileInputStream fis = new FileInputStream(setup_audio_signature());
                    fd = fis.getFD();

                    if (fd != null) {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(fd);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // End replay the sound
                
                deleteTempFile();  
        }
 
        private void deleteTempFile() {
                File file = new File(getTempFilename());
                 
                file.delete();
        }
         
        private void copyWaveFile(String inFilename,String outFilename){
                FileInputStream in = null;
                FileOutputStream out = null;
                long totalAudioLen = 0;
                long totalDataLen = totalAudioLen + 36;
                long longSampleRate = RECORDER_SAMPLERATE;
                //int channels = 2;
                int channels = 1;
                long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
                 
                byte[] data = new byte[bufferSize];
                 
                try {
                        in = new FileInputStream(inFilename);
                        out = new FileOutputStream(outFilename);
                        totalAudioLen = in.getChannel().size();
                        totalDataLen = totalAudioLen + 36;
                         
                        WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                                        longSampleRate, channels, byteRate);
                         
                        while(in.read(data) != -1){
                                out.write(data);
                        }
                         
                        in.close();
                        out.close();
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
 
        private void WriteWaveFileHeader(
                        FileOutputStream out, long totalAudioLen,
                        long totalDataLen, long longSampleRate, int channels,
                        long byteRate) throws IOException {
                 
                byte[] header = new byte[44];
                 
                header[0] = 'R';  // RIFF/WAVE header
                header[1] = 'I';
                header[2] = 'F';
                header[3] = 'F';
                header[4] = (byte) (totalDataLen & 0xff);
                header[5] = (byte) ((totalDataLen >> 8) & 0xff);
                header[6] = (byte) ((totalDataLen >> 16) & 0xff);
                header[7] = (byte) ((totalDataLen >> 24) & 0xff);
                header[8] = 'W';
                header[9] = 'A';
                header[10] = 'V';
                header[11] = 'E';
                header[12] = 'f';  // 'fmt ' chunk
                header[13] = 'm';
                header[14] = 't';
                header[15] = ' ';
                header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
                header[17] = 0;
                header[18] = 0;
                header[19] = 0;
                header[20] = 1;  // format = 1
                header[21] = 0;
                header[22] = (byte) channels;
                header[23] = 0;
                header[24] = (byte) (longSampleRate & 0xff);
                header[25] = (byte) ((longSampleRate >> 8) & 0xff);
                header[26] = (byte) ((longSampleRate >> 16) & 0xff);
                header[27] = (byte) ((longSampleRate >> 24) & 0xff);
                header[28] = (byte) (byteRate & 0xff);
                header[29] = (byte) ((byteRate >> 8) & 0xff);
                header[30] = (byte) ((byteRate >> 16) & 0xff);
                header[31] = (byte) ((byteRate >> 24) & 0xff);
                header[32] = (byte) (2 * 16 / 8);  // block align
                header[33] = 0;
                header[34] = RECORDER_BPP;  // bits per sample
                header[35] = 0;
                header[36] = 'd';
                header[37] = 'a';
                header[38] = 't';
                header[39] = 'a';
                header[40] = (byte) (totalAudioLen & 0xff);
                header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
                header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
                header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
 
                out.write(header, 0, 44);
        }

        /*****
        private final int SPEECH_RECOGNITION_CODE = 1;

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case SPEECH_RECOGNITION_CODE: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String text = result.get(0);

                        Toast.makeText(
                                this, "Speech recognition output:" + text, Toast.LENGTH_LONG).show();
                        //txtOutput.setText(text);
                    }
                    break;
                }

            }
        }
        *****/

        private View.OnClickListener btnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        switch(v.getId()){
                                case R.id.btnStart:{
                                    /*****
                                    new AsyncTask<Void, Void, Exception>() {
                                        @Override
                                        protected Exception doInBackground(Void... params) {
                                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                                                    "Speak something...");
                                            try {
                                                startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
                                            } catch (ActivityNotFoundException a) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Sorry! Speech recognition is not supported in this device.",
                                                        Toast.LENGTH_SHORT).show();
                                            }


                                            return null;
                                        }

                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    *****/
                                        enableButtons(true);
                                        startRecording();
                                                         
                                        break;
                                }
                                case R.id.btnStop:{
                                         
                                        enableButtons(false);
                                        stopRecording();
                 
                                	 	AlertDialog.Builder dbuilder = new AlertDialog.Builder(SoundRecording.this);
                                		WebView wv = new WebView(SoundRecording.this);
                                		wv.loadDataWithBaseURL(null, "Click the device back button if you are satisfied with the recording. Otherwise click the Record button to re-record. \n Signature extraction may take up to a min.", "text/html", "utf-8", null);
                                		dbuilder.setView(wv);
                                		dbuilder.setTitle("Sound recording and playback");
                                		dbuilder.setPositiveButton("OK", null);
                                		dbuilder.show();
                                		
                                        break;
                                }
                        }
                }
        }; 
}



