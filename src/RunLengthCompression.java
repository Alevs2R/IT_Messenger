class RunLengthCompression {
    static String compress(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            int runLength = 1;
            while (i+1 < source.length() && source.charAt(i) == source.charAt(i+1)) {
                runLength++;
                i++;
            }
            if (runLength == 1){
                result.append(source.charAt(i));
            }
            else {
                result.append(runLength);
                result.append(source.charAt(i));
            }
        }
        return result.toString();
    }

    static String decompress(String input){
        StringBuilder result  = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '0' || input.charAt(i) == '1') {
                result.append(input.charAt(i));
            }
            else{
                int num = Integer.parseInt(input.substring(i,i+1));
                while (--num != 0){
                    result.append(input.charAt(i+1));
                }
            }
        }
        return result.toString();
    }
}