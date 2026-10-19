import java.io.IOException;
import java.util.Scanner;

public class FileConverter {
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
        TrainedModel model = TextPersistence.load(filename);

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
        BinaryPersistence.save(model, filename);
        System.out.println("\nSuccess! The text file has been converted to binary.");
    }

    public static void binToText(Scanner scanner, String filename) throws IOException {
        TrainedModel model = BinaryPersistence.load(filename);

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
        TextPersistence.save(model, filename);
        System.out.println("\nSuccess! The binary file has been converted to text.");
    }
}
