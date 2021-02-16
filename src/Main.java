import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /*double stand = 0.035033;
        double x = 6;
        double mean = 5.855;
        double prob = (1/Math.sqrt(2*Math.PI*stand))*Math.exp(-((Math.pow(x-mean,2))/(2*stand)));
        System.out.println(prob);*/
        /*Attribute vyska = new Attribute("vyska");
        Attribute vaha = new Attribute("vaha");
        Attribute velkostNohy = new Attribute("velkostNohy");
        Attribute pohlavie = new Attribute(2, new String[]{"mužský", "ženský"}, "pohlavie");*/
        double prob = (1/Math.sqrt(2*Math.PI*0.035033))
                *Math.exp(-((Math.pow(6-5.855,2))/(2*0.035033)));

        Attribute age = new Attribute("age");
        Attribute anaemia = new Attribute(2, new String[]{"non_anemic", "anemic"}, "anaemia");
        Attribute creatinine = new Attribute("creatinine");
        Attribute diabetes = new Attribute(2, new String[]{"non_diabetic", "diabetic"}, "diabetes");
        Attribute ejectionFraction = new Attribute("ejectionFraction");
        Attribute highBloodPressure = new Attribute(2, new String[]{"non_hypertensive", "hypertensive"}, "highBloodPressure");
        Attribute platelets = new Attribute("platelets");
        Attribute serumCreatinine = new Attribute("serumCreatinine");
        Attribute serumSodium = new Attribute("serumSodium");
        Attribute sex = new Attribute(2, new String[]{"man", "woman"}, "sex");
        Attribute smoking = new Attribute(2, new String[]{"non_smoking", "smoking"}, "smoking");
        Attribute time = new Attribute("time");
        Attribute deathEvent = new Attribute(2, new String[]{"alive", "dead"}, "deathEvent");

        /*String dataFile = "osoby.csv";
        NaiveBayes naiveBayes = new NaiveBayes(dataFile, new Attribute[]{vyska, vaha, velkostNohy, pohlavie}, 8, false, 10);*/
        String dataFile = "heart_failure_clinical_records_dataset_upr.csv";
        NaiveBayes naiveBayes = new NaiveBayes(dataFile, new Attribute[]{age, anaemia, creatinine, diabetes, ejectionFraction, highBloodPressure, platelets,
                serumCreatinine, serumSodium, sex, smoking, time, deathEvent}, 290, false, 10);
        try {
            naiveBayes.start();
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }
}
