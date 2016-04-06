# TensorFlowAndroidMNIST - Android MNIST demo with TensorFlow

This is a demo app for Android with Tensorflow to detect handwritten digits.

![image](http://narr.jp/private/miyoshi/tensorflow/mnist_screen0.png)

This Android demo is based on Tensorflow tutorial.

MNIST For ML Beginners
https://www.tensorflow.org/versions/r0.7/tutorials/mnist/beginners/index.html

Deep MNIST for Experts
https://www.tensorflow.org/versions/r0.7/tutorials/mnist/pros/index.html

## How to train model.
Training scripts for neural network model are located at

https://github.com/miyosuda/TensorFlowAndroidMNIST/tree/master/trainer-script

To create model by yourself, install Tensorflow and run python scripts like

    $ python beginner.py

or

    $ python expert.py

and locate exported .pb file to assets dir.

To export training model, I added some modification to original tutorial scripts.

Now Tensorflow cannot export network graph and trained network weight Variable at the same time,
so we need to create another graph to export and convert Variable into constants.

After training is finished, converted trained Variable to numpy ndarray.

    _W = W.eval(sess)
    _b = b.eval(sess)

and then convert them into constant and re-create graph for exporting.

    W_2 = tf.constant(_W, name="constant_W")
    b_2 = tf.constant(_b, name="constant_b")

And then use tf.train.write_graph to export graph with trained weights.


## How to build JNI codes

Native .so files are already built in this project, but if you would like to
build it by yourself, please install and setup NDK.

First download, extract and place Android NDK.

http://developer.android.com/intl/ja/ndk/downloads/index.html

And then update your PATH environment variable like,

    export NDK_HOME="/Users/[your-username]/Development/android/android-ndk-r11b"
    export PATH=$PATH:$NDK_HOME

And build .so file

    $ cd jni-build
    $ make
    
and copy .so file into /app/src/main/jniLibs/armeabi-v7a/ with

    $ make install

(Unlike original Android demo in Tensorflow, you don't need to install bazel to build this demo.
And it seems that original Tensorflow Android demo fails to be built with bazel and latest ndk-r11b, and requires old ndk-r10e now, but this demo can be built with r11b.)

Tensorflow library files (.a files) and header files are extracted from original Tensorflow Android demo r0.7.
