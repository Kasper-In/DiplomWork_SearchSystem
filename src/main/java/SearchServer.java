import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SearchServer {

    private final int SERVER_PORT;

    public SearchServer(int SERVER_PORT) {
        this.SERVER_PORT = SERVER_PORT;
    }

    public void start() throws IOException {
        Gson gson = new Gson();
        BooleanSearchEngine searchEngine = new BooleanSearchEngine(new File("pdfs"));
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер стартовал на порту " + SERVER_PORT + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String requestClient = in.readLine();
                    String[] wordsFromRequest = requestClient.split("\\P{IsAlphabetic}+");
                    List<PageEntry> resultList = searchEngine.searchWords(wordsFromRequest);
                    if (resultList == null || resultList.size() == 0) {
                        out.println("Слова \"" + requestClient + "\" не найдены");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (PageEntry pageEntry : resultList) {
                            sb.append(gson.toJson(pageEntry));
                            sb.append("#");
                        }
                        String answerServer = sb.toString();
                        out.println(answerServer);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер :(");
            e.printStackTrace();
        }
    }
}