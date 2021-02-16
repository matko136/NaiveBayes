public class AttrStatistics {
    boolean isNumeric;
    double probability[];
    Attribute attribute;
    int indexAttr;
    double standardDeviation[];
    double mean[];
    public AttrStatistics(int indexAttr, Sample[] samples, Attribute attribute, Attribute classAttr
            , int classAttrInd, String[] classValues) {
        this.indexAttr = indexAttr;
        this.attribute = attribute;
        int numOfClassValues = classValues.length;
        this.isNumeric = attribute.isNumeric();
        standardDeviation = new double[numOfClassValues];
        mean = new double[numOfClassValues];
        int numOfClassesSamps[] = new int[numOfClassValues];
        this.probability = new double[attribute.getNumberOfValues()*numOfClassValues];
        if(!this.isNumeric) {
            int numOfAttValueClassSample[] = new int[attribute.getNumberOfValues() * numOfClassValues];
            for(int i =0; i < samples.length; i++) {
                for(int k = 0; k < attribute.getNumberOfValues(); k++) {
                    if(samples[i].getAttValue(indexAttr).getsValue().equals(attribute.getValue(k))) {
                        for (int j = 0; j < numOfClassValues; j++) {
                            if (samples[i].getAttValue(classAttrInd).getsValue().equals(classAttr.getValue(j))) {
                                numOfAttValueClassSample[k*numOfClassValues + j]++;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            for(int i = 0; i < attribute.getNumberOfValues()*numOfClassValues; i++) {
                probability[i] = (double)numOfAttValueClassSample[i]/samples.length;
            }
        } else {
            for(int i = 0; i < samples.length; i++) {
                for(int j = 0; j < numOfClassValues; j++) {
                    //System.out.println(i/*samples[i].getAttValue(classAttrInd).getsValue()*/);
                    if(samples[i].getAttValue(classAttrInd).getsValue().equals(classValues[j])) {
                        this.mean[j] += samples[i].getAttValue(this.indexAttr).getnValue();
                        numOfClassesSamps[j]++;
                        break;
                    }
                }
            }
            for(int i = 0; i < numOfClassValues; i++) {
                this.mean[i] = this.mean[i]/numOfClassesSamps[i];
            }
            for(int i = 0; i < samples.length; i++) {
                for(int j = 0; j < numOfClassValues; j++) {
                    if(samples[i].getAttValue(classAttrInd).getsValue().equals(classValues[j])) {
                        this.standardDeviation[j] += Math.pow((samples[i].getAttValue(this.indexAttr).getnValue()-this.mean[j]),2);
                        break;
                    }
                }
            }
            for(int i = 0; i < numOfClassValues; i++) {
                this.standardDeviation[i] = Math.sqrt(this.standardDeviation[i]/(numOfClassesSamps[i]));
            }
        }
    }

    public double getProbability(double data, int classAttrInd) {
        if(!this.isNumeric) {
            return this.probability[(int)data];
        } else {
            /*double stand = 0.035033;
            double x = 6;
            double mean = 5.855;
            double first = 1/Math.sqrt(2*Math.PI*stand);
            double exp = Math.exp(-((Math.pow(x-mean,2))/(2*stand)));
            double prob = first*exp;*/
            double prob = (1/Math.sqrt(2*Math.PI*Math.pow(this.standardDeviation[classAttrInd],2)))
                    *Math.exp(-((Math.pow(data-this.mean[classAttrInd],2))/(2*Math.pow(this.standardDeviation[classAttrInd],2))));
            return prob;
        }
    }

    public double getStandardDeviation(int index) {
        return standardDeviation[index];
    }

    public double getMean(int index) {
        return mean[index];
    }
}
