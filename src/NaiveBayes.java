import java.io.*;
import java.util.Scanner;

public class NaiveBayes {
    int minimumInstancesOnLeave = 2;
    Sample samples[];
    Attribute[] attrs;
    private boolean validation;
    private int kFold;
    int numberOfSamples;
    BufferedReader buffReader;
    Scanner scan;
    int numberOfAttributes;
    AttrStatistics attrStatistics[];
    int confMat[][];

    public NaiveBayes(String dataFile, Attribute attrs[], int numberOfSamples, boolean validation, int kFold) {
        confMat = new int[2][2];
        this.attrs = attrs;
        this.validation = validation;
        this.kFold = kFold;
        try {
            this.scan = new Scanner(new File(dataFile));
            this.scan.useDelimiter(",");
            this.buffReader = new BufferedReader(new FileReader(dataFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.numberOfAttributes = attrs.length;
        this.numberOfSamples = numberOfSamples;
        this.samples = new Sample[numberOfSamples];
        this.attrStatistics = new AttrStatistics[numberOfAttributes];
    }

    public void start() throws IOException {
        AttValue attVals[] = new AttValue[numberOfAttributes];

        int indexOfSample = 0;
        this.buffReader.readLine();
        String line = "";
        String splitBy = ",";
        while ((line = this.buffReader.readLine()) != null) {
            int indexOfVal = 0;
            String[] attVal = line.split(splitBy);
            while(indexOfVal < numberOfAttributes) {
                if(attrs[indexOfVal].isNumeric()) {
                    attVals[indexOfVal] = new AttValue(true,"", Double.parseDouble(attVal[indexOfVal]), attrs[indexOfVal].getName());// scan.nextDouble()
                } else {
                    attVals[indexOfVal] = new AttValue(false, attVal[indexOfVal], 0, attrs[indexOfVal].getName());
                }
                indexOfVal++;
            }
            samples[indexOfSample++] = new Sample(attVals);
            attVals = new AttValue[numberOfAttributes];
        }


        crossValidation(this.kFold);
    }

    private void crossValidation(int kFold) {
        int numOfCatValues = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
        int numOfSpecificCatValues[]= new int[numOfCatValues];
        for(int i = 0; i < this.numberOfSamples; i++) {
            for(int j = 0; j < numOfCatValues; j++) {
                if(this.samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                    numOfSpecificCatValues[j]++;
                }
            }
        }
        Sample sampsCategoricalValue[][] = new Sample[numOfCatValues][];
        for(int i = 0; i < numOfCatValues; i++) {
            sampsCategoricalValue[i] = new Sample[numOfSpecificCatValues[i]];
        }
        int specCatValIndex[] = new int[numOfCatValues];
        for(int i = 0; i < this.numberOfSamples; i++) {
            for(int j = 0; j < numOfCatValues; j++) {
                if(this.samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                    sampsCategoricalValue[j][specCatValIndex[j]++] = this.samples[i];
                }
            }
        }
        specCatValIndex = new int[numOfCatValues];
        Sample samps[][] = new Sample[kFold][this.numberOfSamples/kFold];
        for(int i = 0; i < kFold; i++) {
                int celk = 0;
                for(int h = 0; h < numOfCatValues; h++) {
                    int num = 0;
                    while(num != numOfSpecificCatValues[h]/kFold) {
                        int k = ((i*numOfSpecificCatValues[h]/kFold)+num+celk);
                        int z = (i*numOfSpecificCatValues[h]/kFold)+num;
                        samps[i][num+celk] = sampsCategoricalValue[h][((i*numOfSpecificCatValues[h]/kFold)+num)];//this.samples[(i * this.numberOfSamples / kFold) + j];
                        num++;
                    }
                    celk += num;
                }
        }

        for(int i = 0; i < 10; i++) {
            doBayes(samps, i);
        }
        int fs = 0;
        System.out.println("\nConfusion matrix:");
        for(int i = 0; i < 2; i++) {
            System.out.println();
            for(int j = 0; j < 2; j++)
                System.out.print(" " + this.confMat[i][j]);
        }
    }

    private void doBayes(Sample[][] samps, int indexTest) {
        Sample trainSamples[] = new Sample[this.numberOfSamples-this.numberOfSamples/kFold];
        int indexSamp = 0;
        for(int i = 0; i < kFold; i++) {
            if(i != indexTest) {
                for(int j = 0; j < this.numberOfSamples/kFold; j++) {
                    trainSamples[indexSamp++] = samps[i][j];
                }
            }
        }
        System.out.println("\n" + (indexTest+1) + " th fold");
        System.out.println("Train samples");
        for(int i = 0; i < samps.length; i++) {
            if(i != indexTest) {
                for (int j = 0; j < samps[i].length; j++) {
                    System.out.println(samps[i][j].toString());
                }
            }
        }
        System.out.println("Test samples");
        for(int j = 0; j < this.numberOfSamples/kFold; j++) {
            System.out.println(samps[indexTest][j]);
        }
        for(int i = 0; i < this.numberOfAttributes; i++) {
            attrStatistics[i] = new AttrStatistics(i, trainSamples, this.attrs[i], this.attrs[this.numberOfAttributes-1]
                    , this.numberOfAttributes-1
                    , attrs[this.numberOfAttributes-1].getValues());
            System.out.println("Attribute " + this.attrs[i].getName());
            if(this.attrs[i].isNumeric()) {
                for (int j = 0; j < this.attrs[this.numberOfAttributes - 1].getNumberOfValues(); j++) {
                    System.out.println("\tClass " + this.attrs[this.numberOfAttributes - 1].getValue(j));
                    System.out.println("\t\tStandard deviation: " + this.attrStatistics[i].getStandardDeviation(j)
                            + " Mean: " + this.attrStatistics[i].getMean(j));
                }
            } else {
                for (int j = 0; j < this.attrs[this.numberOfAttributes - 1].getNumberOfValues(); j++) {
                    System.out.println("\tClass " + this.attrs[this.numberOfAttributes - 1].getValue(j));
                    for(int k = 0; k < this.attrs[i].getNumberOfValues(); k++) {
                        System.out.println("\t\tProbability of " + this.attrs[i].getValue(k) + ": "
                                + this.attrStatistics[i].getProbability(k*this.attrs[this.numberOfAttributes - 1].getNumberOfValues() + j
                                , this.numberOfAttributes - 1));
                    }
                }
            }
        }
        int confMatrxx[][] =  new int[2][2];
        int numOfCatValues = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
        double probabilityClassFinal[] = new double[numOfCatValues];
        for(int i = 0; i < this.numberOfSamples/kFold; i++) {
            double evidence = 0;
            double probabilityClassAtt[] = new double[numOfCatValues];
            double probabilityClass[] = new double[numOfCatValues];
            for(int j = 0; j < numOfCatValues; j++) {
                probabilityClassAtt[j] = this.attrStatistics[this.numberOfAttributes-1].getProbability(j*3, 0);
                probabilityClass[j] =  this.attrStatistics[this.numberOfAttributes-1].getProbability(j*3, 0);
            }
            for(int j = 0; j < this.numberOfAttributes-1; j++) {
                for(int k = 0; k < numOfCatValues; k++) {
                    if(!this.attrs[j].isNumeric()) {
                        int indexOfAttValue = 0;
                        for(int g = 0; g < numOfCatValues; g++) {
                            if(samps[indexTest][i].getAttValue(j).getsValue().equals(this.attrs[j].getValue(g))) {
                                indexOfAttValue = g;
                            }
                        }
                        double nas = this.attrStatistics[j].getProbability(indexOfAttValue*numOfCatValues + k,0);
                        probabilityClass[k] *= this.attrStatistics[j].getProbability(indexOfAttValue*numOfCatValues + k,0);
                                //samps[indexTest][i].getAttValue(j).g
                    } else {
                        double nas =this.attrStatistics[j].getProbability(samps[indexTest][i].getAttValue(j).getnValue(), k);
                        probabilityClass[k] *= this.attrStatistics[j].getProbability(samps[indexTest][i].getAttValue(j).getnValue(), k);
                    }
                }
            }
            for(int j = 0; j < numOfCatValues; j++) {
                evidence += probabilityClass[j];
            }
            int bestProbIndex = 0;
            double bestProb = probabilityClass[0]/evidence;
            for(int j = 0; j < numOfCatValues; j++) {
                probabilityClassFinal[j] = probabilityClass[j]/evidence;
                if(probabilityClassFinal[j] > bestProb) {
                    bestProbIndex = j;
                    bestProb = probabilityClassFinal[j];
                }
            }
            String realValue = samps[indexTest][i].getAttValue(this.numberOfAttributes-1).getsValue();
            if(this.attrs[this.numberOfAttributes-1].getValue(bestProbIndex).equals(realValue)) {
                if(bestProbIndex == 1) {
                    confMat[0][0]++;
                } else {
                    confMat[1][1]++;
                }
            } else {
                if(realValue.equals(this.attrs[this.numberOfAttributes-1].getValue(1))) {
                    confMat[0][1]++;
                } else {
                    confMat[1][0]++;
                }
            }
        }
    }


}
