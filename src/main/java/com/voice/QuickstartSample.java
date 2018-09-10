package com.voice;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import static com.voice.Constants.*;

public class QuickstartSample {
    public static void main(String[] args) throws Exception {
        long mills1 = System.currentTimeMillis();
        System.out.println(" ---- " + new Date(mills1));

        String fileName = REPEATE_THE_QUESTION_MONO_filePath;
        //getTranscription(fileName);
        streamingRecognizeFile(fileName);

        long mills2 = System.currentTimeMillis();
        System.out.println(" ---- " + new Date(mills2));
        System.out.println((mills2-mills1)/1000);
    }

    /**
     * Performs streaming speech recognition on raw PCM audio data.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public static void streamingRecognizeFile(String fileName) throws Exception, IOException {
        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);

        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        try (SpeechClient speech = SpeechClient.create()) {

            // Configure request with local raw PCM audio
            RecognitionConfig recConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(rate)
                            .build();
            StreamingRecognitionConfig config =
                    StreamingRecognitionConfig.newBuilder().setConfig(recConfig).build();

//            // Builds the sync recognize request
//            RecognitionConfig config = RecognitionConfig.newBuilder()
//                    .setEncoding(AudioEncoding.LINEAR16)
//                    .setSampleRateHertz(44100)
//                    .setLanguageCode("en-US")
//                    .setAudioChannelCount(2)
//                    .build();
//            RecognitionAudio audio = RecognitionAudio.newBuilder()
//                    .setContent(audioBytes)
//                    .build();

            class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
                private final SettableFuture<List<T>> future = SettableFuture.create();
                private final List<T> messages = new java.util.ArrayList<T>();

                @Override
                public void onNext(T message) {
                    messages.add(message);
                }

                @Override
                public void onError(Throwable t) {
                    future.setException(t);
                }

                @Override
                public void onCompleted() {
                    future.set(messages);
                }

                // Returns the SettableFuture object to get received messages / exceptions.
                public SettableFuture<List<T>> future() {
                    return future;
                }
            }

            ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver =
                    new ResponseApiStreamingObserver<>();

            BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable =
                    speech.streamingRecognizeCallable();

            ApiStreamObserver<StreamingRecognizeRequest> requestObserver =
                    callable.bidiStreamingCall(responseObserver);

            // The first request must **only** contain the audio configuration:
            requestObserver.onNext(
                    StreamingRecognizeRequest.newBuilder().setStreamingConfig(config).build());

            // Subsequent requests must **only** contain the audio data.
            requestObserver.onNext(
                    StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(data))
                            .build());

            // Mark transmission as completed after sending the data.
            requestObserver.onCompleted();

            List<StreamingRecognizeResponse> responses = responseObserver.future().get();

            for (StreamingRecognizeResponse response : responses) {
                // For streaming recognize, the results list has one is_final result (if available) followed
                // by a number of in-progress results (if iterim_results is true) for subsequent utterances.
                // Just print the first result here.
                StreamingRecognitionResult result = response.getResultsList().get(0);
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcript : %s\n", alternative.getTranscript());
            }
        }
    }

    /**
     * Demonstrates using the Speech API to transcribe an audio file.
     */
    public static String getTranscription(String fileName) throws Exception {
        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create()) {

            // The path to the audio file to transcribe
            //String fileName = "./resources/audio.raw";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(rate)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            //long mills1 = System.currentTimeMillis();
            //System.out.println(" ---- " + new Date(mills1));

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);

            //long mills2 = System.currentTimeMillis();
            //System.out.println(" ---- " + new Date(mills2));
            //System.out.println((mills2-mills1)/1000);

            List<SpeechRecognitionResult> results = response.getResultsList();

            String resultText = null;

            //todo разобраться почему несколько результатов. пока я беру первый
            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                resultText = alternative.getTranscript();
                break;
            }

            return resultText;
        }
    }
}
