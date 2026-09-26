# 🧠 Text Classifier Neural Network

A small Java project implementing a binary text classifier neural network entirely from scratch with no ML libraries and no external dependencies. Built to understand exactly what's happening under the hood of a neural network: vectorization, forward passes, activation functions, and backpropagation, all written by hand with raw arrays and loops.

The network takes a sentence, converts it into a bag-of-words vector, and predicts a probability (0–100%) of it belonging to a target class. Currently trained as a spam/ham classifier, but reusable for any binary text categorization task.

---

## Features

- **Vocabulary building:** Maps every unique word seen in training data to an index, and converts arbitrary sentences into fixed-size bag-of-words vectors
- **Feedforward neural network:** A hand-built 2-layer network (`input → hidden (ReLU) → output (Sigmoid)`) with randomly initialized weights and biases
- **Backpropagation from scratch:** Full gradient descent implementation, including output-layer error, backward error propagation into the hidden layer, and weight/bias updates, with no autograd or external math libraries
- **Trainable on custom datasets:** Swap in your own labeled sentences to train the network on any binary classification task
- **Precomputed input vectors:** Training inputs are vectorized once before the epoch loop for efficiency, rather than repeating identical work every epoch

---

## Project Structure

| File | Responsibility |
|---|---|
| `Vocabulary.java` | Builds the word → index mapping and vectorizes sentences into bag-of-words arrays |
| `Network.java` | Holds all weights/biases and runs the forward pass (`predict`) |
| `Trainer.java` | Implements backpropagation and updates the network's weights via `trainOne` |
| `Main.java` | Loads training data, builds the vocabulary, trains the network across epochs, and tests it on unseen sentences |

---

## Getting Started

### Prerequisites
- [OpenJDK 21+](https://jdk.java.net/21/)

### Running the project
1. Clone the repository:
   ```bash
   git clone https://github.com/FuzzyLypt/CategorizationAIModel
   ```
2. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)
3. Compile and run:
   ```bash
   javac *.java
   java Main
   ```

---

## How It Works

1. **Vectorization:** Each training sentence is split into words and converted into a bag-of-words vector (1.0 if a word is present, 0.0 otherwise)
2. **Forward pass:** The vector flows through a hidden layer (ReLU activation) and into a single output neuron (Sigmoid activation), producing a probability
3. **Training loop:** For each labeled example, the network's prediction is compared to the true label, and the error is backpropagated to adjust every weight and bias, repeated over many epochs
4. **Testing:** The trained network is evaluated on sentences it has never seen, to check whether it generalized rather than memorized

> **Note:** since this is a bag-of-words model, it can only recognize exact words seen during training (words outside the training vocabulary are silently ignored). Expanding the training dataset is the most direct way to improve accuracy.

---

## Tech Stack
- **Language:** Java
- **JDK:** OpenJDK 21+
- **Dependencies:** none — pure Java (`java.util.HashMap`, `java.util.Random`)

---

## Contact
**Larson A. Oliveira**

[![Discord](https://img.shields.io/badge/Discord-%40fuzzylypt227-5865F2?logo=discord&logoColor=white)](https://discord.com)
[![Email](https://img.shields.io/badge/Email-larson.oliveira123%40gmail.com-D14836?logo=gmail&logoColor=white)](mailto:larson.oliveira123@gmail.com)
