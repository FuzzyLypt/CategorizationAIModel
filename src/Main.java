public class Main {

    public static void main(String[] args) {

        // === STEP 1: TRAINING DATA ===
        // A small labeled dataset — pairs of (sentence, label).
        // label = 1.0 means spam, 0.0 means not spam (ham).
        String[] sentences = {
                "buy cheap pills now",
                "meeting scheduled for tomorrow",
                "win a free prize today",
                "can we reschedule our call",
                "join our promotion telegram group",
                "please meet the deadline of the project",
                "enter our merch shop",
                "i've sent you the link for the site",
                "be part of our religion",
                "the meeting is going as planned"
        };
        float[] labels = {
                1f, 0f, 1f, 0f, 1f, 0f, 1f, 0f, 1f, 0f
        };

        // === STEP 2: BUILD THE VOCABULARY ===
        // Walk through every sentence, split into words, add each word to Vocabulary.
        String[][] words = new String[sentences.length][0];
        Vocabulary vocab = new Vocabulary();
        for (int i = 0; i < sentences.length; i++) {
            words[i] = sentences[i].trim().split("\\s+");
            for (int j = 0; j < words[i].length; j++) {
                vocab.addWord(words[i][j]);
            }
        }

        // === STEP 3: PRECOMPUTE INPUT VECTORS ===
        // Vectorize every sentence once, up front — since the vocabulary is now fixed,
        // re-vectorizing inside the epoch loop would just repeat identical work.
        float[][] trainingInputs = new float[sentences.length][];
        for (int i = 0; i < sentences.length; i++) {
            trainingInputs[i] = vocab.vectorize(words[i]);
        }

        // === STEP 4: CREATE THE NETWORK AND TRAINER ===
        Network network = new Network(vocab.size());
        Trainer trainer = new Trainer();
        float learningRate = 0.1f;
        int epochs = 500;

        // === STEP 5: TRAINING LOOP ===
        // Run through the entire dataset many times (epochs), calling trainOne
        // on each (sentence, label) pair every time.
        for (int i = 0; i < epochs; i++) {
            for (int j = 0; j < sentences.length; j++) {
                trainer.trainOne(network, trainingInputs[j], labels[j], learningRate);
            }
        }

        // === STEP 6: TEST ON NEW SENTENCES ===
        // Try the trained network on sentences it has NEVER seen during training —
        // this is the only real way to judge whether it learned a general pattern.
        String[] testSentences = {
                "claim your free reward",
                "movie night tonight",
                "come and buy things here",
                "we need to order more ink for the printers",
                "enter my site, i have lots of merch",
                "go to the meeting room at noon"
        };

        for (String testSentence : testSentences) {
            String[] testWords = testSentence.trim().split("\\s+");
            float[] resultVector = vocab.vectorize(testWords);
            System.out.printf("\n%s -> %.2f", testSentence, network.predict(resultVector));
        }
    }
}
