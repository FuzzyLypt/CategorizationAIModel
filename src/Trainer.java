public class Trainer {

    // Derivative of the sigmoid function, expressed in terms of its OWN output.
    // If s = sigmoid(z), then sigmoid'(z) = s * (1 - s) — no need to recompute from z.
    // Tells us how sensitive the output is to a small change in z at this exact point.
    private float sigmoidDerivative(float sigmoidOutput) {
        return sigmoidOutput * (1 - sigmoidOutput);
    }

    // Derivative of ReLU: 1 if the neuron was "alive" (output > 0), 0 if it was "dead"
    // (clamped to 0 during the forward pass). Dead neurons get zero blame during
    // backprop, since nudging their inputs wouldn't have changed their output anyway.
    private float reluDerivative(float reluOutput) {
        return reluOutput > 0 ? 1 : 0;
    }

    // Runs ONE step of backpropagation on ONE training example, updating the
    // network's weights and biases in place (mutating network's real arrays).
    public void trainOne(Network network, float[] input, float trueLabel, float learningRate) {

        // === STEP 1: FORWARD PASS ===
        // Run the input through the network to get its current prediction,
        // and retrieve the hidden layer activations from that same pass
        // (predict() caches them internally since it can only return one value).
        float prediction = network.predict(input);
        float[] hiddenOutput = network.getLastHiddenOutput();

        // === STEP 2: OUTPUT LAYER ERROR ===
        // How wrong was the prediction, and how much should the pre-sigmoid
        // sum (z) change to correct it?
        // outputError: raw difference between what we predicted and the true label.
        // outputDelta: that error scaled by sigmoid's sensitivity at this point —
        //              this is the "signal" we'll propagate backward through the network.
        float outputError = prediction - trueLabel;
        float outputDelta = outputError * sigmoidDerivative(prediction);

        // === STEP 3: UPDATE OUTPUT LAYER ===
        // Fetch the REAL outputWeights array (a reference, not a copy) so that
        // mutating it here actually changes the network.
        // Each weight moves opposite to its contribution to the error, scaled by
        // how strongly that hidden neuron fired (hiddenOutput[k]) and by learningRate
        // (how big a step to take).
        float[] outputWeights = network.getOutputWeights();
        for (int k = 0; k < outputWeights.length; k++) {
            outputWeights[k] -= learningRate * outputDelta * hiddenOutput[k];
        }

        // outputBias is a primitive, not a reference — can't mutate it through a getter.
        // Read the current value, compute the new value, write it back with the setter.
        network.setOutputBias(network.getOutputBias() - learningRate * outputDelta);

        // === STEP 4: PROPAGATE ERROR BACKWARD INTO THE HIDDEN LAYER ===
        // The hidden layer has no "true label" of its own — it only influenced the
        // final prediction THROUGH outputWeights. So each hidden neuron j's share of
        // the blame is proportional to how strongly it was connected to the output.
        float[] hiddenError = new float[outputWeights.length];
        float[] hiddenDelta = new float[outputWeights.length];
        for (int j = 0; j < outputWeights.length; j++) {
            // hiddenError[j]: blame assigned to neuron j, via its connection weight
            // to the output (using outputWeights[j], NOT hiddenOutput[j] — these are
            // different arrays with different meanings).
            hiddenError[j] = outputDelta * outputWeights[j];

            // hiddenDelta[j]: that blame gated by ReLU's derivative — if neuron j was
            // "dead" (output was 0) during the forward pass, it gets zero update here.
            hiddenDelta[j] = hiddenError[j] * reluDerivative(hiddenOutput[j]);
        }

        // === STEP 5: UPDATE HIDDEN LAYER ===
        // Fetch the REAL hiddenWeights and hiddenBias arrays so mutations persist.
        float[][] hiddenWeights = network.getHiddenWeights();
        float[] hiddenBias = network.getHiddenBias();

        // Outer loop: one hidden neuron at a time (j = which neuron).
        for (int j = 0; j < hiddenOutput.length; j++) {

            // hiddenBias[j] depends only on j, not i — update it exactly ONCE per
            // neuron, outside the inner loop (mirrors how relu(sum) was applied once
            // per neuron in Network.predict()).
            hiddenBias[j] -= learningRate * hiddenDelta[j];

            // Inner loop: walk every input feature (i = which word in the vocabulary)
            // connected to this neuron, and nudge that specific weight.
            // input[i] scales the update — if a word wasn't present (input[i] == 0),
            // that weight gets no update this round, since it had no effect this time.
            for (int i = 0; i < input.length; i++) {
                hiddenWeights[i][j] -= learningRate * hiddenDelta[j] * input[i];
            }
        }
    }
}