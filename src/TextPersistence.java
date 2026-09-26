import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TextPersistence {
    final static String saveFolderName = "SavedModels";

    public static void save(TrainedModel model, String filename) throws IOException {
        File saveFolder = new File(saveFolderName);
        if (!saveFolder.exists()) {
            boolean outputFolderExists = saveFolder.mkdirs();
            if (!outputFolderExists) {
                System.out.println("Fatal error creating folder!");
                System.exit(1);
            }
        }

        PrintWriter writer = new PrintWriter(new FileWriter(saveFolderName.concat("/" + filename)));

        String[] words = model.vocabulary.getWordsInOrder();
        writer.println(words.length);
        for (String word : words) {
            writer.println(word);
        }

        float[][] hiddenWeights = model.network.getHiddenWeights();
        writer.println(hiddenWeights.length);
        writer.println(hiddenWeights[0].length);
        for (float[] hiddenWeight : hiddenWeights) {
            for (float v : hiddenWeight) {
                writer.println(v);
            }
        }

        float[] hiddenBias = model.network.getHiddenBias();
        for (float bias : hiddenBias) {
            writer.println(bias);
        }

        float[] outputWeights = model.network.getOutputWeights();
        for (float outputWeight : outputWeights) {
            writer.println(outputWeight);
        }

        float outputBias = model.network.getOutputBias();
        writer.println(outputBias);

        writer.close();
    }

    public static TrainedModel load(String filename) throws IOException {
        Scanner scanner = new Scanner(new File(saveFolderName.concat("/" + filename)));

        int vocabSize = Integer.parseInt(scanner.nextLine());
        Vocabulary vocab = new Vocabulary();
        for (int i=0; i < vocabSize; i++) {
            vocab.addWord(scanner.nextLine());
        }

        int inputSize = Integer.parseInt(scanner.nextLine());
        int hiddenSize = Integer.parseInt(scanner.nextLine());
        Network loadedNetwork = new Network(inputSize);

        float[][] hiddenWeights = loadedNetwork.getHiddenWeights();
        for (int i=0; i < inputSize; i++) {
            for (int j=0; j < hiddenSize; j++) {
                hiddenWeights[i][j] = Float.parseFloat(scanner.nextLine());
            }
        }

        float[] hiddenBias = loadedNetwork.getHiddenBias();
        for (int i=0; i < hiddenSize; i++) {
            hiddenBias[i] = Float.parseFloat(scanner.nextLine());
        }

        float[] outputWeights = loadedNetwork.getOutputWeights();
        for (int i=0; i < hiddenSize ; i++) {
            outputWeights[i] = Float.parseFloat(scanner.nextLine());
        }

        loadedNetwork.setOutputBias(Float.parseFloat(scanner.nextLine()));

        scanner.close();

        return new TrainedModel(vocab, loadedNetwork);
    }
}
