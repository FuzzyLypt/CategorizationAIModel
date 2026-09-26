public class TrainedModel {
    public Vocabulary vocabulary;
    public Network network;

    public Vocabulary getVocabulary() {
        return vocabulary;
    }
    public Network getNetwork() {
        return network;
    }

    public TrainedModel(Vocabulary vocabulary, Network network) {
        this.vocabulary = vocabulary;
        this.network = network;
    }
}
