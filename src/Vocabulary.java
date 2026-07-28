import java.util.HashMap;
import java.util.Map;

public class Vocabulary {
    private Map<String, Integer> wordToIndex = new HashMap<>();
    private int nextIndex = 0;

    public void addWord(String word) {
        if (!wordToIndex.containsKey(word)) {
            wordToIndex.put(word, nextIndex);
            nextIndex++;
        }
    }

    public float[] vectorize(String[] words) {
        float[] vector = new float[wordToIndex.size()];
        for (String word : words) {
            if (wordToIndex.containsKey(word)) {
                vector[wordToIndex.get(word)] = 1.0f;
            }
        }
        return vector;
    }

    public int size() {
        return wordToIndex.size();
    }
}
