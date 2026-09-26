import java.util.Random;

public class Network {

    // === ACTIVATION FUNCTIONS ===
    // Pure math helpers — no dependency on any field, just transform a number.

    // ReLU: passes positive values through unchanged, crushes negative values to 0.
    // Used between the hidden layer, lets the network build nonlinear representations.
    private float relu(float x) { return Math.max(0f, x); }

    // Sigmoid: squashes any real number into the range (0, 1).
    // Used at the output layer to produce a "probability" (0 = not spam, 1 = spam).
    private float sigmoid(float x) { return (float) (1 / (1 + Math.exp(-x))); }

    // === FIELDS — the network's entire learned "memory" ===

    private final int inputSize; // size of the vocabulary (number of input features)
    private int hiddenSize = 16; // number of hidden neurons

    // hiddenWeights[i][j] = strength of connection from input feature i -> hidden neuron j
    // shape: [inputSize][hiddenSize]
    private float[][] hiddenWeights;

    // hiddenBias[j] = bias added to hidden neuron j before activation
    // shape: [hiddenSize]
    private float[] hiddenBias;

    // outputWeights[k] = strength of connection from hidden neuron k -> the single output neuron
    // shape: [hiddenSize]
    private float[] outputWeights;

    // outputBias = bias added to the single output neuron before activation
    private float outputBias;

    // Snapshot of the hidden layer's activations from the MOST RECENT call to predict().
    // Trainer needs these values to compute weight updates, but predict() only returns
    // the final prediction — so we cache the intermediate hiddenOutput here instead.
    private float[] lastHiddenOutput;

    // === GETTERS / SETTER ===
    // hiddenWeights, hiddenBias, outputWeights are arrays (reference types) —
    // returning them exposes the REAL internal array. Trainer can mutate them
    // directly through the returned reference, no setter needed.
    public float[][] getHiddenWeights() { return hiddenWeights; }
    public float[] getHiddenBias() { return hiddenBias; }
    public float[] getOutputWeights() { return outputWeights; }

    // outputBias is a primitive (float) - returning it gives a copy, not a reference.
    // So mutating it from outside requires an explicit setter to write the new value back.
    public float getOutputBias() { return outputBias; }
    public void setOutputBias(float outputBias) {
        this.outputBias = outputBias;
    }

    // === CONSTRUCTOR ===
    // Allocates all arrays at the correct sizes and randomly initializes the weights.
    public Network(int inputSize) {
        this.inputSize = inputSize;

        // Allocate memory for every field. Biases default to 0.0f automatically —
        // only weights get explicitly randomized below.
        hiddenWeights = new float[inputSize][hiddenSize];
        hiddenBias = new float[hiddenSize];
        outputWeights = new float[hiddenSize];
        outputBias = 0f;

        Random rand = new Random();

        // Randomly initialize every hidden layer weight to a small value in [-0.5, 0.5).
        // This breaks "symmetry", if all weights started equal (e.g. all 0),
        // every hidden neuron would compute the same thing and update identically
        // forever, making extra neurons pointless. Random values let each neuron
        // specialize differently during training.
        for (int i = 0; i < hiddenWeights.length; i++) {
            for (int j = 0; j < hiddenWeights[i].length; j++) {
                hiddenWeights[i][j] = rand.nextFloat(-.5f, .5f);
            }
        }

        // Same symmetry-breaking randomization for the output layer weights.
        for (int k = 0; k < outputWeights.length; k++) {
            outputWeights[k] = rand.nextFloat(-.5f, .5f);
        }
    }

    // Returns the hidden layer activations saved during the last predict() call.
    // Used by Trainer during backpropagation.
    public float[] getLastHiddenOutput() {
        return lastHiddenOutput;
    }

    // === FORWARD PASS ===
    // Takes a bag-of-words input vector, runs it through both layers,
    // and returns a single probability between 0 and 1.
    public float predict(float[] input) {

        // --- LAYER 1: input -> hidden (16 neurons) ---
        float[] hiddenOutput = new float[hiddenSize];

        // Outer loop: compute one hidden neuron at a time (j = which neuron).
        for (int j = 0; j < hiddenSize; j++) {

            // Start the running sum at this neuron's bias.
            float sum = hiddenBias[j];

            // Inner loop: accumulate the weighted contribution of every input feature
            // (i = which input feature) into this neuron's sum.
            for (int i = 0; i < inputSize; i++) {
                sum += input[i] * hiddenWeights[i][j];
            }

            // Apply ReLU exactly once, after the full sum for this neuron is built.
            hiddenOutput[j] = relu(sum);
        }

        // --- LAYER 2: hidden -> output (1 neuron) ---
        float outputSum = outputBias;

        // Accumulate the weighted contribution of every hidden neuron into the final sum.
        for (int k = 0; k < hiddenOutput.length; k++){
            outputSum += hiddenOutput[k] * outputWeights[k];
        }

        // Cache this pass's hidden activations so Trainer can retrieve them later
        // via getLastHiddenOutput() — they'd otherwise be lost once this method returns.
        lastHiddenOutput = hiddenOutput;

        // Apply Sigmoid once to the final sum, turning it into a 0-to-1 probability.
        return sigmoid(outputSum);
    }

}