import tensorflow as tf
import shutil
import os.path

if os.path.exists("./tmp/beginner-export"):
    shutil.rmtree("./tmp/beginner-export")

# Import data
from tensorflow.examples.tutorials.mnist import input_data

mnist = input_data.read_data_sets("./tmp/data/", one_hot=True)

g = tf.Graph()

with g.as_default():
    c = tf.constant(5.0)
    assert c.graph is g

    # Create the model
    x = tf.placeholder("float", [None, 784])
    W = tf.Variable(tf.zeros([784, 10]), name="vaiable_W")
    b = tf.Variable(tf.zeros([10]), name="variable_b")
    y = tf.nn.softmax(tf.matmul(x, W) + b)

    # Define loss and optimizer
    y_ = tf.placeholder("float", [None, 10])
    cross_entropy = -tf.reduce_sum(y_ * tf.log(y))
    train_step = tf.train.GradientDescentOptimizer(0.01).minimize(cross_entropy)

    sess = tf.Session()

    # Train
    init = tf.initialize_all_variables()
    sess.run(init)

    for i in range(1000):
        batch_xs, batch_ys = mnist.train.next_batch(100)
        train_step.run({x: batch_xs, y_: batch_ys}, sess)

    # Test trained model
    correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))

    print(accuracy.eval({x: mnist.test.images, y_: mnist.test.labels}, sess))

# Store variable
_W = W.eval(sess)
_b = b.eval(sess)

sess.close()

# Create new graph for exporting
g2 = tf.Graph()
with g2.as_default():
    # Reconstruct graph
    x2 = tf.placeholder("float", [None, 784], name="input")
    W2 = tf.constant(_W, name="constant_W")
    b2 = tf.constant(_b, name="constant_b")
    y2 = tf.nn.softmax(tf.matmul(x2, W2) + b2, name="output")

    sess2 = tf.Session()

    init2 = tf.initialize_all_variables();
    sess2.run(init2)

    
    graph_def = g2.as_graph_def()
    
    tf.train.write_graph(graph_def, './tmp/beginner-export',
                         'beginner-graph.pb', as_text=False)

    # Test trained model
    y_2 = tf.placeholder("float", [None, 10])
    correct_prediction2 = tf.equal(tf.argmax(y2, 1), tf.argmax(y_2, 1))
    accuracy2 = tf.reduce_mean(tf.cast(correct_prediction2, "float"))
    print(accuracy2.eval({x2: mnist.test.images, y_2: mnist.test.labels}, sess2))
