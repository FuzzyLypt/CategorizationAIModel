import Utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vocabulary {
    private Map<String, Integer> wordToIndex = new HashMap<>();
    private List<String> wordsInOrder = new ArrayList<>();
    private int nextIndex = 0;

    public void addWord(String word) {
        if (!wordToIndex.containsKey(word)) {
            wordToIndex.put(word, nextIndex);
            wordsInOrder.add(word);
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

    public String[] getWordsInOrder() {
        return wordsInOrder.toArray(new String[0]);
    }
}
