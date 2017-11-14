import java.util.ArrayList;

class RunLengthCompression {

    static Byte[] compress(byte[] binary) {
        ArrayList<Byte> compressed = new ArrayList<>();
        int counter = 1;
        for (int i = 0; i < binary.length; i++) {
            while (i + 1 < binary.length && binary[i] == binary[i + 1]) {
                counter++;
                if (counter == 256) {
                    compressed.add((byte) 0);
                    counter = 1;
                }
                i++;
            }
            compressed.add((byte) counter);
            compressed.add(binary[i]);
            counter = 1;
        }
        return (Byte[]) compressed.toArray();
    }

    static Byte[] decompress(byte[] compressed) {
        ArrayList<Byte> decompressed = new ArrayList<>();
        byte tmp;
        int count = 0;
        for (int i = 0; i < compressed.length; i++) {
            tmp = compressed[i];
            if (tmp == 0) {
                count += 255;
                continue;
            }
            count += tmp;
            for (int j = 0; j < count; j++) {
                decompressed.add(compressed[i + 1]);
            }
            i++;
            count = 0;
        }
        return (Byte[]) decompressed.toArray();

    }
}