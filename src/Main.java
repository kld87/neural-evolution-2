import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("scenario #: ");
        String input = scanner.next();

        switch (input) {
            case "1":
                new Scenario1();
                break;

            case "2":
                new Scenario2();
                break;

            case "3":
            case "4d": //Scenario 4, dynamic trainer
                System.out.print("layers max: ");
                int layersMax = scanner.nextInt();

                System.out.print("neurons max: ");
                int neuronsMax = scanner.nextInt();

                System.out.print("memory max: ");
                int memoryMax = scanner.nextInt();

                System.out.print("steps max: ");
                int stepsMax = scanner.nextInt();

                System.out.print("mutation chances: ");
                int mutateChances = scanner.nextInt();

                if (input.equals("3")) {
                    new Scenario3(layersMax, neuronsMax, memoryMax, stepsMax, mutateChances);
                } else if (input.equals("4d")) {
                    new Scenario4(layersMax, neuronsMax, memoryMax, stepsMax, mutateChances);
                }
                break;

            case "4f": //Scenario 4, fixed trainer
                System.out.print("steps: ");
                int steps = scanner.nextInt();

                System.out.print("threshold: ");
                int threshold = scanner.nextInt();

                new Scenario4(steps, threshold);
                break;

            default:
                System.out.println("invalid scenario");
                break;

        }
    }
}