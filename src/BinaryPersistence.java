import java.io.*;

public class BinaryPersistence {
    final static String saveFolderName = "SavedModels";
    final static int magicNumber = 0xCAFEBABE;
    final static int versionNumber = 1;

    public static void save(TrainedModel model, String filename) throws IOException {
        File saveFolder = new File(saveFolderName);
        if (!saveFolder.exists()) {
            boolean outputFolderExists = saveFolder.mkdirs();
            if (!outputFolderExists) {
                System.out.println("Fatal error creating folder!");
                System.exit(1);
            }
        }

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(saveFolderName + "/" + filename + ".bin")))) {

            out.writeInt(magicNumber);
            out.writeInt(versionNumber);

            String[] words = model.vocabulary.getWordsInOrder();
            out.writeInt(words.length);
            for (String word : words) {
                out.writeUTF(word);
            }

            float[][] hiddenWeights = model.network.getHiddenWeights();
            out.writeInt(hiddenWeights.length);
            out.writeInt(hiddenWeights[0].length);
            for (float[] hiddenWeight : hiddenWeights) {
                for (float v : hiddenWeight) {
                    out.writeFloat(v);
                }
            }

            float[] hiddenBias = model.network.getHiddenBias();
            for (float bias : hiddenBias) {
                out.writeFloat(bias);
            }

            float[] outputWeights = model.network.getOutputWeights();
            for (float outputWeight : outputWeights) {
                out.writeFloat(outputWeight);
            }

            out.writeFloat(model.network.getOutputBias());

            out.close();
        }

    }

    public static TrainedModel load(String filename) throws IOException {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(saveFolderName.concat("/" + filename + ".bin"))))) {

            int fileMagicNumber = in.readInt();
            if (fileMagicNumber != magicNumber) {
                throw new IOException("Incompatible File!");
            }
            int fileVersionNumber = in.readInt();
            if (fileVersionNumber != versionNumber) {
                throw new IOException("Invalid File Version!");
            }

            int vocabSize = in.readInt();
            Vocabulary vocab = new Vocabulary();
            for (int i = 0; i < vocabSize; i++) {
                vocab.addWord(in.readUTF());
            }

            int inputSize = in.readInt();
            int hiddenSize = in.readInt();
            Network loadedNetwork = new Network(inputSize);

            float[][] hiddenWeights = loadedNetwork.getHiddenWeights();
            for (int i = 0; i < inputSize; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    hiddenWeights[i][j] = in.readFloat();
                }
            }

            float[] hiddenBias = loadedNetwork.getHiddenBias();
            for (int i = 0; i < hiddenSize; i++) {
                hiddenBias[i] = in.readFloat();
            }

            float[] outputWeights = loadedNetwork.getOutputWeights();
            for (int i = 0; i < hiddenSize; i++) {
                outputWeights[i] = in.readFloat();
            }

            loadedNetwork.setOutputBias(in.readFloat());

            in.close();

            return new TrainedModel(vocab, loadedNetwork);
        }
    }
}
