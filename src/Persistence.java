import java.io.*;
import java.util.Scanner;

public class Persistence {
    final static String saveFolderName = "SavedModels";
    final static int magicNumber = 0xCAFEBABE;
    final static int versionNumber = 1;

    public static void saveAsText(TrainedModel model, String filename) throws IOException {
        File saveFolder = new File(saveFolderName);
        if (!saveFolder.exists()) {
            boolean outputFolderExists = saveFolder.mkdirs();
            if (!outputFolderExists) {
                System.out.println("Fatal error creating folder!");
                System.exit(1);
            }
        }

        PrintWriter writer = new PrintWriter(new FileWriter(saveFolderName.concat("/" + filename + ".txt")));

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

    public static TrainedModel loadText(String filename) throws IOException {
        Scanner scanner = new Scanner(new File(saveFolderName.concat("/" + filename + ".txt")));

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

    public static void saveAsBinary(TrainedModel model, String filename) throws IOException {
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

    public static TrainedModel loadBinary(String filename) throws IOException {
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

    public static void converterMenu(Scanner scanner) {
        boolean input = false;
        do {
            System.out.println("""
                    
                    Choose conversion operation:
                    
                    1 - Text to binary
                    2 - Binary to text
                    0 - Exit operation""");
            byte choice = scanner.nextByte();
            switch (choice) {
                case 1:
                {
                    scanner.nextLine();
                    System.out.println("\nWhat is the EXACT name of the file you want to convert (without the file extension): ");
                    String filename = scanner.nextLine();
                    try {
                        textToBin(scanner, filename);
                        input = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("\nAn error has occurred and the file could not be converted.");
                    }
                }
                break;
                case 2:
                {
                    scanner.nextLine();
                    System.out.println("\nWhat is the EXACT name of the file you want to convert (without the file extension): ");
                    String filename = scanner.nextLine();
                    try {
                        binToText(scanner, filename);
                        input = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("\nAn error has occurred and the file could not be converted.");
                    }
                }
                break;
                case 0:
                    input = true;
                    break;
            }
        } while (!input);
    }

    public static void textToBin(Scanner scanner, String filename) throws IOException {
        TrainedModel model = Persistence.loadText(filename);

        boolean input = false;
        do {
            System.out.println("\nDo you wish to rename the converted file? (y/n)");
            scanner.nextLine();
            char choice = scanner.nextLine().charAt(0);
            switch (choice) {
                case 'y':
                {
                    System.out.println("\nInsert the new name of the converted file: ");
                    filename = scanner.nextLine();
                    input = true;
                }
                break;
                case 'n':
                    input = true;
                    break;
                default:
                    System.out.println("\nUnrecognized input.");
                    break;
            }
        } while (!input);
        Persistence.saveAsBinary(model, filename);
        System.out.println("\nSuccess! The text file has been converted to binary.");
    }

    public static void binToText(Scanner scanner, String filename) throws IOException {
        TrainedModel model = Persistence.loadBinary(filename);

        boolean input = false;
        do {
            System.out.println("\nDo you wish to rename the converted file? (y/n)");
            scanner.nextLine();
            char choice = scanner.nextLine().charAt(0);
            switch (choice) {
                case 'y':
                {
                    System.out.println("\nInsert the new name of the converted file: ");
                    filename = scanner.nextLine();
                    input = true;
                }
                break;
                case 'n':
                    input = true;
                    break;
                default:
                    System.out.println("\nUnrecognized input.");
                    break;
            }
        } while (!input);
        Persistence.saveAsText(model, filename);
        System.out.println("\nSuccess! The binary file has been converted to text.");
    }
}
