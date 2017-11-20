import java.util.Random;

public class NoiseGenerator {
    /**
     * This function takes byte array as an input and performs some flipping of bits
     * with probability of epsilon.
     * @param array is the input byte array
     * @param epsilon is the probability of flipping a bit
     */
    void generateNoise(byte[] array, double epsilon) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            StringBuilder newValue = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                byte temp = array[i];
                int value = (temp >>> (j)) & 1;
                if (epsilon >= random.nextDouble())
                    value = value == 1 ? 0 : 1;
                else
                    value = value == 1 ? 1 : 0;
                newValue.append(value);
            }
            newValue = newValue.reverse();
            String str = newValue.toString();
            int tr = Integer.parseInt(str, 2);
            array[i] = (byte)tr;
        }
    }
}
