from __future__ import absolute_import, unicode_literals
import input_data
import tensorflow as tf
import shutil
import os.path

export_dir = './tmp/expert-export'

if os.path.exists(export_dir):
    shutil.rmtree(export_dir)

def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev=0.1)
    return tf.Variable(initial)

def bias_variable(shape):
    initial = tf.constant(0.1, shape=shape)
    return tf.Variable(initial)

def conv2d(x, W):
    return tf.nn.conv2d(x, W, strides=[1, 1, 1, 1], padding='SAME')

def max_pool_2x2(x):
    return tf.nn.max_pool(x, ksize=[1, 2, 2, 1],
                          strides=[1, 2, 2, 1], padding='SAME')

mnist = input_data.read_data_sets("data/", one_hot=True)

g = tf.Graph()
with g.as_default():
    x = tf.placeholder("float", shape=[None, 784])
    y_ = tf.placeholder("float", shape=[None, 10])

    W_conv1 = weight_variable([5, 5, 1, 32])
    b_conv1 = bias_variable([32])
    x_image = tf.reshape(x, [-1, 28, 28, 1])
    h_conv1 = tf.nn.relu(conv2d(x_image, W_conv1) + b_conv1)
    h_pool1 = max_pool_2x2(h_conv1)

    W_conv2 = weight_variable([5, 5, 32, 64])
    b_conv2 = bias_variable([64])
    h_conv2 = tf.nn.relu(conv2d(h_pool1, W_conv2) + b_conv2)
    h_pool2 = max_pool_2x2(h_conv2)

    W_fc1 = weight_variable([7 * 7 * 64, 1024])
    b_fc1 = bias_variable([1024])
    h_pool2_flat = tf.reshape(h_pool2, [-1, 7 * 7 * 64])
    h_fc1 = tf.nn.relu(tf.matmul(h_pool2_flat, W_fc1) + b_fc1)

    keep_prob = tf.placeholder("float")
    h_fc1_drop = tf.nn.dropout(h_fc1, keep_prob)

    W_fc2 = weight_variable([1024, 10])
    b_fc2 = bias_variable([10])

    y_conv = tf.nn.softmax(tf.matmul(h_fc1_drop, W_fc2) + b_fc2)

    cross_entropy = -tf.reduce_sum(y_ * tf.log(y_conv))
    train_step = tf.train.AdamOptimizer(1e-4).minimize(cross_entropy)
    correct_prediction = tf.equal(tf.argmax(y_conv, 1), tf.argmax(y_, 1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))

    sess = tf.Session()
    sess.run(tf.initialize_all_variables())

    for i in range(20000):
        batch = mnist.train.next_batch(50)
        if i % 100 == 0:
            train_accuracy = accuracy.eval(
                {x: batch[0], y_: batch[1], keep_prob: 1.0}, sess)
            print "step %d, training accuracy %g" % (i, train_accuracy)
        train_step.run(
            {x: batch[0], y_: batch[1], keep_prob: 0.5}, sess)

    print "test accuracy %g" % accuracy.eval(
        {x: mnist.test.images, y_: mnist.test.labels, keep_prob: 1.0}, sess)

# Store variable
_W_conv1 = W_conv1.eval(sess)
_b_conv1 = b_conv1.eval(sess)
_W_conv2 = W_conv2.eval(sess)
_b_conv2 = b_conv2.eval(sess)
_W_fc1 = W_fc1.eval(sess)
_b_fc1 = b_fc1.eval(sess)
_W_fc2 = W_fc2.eval(sess)
_b_fc2 = b_fc2.eval(sess)

sess.close()

# Create new graph for exporting
g_2 = tf.Graph()
with g_2.as_default():
    x_2 = tf.placeholder("float", shape=[None, 784], name="input")

    W_conv1_2 = tf.constant(_W_conv1, name="constant_W_conv1")
    b_conv1_2 = tf.constant(_b_conv1, name="constant_b_conv1")
    x_image_2 = tf.reshape(x_2, [-1, 28, 28, 1])
    h_conv1_2 = tf.nn.relu(conv2d(x_image_2, W_conv1_2) + b_conv1_2)
    h_pool1_2 = max_pool_2x2(h_conv1_2)

    W_conv2_2 = tf.constant(_W_conv2, name="constant_W_conv2")
    b_conv2_2 = tf.constant(_b_conv2, name="constant_b_conv2")
    h_conv2_2 = tf.nn.relu(conv2d(h_pool1_2, W_conv2_2) + b_conv2_2)
    h_pool2_2 = max_pool_2x2(h_conv2_2)
    
    W_fc1_2 = tf.constant(_W_fc1, name="constant_W_fc1")
    b_fc1_2 = tf.constant(_b_fc1, name="constant_b_fc1")
    h_pool2_flat_2 = tf.reshape(h_pool2_2, [-1, 7 * 7 * 64])
    h_fc1_2 = tf.nn.relu(tf.matmul(h_pool2_flat_2, W_fc1_2) + b_fc1_2)

    W_fc2_2 = tf.constant(_W_fc2, name="constant_W_fc2")
    b_fc2_2 = tf.constant(_b_fc2, name="constant_b_fc2")

    # DropOut is skipped for exported graph.
    
    y_conv_2 = tf.nn.softmax(tf.matmul(h_fc1_2, W_fc2_2) + b_fc2_2, name="output")
    
    sess_2 = tf.Session()
    init_2 = tf.initialize_all_variables();
    sess_2.run(init_2)

    graph_def = g_2.as_graph_def()
    tf.train.write_graph(graph_def, export_dir, 'expert-graph.pb', as_text=False)

    # Test trained model
    y__2 = tf.placeholder("float", [None, 10])
    correct_prediction_2 = tf.equal(tf.argmax(y_conv_2, 1), tf.argmax(y__2, 1))
    accuracy_2 = tf.reduce_mean(tf.cast(correct_prediction_2, "float"))

    print "check accuracy %g" % accuracy_2.eval(
        {x_2: mnist.test.images, y__2: mnist.test.labels}, sess_2)
