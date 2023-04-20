package MainStudioComponents;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Audio {
    private int buffID;
    private int srcID;
    private String path;

    private boolean playing = false;

    public Audio(String path, boolean loops) {
        this.path = path;

        // Give space to store the return information from stb
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer =
                stb_vorbis_decode_filename(path, channelsBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null) {
            System.out.println("COULD NOT LOAD SOUND");
            stackPop();
            stackPop();
            return;
        }

        // Get info that was stored in buffers by stb
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        // free buffers
        stackPop();
        stackPop();

        // find openAL format
        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        buffID = alGenBuffers();
        alBufferData(buffID, format, rawAudioBuffer, sampleRate);

        // Generate the source
        srcID = alGenSources();

        alSourcei(srcID, AL_BUFFER, buffID);
        alSourcei(srcID, AL_LOOPING, loops ? 1 : 0);
        alSourcei(srcID, AL_POSITION, 0);
        alSourcef(srcID, AL_GAIN, 0.3f);

        // Free stb raw audio buffer
        free(rawAudioBuffer);
    }

    public void playAudio() {
        int state = alGetSourcei(srcID, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            playing = false;
            alSourcei(srcID, AL_POSITION, 0);
        }

        if (!playing) {
            alSourcePlay(srcID);
            playing = true;
        }
    }

    public String getPath() {
        return this.path;
    }

}