//
// Created by andy on 13/11/19.
//

#include <jni.h>
#include <string>


class Cell{
public:
    double ss = 0;
};


jfloatArray x_arr;
jfloatArray y_arr;
Cell *c;

extern "C" JNIEXPORT void JNICALL
Java_com_example_tsnegallery_TsneActivity_initTsneJNI(
        JNIEnv *env, jobject obj, jfloatArray x, jfloatArray y){

    c = new Cell();
    c->ss = 100;

    x_arr = (jfloatArray)env->NewGlobalRef((jobject)x);
    y_arr = (jfloatArray)env->NewGlobalRef((jobject)y);

}


extern "C" JNIEXPORT void JNICALL
Java_com_example_tsnegallery_TsneActivity_destroyTsneJNI(
        JNIEnv *env, jobject obj){

    jfloat *x = env->GetFloatArrayElements(x_arr, NULL);
    env->ReleaseFloatArrayElements(x_arr, x, 0);

    jfloat *y = env->GetFloatArrayElements(y_arr, NULL);
    env->ReleaseFloatArrayElements(y_arr, y, 0);

    env->DeleteGlobalRef(x_arr);
    env->DeleteGlobalRef(y_arr);

    delete c;

}


extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_tsnegallery_TsneActivity_runTsneJNI(
        JNIEnv *env, jobject obj, jint n, jint dim, jint init_from_y, jfloatArray x_in){
    float *r_y = new float[n * 2];

    jfloat *x = env->GetFloatArrayElements(x_arr, NULL);

    jfloat *y = env->GetFloatArrayElements(y_arr, NULL);

    float mm = c->ss;
    for(int i = 0; i < n*2; i++){
        r_y[i] = i;
    }

    jfloatArray ret = env->NewFloatArray(n*2);
    env->SetFloatArrayRegion(ret, 0, n*2, r_y);

    delete r_y;
    return ret;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_tsnegallery_TsneActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
