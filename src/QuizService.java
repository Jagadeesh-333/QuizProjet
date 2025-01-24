import java.util.*;
import java.util.concurrent.*;
 class QuestionService {
    List<Questions> questions = new ArrayList<>();
    int score = 0;

    public QuestionService() {
        generateRandomQuestions();
    }

    public void generateRandomQuestions() {
        String[] questionTexts = {
                "What is the capital of India?", "What is the capital of Australia?",
                "What is the capital of USA?", "What is the capital of UK?",
                "What is the capital of Japan?", "What is the capital of Canada?",
                "What is the capital of Germany?", "What is the capital of France?",
                "What is the capital of Italy?", "What is the capital of Brazil?"
        };
        String[][] options = {
                {"Delhi", "Mumbai", "Chennai", "Kolkata"},
                {"Canberra", "Sydney", "Melbourne", "Perth"},
                {"Washington DC", "New York", "Los Angeles", "Chicago"},
                {"London", "Manchester", "Birmingham", "Liverpool"},
                {"Tokyo", "Osaka", "Kyoto", "Hiroshima"},
                {"Ottawa", "Toronto", "Vancouver", "Montreal"},
                {"Berlin", "Munich", "Frankfurt", "Hamburg"},
                {"Paris", "Lyon", "Marseille", "Nice"},
                {"Rome", "Milan", "Naples", "Venice"},
                {"Brasilia", "Rio de Janeiro", "Sao Paulo", "Salvador"}
        };
        String[] answers = {
                "Delhi", "Canberra", "Washington DC", "London", "Tokyo",
                "Ottawa", "Berlin", "Paris", "Rome", "Brasilia"
        };

        for (int i = 0; i < questionTexts.length; i++) {
            questions.add(new Questions(i + 1, questionTexts[i], answers[i],
                    options[i][0], options[i][1], options[i][2], options[i][3]));
        }
    }

    public void playQuiz() {
        Collections.shuffle(questions);

        for (Questions q : questions) {
            List<String> options = new ArrayList<>(List.of(q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()));
            Collections.shuffle(options);

            String correctAnswer = q.getAnswer();
            char correctOption = ' ';

            // Find the correct option after shuffling
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).equalsIgnoreCase(correctAnswer)) {
                    correctOption = (char) ('A' + i);
                    break;
                }
            }

            System.out.println("\nQuestion no: " + q.getId());
            System.out.println(q.getQuestion());
            for (int i = 0; i < options.size(); i++) {
                char optionChar = (char) ('A' + i);
                System.out.println(optionChar + ". " + options.get(i));
            }

            System.out.println("Enter your answer (A, B, C, or D): ");
            System.out.flush();

            // Countdown timer thread
            Thread countdownThread = new Thread(() -> {
                for (int i = 10; i > 0; i--) {
                    System.out.print("\rTime left: " + i + " seconds    ");
                    System.out.flush();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;  // Stop countdown if interrupted
                    }
                }
                System.out.print("\rTime's up!                     \n");
            });

            countdownThread.start();

            // User input handling
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> {
                Scanner scanner = new Scanner(System.in);
                return scanner.nextLine().trim().toUpperCase();
            });

            try {
                String userAnswer = future.get(10, TimeUnit.SECONDS);

                if (userAnswer.length() == 1 && userAnswer.charAt(0) >= 'A' && userAnswer.charAt(0) <= 'D') {
                    char selectedOption = userAnswer.charAt(0);
                    if (selectedOption == correctOption) {
                        System.out.println("Correct Answer!");
                        score++;
                    } else {
                        System.out.println("Wrong Answer! The correct answer is: " + correctAnswer);
                    }
                } else {
                    System.out.println("Invalid input! Please answer using A, B, C, or D.");
                }
            } catch (TimeoutException e) {
                System.out.println("\nTime's up! The correct answer is: " + correctAnswer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                future.cancel(true);
                executor.shutdown();
            }

            countdownThread.interrupt();
        }

        calculateScore();
    }

    public void calculateScore() {
        System.out.println("\nYour final score is: " + score + " out of " + questions.size());
    }

    public static void main(String[] args) {
        QuestionService quiz = new QuestionService();
        quiz.playQuiz();
    }
}
