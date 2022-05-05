import javax.print.DocFlavor;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Movements {

    public String account; //Номер счета 0
    public String accountType; //Тип счёта 1
    public String currency; //Валюта 2
    public String date; //дата операции 3
    public String ref; //Референс проводки 4
    public String description; //Описание операции 5
    public double income; //Приход 6
    public double expense; //Расход 7
    List<Movements> movementsList = new ArrayList<>();
    static Map<String, Double> map = new HashMap<>();

    public Movements(String pathMovementsCsv) {
        setNewMovement(Paths.get(pathMovementsCsv));
        System.out.println("Сумма расходов: " + getExpenseSum());
        System.out.println("Сумма доходов: " + getIncomeSum());
        System.out.println("\nСуммы расходов по организациям:");
        getMap();
    }

    private void getMap() {
        for (Map.Entry e: map.entrySet()){
            System.out.println(e.getKey() + "\t\t\t" + e.getValue());
        }
    }

    public Movements(String[] s){
        this.account = s[0];
        this.accountType = s[1];
        this.currency = s[2];
        this.date = s[3];
        this.ref = s[4];
        this.description = s[5];
        this.income = Double.parseDouble(s[6].replaceAll("\\,", "."));
        this.expense = Double.parseDouble(s[7].replaceAll("\\,", "."));
        mapSet(description, expense);
    }

    private void mapSet(String description, double expense) {

        String recipient = description.split("\\s{4}")[1].trim();
        String[] r = recipient.split("\\\\");
        String org;

        if (r.length == 1){
            r =r[0].split("/");
        }

        StringBuilder b = new StringBuilder();
        Pattern p = Pattern.compile("^\\d+$");

        for (int i = 1; i < r.length; i++){
            Matcher m = p.matcher(r[i]);
            if (m.find()){
                continue;
            }
            b.append(r[i].trim() + " ");
        }
        org = b.toString().trim();
        if (map.containsKey(org)){
            map.put(org, map.get(org) + expense);
        }
        else map.put(org, expense);
    }


    private void setNewMovement(Path path) {
        try{
            List<String> list = Files.readAllLines(path);
            Pattern pQM = Pattern.compile("\"\\S+\"");
            for(int i = 1; i< list.size(); i++){
                String line = list.get(i);
                Matcher mQM = pQM.matcher(line);
                if (mQM.find()){
                    int start = mQM.start();
                    int end = mQM.end();
                    String text = line.substring(start, end);
                    line = line.replace(text, line.substring(start + 1, end - 1).replace(",","."));
                }
                movementsList.add(new Movements(line.split("\\,")));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public double getExpenseSum() {
        return movementsList.stream().mapToDouble(movements -> movements.expense).sum();
    }

    public double getIncomeSum() {
        return movementsList.stream().mapToDouble(movements -> movements.income).sum();
    }
}
