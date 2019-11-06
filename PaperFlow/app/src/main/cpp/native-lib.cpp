#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <GLES2/gl2.h>
#include "common.hpp"
#include "opencv2/core/ocl.hpp"
#include <android/bitmap.h>
#include <opencv2/aruco.hpp>
#include <sstream>
#include <vector>

using namespace std;


template<typename T>
string to_string(T value) {
    ostringstream os;
    os << value;
    return os.str();
}


typedef struct {
    cv::Mat warped;
    string response;
} arucoDetails;


vector<float> colsum(vector<cv::Point> array) {
    std::vector<float> temp = {0.0f, 0.0f, 0.0f, 0.0f};
    for (int i = 0; i < array.size(); i++) {
        temp[i] += array[i].x + array[i].y;
    }
    return temp;
}


vector<float> coldiff(std::vector<cv::Point> array) {
    std::vector<float> temp = {0.0f, 0.0f, 0.0f, 0.0f};
    for (int i = 0; i < array.size(); i++) {
        temp[i] += array[i].y - array[i].x;
    }
    return temp;
}

vector<cv::Point> rectify(vector<cv::Point> inputArray) {
    vector<cv::Point> newpt;
    newpt.push_back(cv::Point(0, 0));
    newpt.push_back(cv::Point(0, 0));
    newpt.push_back(cv::Point(0, 0));
    newpt.push_back(cv::Point(0, 0));
    vector<float> sumlist = colsum(inputArray);
    vector<float> difflist = coldiff(inputArray);
    newpt[0] = inputArray[min_element(sumlist.begin(), sumlist.end()) - sumlist.begin()];
    newpt[2] = inputArray[max_element(sumlist.begin(), sumlist.end()) - sumlist.begin()];
    newpt[1] = inputArray[min_element(difflist.begin(), difflist.end()) - difflist.begin()];
    newpt[3] = inputArray[max_element(difflist.begin(), difflist.end()) - difflist.begin()];
    sumlist.clear();
    difflist.clear();
    return newpt;
}

vector<int> rectifyAruco(vector<cv::Point> inputArray) {
    vector<int> indices;

    vector<float> sumlist = colsum(inputArray);
    vector<float> difflist = coldiff(inputArray);
    indices.push_back(min_element(sumlist.begin(), sumlist.end()) - sumlist.begin());
    indices.push_back(max_element(sumlist.begin(), sumlist.end()) - sumlist.begin());
    indices.push_back(min_element(difflist.begin(), difflist.end()) - difflist.begin());
    indices.push_back(max_element(difflist.begin(), difflist.end()) - difflist.begin());
    sumlist.clear();
    difflist.clear();
    return indices;
}

cv::Mat four_point_transform(cv::Mat inputImg, vector<cv::Point> approx) {
    vector<cv::Point> approxpts = rectify(approx);

    vector<cv::Point2f> pts;
    cv::Point tl = approxpts[0];
    cv::Point tr = approxpts[1];
    cv::Point br = approxpts[2];
    cv::Point bl = approxpts[3];
    cv::Mat orig = inputImg.clone();
    auto widthA = sqrt(pow((br.x - bl.x), 2) + pow((br.y - bl.y), 2));
    auto widthB = sqrt(pow((tr.x - tl.x), 2) + pow((tr.y - tl.y), 2));
    auto maxWidth = max(widthA, widthB);


    auto heightA = sqrt(pow((tr.x - br.x), 2) + pow((tr.y - br.y), 2));
    auto heightB = sqrt(pow((tl.x - bl.x), 2) + pow((tl.y - bl.y), 2));
    auto maxHeight = max(heightA, heightB);


    pts.push_back(cv::Point2f(0.0f, 0.0f));
    pts.push_back(cv::Point2f(maxWidth - 1, 0.0f));
    pts.push_back(cv::Point2f(maxWidth - 1, maxHeight - 1));
    pts.push_back(cv::Point2f(0.0f, maxHeight - 1));
    std::vector<cv::Point2f> approxfloat(approxpts.begin(), approxpts.end());

    cv::Mat M = cv::getPerspectiveTransform(approxfloat, pts);
    cv::warpPerspective(orig, orig, M, cv::Size(maxWidth, maxHeight));

    return orig;
}

// TODO: add code for aruco
arucoDetails
getArucoMarkers(cv::Mat image) {

    cv::cvtColor(image, image, cv::COLOR_BGRA2BGR);
    cv::rotate(image, image, cv::ROTATE_90_CLOCKWISE);

    arucoDetails arucoDetails1;

    vector<int> markerIDs;
    vector<cv::Point> points, rotPoints;
    vector<vector<cv::Point2f>> markerCorners, rejectedCandidates;
    cv::Ptr<cv::aruco::Dictionary> markerDictionary = cv::aruco::getPredefinedDictionary(
            cv::aruco::PREDEFINED_DICTIONARY_NAME::DICT_5X5_1000);


    cv::aruco::detectMarkers(image, markerDictionary, markerCorners, markerIDs);

    LOGD("aruco marker size=%d", markerIDs.size());

    if (markerIDs.size() == 4) {

        for (auto &point:markerCorners[0]) {
            rotPoints.push_back(point);
        }

        vector<int> indicesRot = rectifyAruco(rotPoints);


        if (indicesRot[1] == 0) {
            cv::rotate(image, image, cv::ROTATE_180);

        } else if (indicesRot[2] == 0) {
            //LOGD("improper rotation %d",2);
            cv::rotate(image, image, cv::ROTATE_90_COUNTERCLOCKWISE);


        } else if (indicesRot[3] == 0) {
            //LOGD("improper rotation %d",3);

            cv::rotate(image, image, cv::ROTATE_90_CLOCKWISE);


        }


        cv::aruco::detectMarkers(image, markerDictionary, markerCorners, markerIDs);

        for (auto &corners:markerCorners) {
            cv::Point pt1 = corners[0];
            cv::Point pt3 = corners[2];


            cv::Point newPt = cv::Point((pt1.x + pt3.x) / 2, (pt1.y + pt3.y) / 2);
            points.push_back(newPt);
        }
        markerCorners.clear();
        vector<int> indices = rectifyAruco(points);
        string identifier;
        for (int i = 0; i < indices.size(); i++) {
            string markerstr = to_string(markerIDs[indices[i]]);
            if (markerstr.length() < 4)
                markerstr = std::string(4 - markerstr.length(), '0').append(markerstr);

            identifier += markerstr;
        }
        LOGD("aruco identifier=%s", identifier.c_str());


        cv::Mat warped = four_point_transform(image, points);

        cv::resize(warped, warped, cv::Size(800, 800));
        arucoDetails1.response = identifier;
        arucoDetails1.warped = warped;
        warped.release();
        indices.clear();
        indicesRot.clear();
        markerIDs.clear();
    }
    return arucoDetails1;
}


vector<cv::Mat> getOuterBlobs(cv::Mat image, int **pCoords,
                              int length_outer, string warpName) {

    vector<cv::Mat> rois;

    for (int i = 0; i < length_outer; i++) {
        auto coords = pCoords[i];

        cv::Rect roi = cv::Rect(coords[0], coords[1], coords[2], coords[3]);
        cv::Mat region = image(roi);
        cv::Mat gray = region.clone();
        ostringstream strstream1;
        strstream1 << warpName << i << ".png";
        string path0(strstream1.str());
        cv::imwrite(path0, region);

//        cv::cvtColor(region, gray, cv::COLOR_BGR2GRAY);
//
//        cv::threshold(gray, gray, 0, 255, cv::THRESH_BINARY + cv::THRESH_OTSU);
//        cv::resize(gray, gray, cv::Size(32, 32), 0, 0, cv::INTER_AREA);


        rois.push_back(region);


        gray.release();
        region.release();

    }

    //cv::imwrite(path0, image);
    return rois;


}

vector<string>
getOmrResults(cv::Mat image, int **pCoords, int **pxrange, int len, int len2, int minThreshold,
              int maxThreshold,
              int minArea, int maxArea, float minCircularity,
              float maxCircularity, float minConvexity, float minInertiaRatio, string warpName) {
    cv::SimpleBlobDetector::Params params;
    // Filter by threshold
    params.minThreshold = minThreshold;
    params.maxThreshold = maxThreshold;

    // Filter by area
    params.filterByArea = true;
    params.minArea = minArea;
    params.maxArea = maxArea;


    // Filter by circularity
    params.filterByCircularity = true;
    params.minCircularity = minCircularity;
    params.maxCircularity = maxCircularity;

    // Filter by convexity
    params.filterByConvexity = true;
    params.minConvexity = minConvexity;

    // Filter by inertia
    params.filterByInertia = false;
    params.minInertiaRatio = minInertiaRatio;
    vector<string> mcqOptions = {"A", "B", "C", "D"};
    vector<string> answers;
    LOGD("keypoint size = %d", len);

    for (int i = 0; i < len; i++) {
        auto coords = pCoords[i];

        cv::Rect roi = cv::Rect(coords[0], coords[1], coords[2], coords[3]);
        cv::Mat region = image(roi);
        cv::Ptr<cv::SimpleBlobDetector> detector = cv::SimpleBlobDetector::create(params);
        vector<cv::KeyPoint> keypoints;
        detector->detect(region, keypoints);
        cv::Mat imwKeypoints = region.clone();
        cv::drawKeypoints(region, keypoints, imwKeypoints, cv::Scalar(0, 255, 0),
                          cv::DrawMatchesFlags::DRAW_RICH_KEYPOINTS);
//        ostringstream strstream1;
//
//        strstream1 << warpName << i << ".png";
//        string path0(strstream1.str());
//        cv::imwrite(path0, region);
        imwKeypoints.release();
        if (keypoints.size() > 0) {
            for (cv::KeyPoint point:keypoints) {
                bool found = false;
                for (int j = 0; j < len2; j++) {
                    if (int(point.pt.x) < pxrange[i][j]) {
                        LOGD("keypoint size = %s", mcqOptions[j].c_str());

                        answers.push_back(mcqOptions[j]);
                        found = true;
                    }
                    if (found) break;
                }
            }
        }


    }
    return answers;

}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_paperflow_MainActivity_processFrame(JNIEnv *env, jclass object,
                                                          jobject bitmap,
                                                          jobjectArray coordsArrayList,
                                                          jstring typestr, jstring warpName_) {
    const char *type = env->GetStringUTFChars(typestr, 0);
    int ret;
    AndroidBitmapInfo info;
    void *pixelsorig = 0;
    const char *warpth = env->GetStringUTFChars(warpName_, 0);


    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        return 0;
    }

    if (info.format !=
        ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return 0;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixelsorig)) < 0) {
    }


    cv::Mat mbgra(info.height, info.width, CV_8UC4, pixelsorig);


    jclass coordCls = env->FindClass("com/example/paperflow/Coords");

    jmethodID getCoords = env->GetMethodID(coordCls, "getCoords",
                                           "()[I");
    jmethodID getXrange = env->GetMethodID(coordCls, "getXrange",
                                           "()[I");
    int len1 = env->GetArrayLength(coordsArrayList);
    int **localArray;
    // allocate localArray using len1
    localArray = new int *[len1];
    jobject jCoord = env->GetObjectArrayElement(coordsArrayList, 0);

    jintArray dim = (jintArray) env->CallObjectMethod(jCoord, getCoords);

    int len2 = env->GetArrayLength(dim);

    for (int i = 0; i < len1; ++i) {
        jobject jCoordtemp = env->GetObjectArrayElement(coordsArrayList, i);

        jintArray oneDim = (jintArray) env->CallObjectMethod(jCoordtemp, getCoords);

        jint *element = env->GetIntArrayElements(oneDim, 0);
        //allocate localArray[i] using len2
        localArray[i] = new int[len2];
        for (int j = 0; j < len2; ++j) {
            localArray[i][j] = element[j];
        }
        (env)->ReleaseIntArrayElements(oneDim, element, 0);
        (env)->DeleteLocalRef(oneDim);
    }
    std::string type_paper(type);

    if (type_paper == "omr") {
        int **localArrayXrange;
        // allocate localArray using len1
        localArrayXrange = new int *[len1];
        jintArray dimxrange = (jintArray) env->CallObjectMethod(jCoord, getXrange);
        jclass strClass = env->FindClass("java/lang/String");
        jobjectArray strArray = env->NewObjectArray(len1, strClass, env->NewStringUTF(""));
        int len_xrange = env->GetArrayLength(dimxrange);
        for (int i = 0; i < len1; ++i) {
            jobject jCoordtemp = env->GetObjectArrayElement(coordsArrayList, i);

            jintArray oneDim = (jintArray) env->CallObjectMethod(jCoordtemp, getXrange);

            jint *element = env->GetIntArrayElements(oneDim, 0);
            //allocate localArray[i] using len2
            localArrayXrange[i] = new int[len_xrange];
            for (int j = 0; j < len_xrange; ++j) {
                localArrayXrange[i][j] = element[j];
            }
            (env)->ReleaseIntArrayElements(oneDim, element, 0);
            (env)->DeleteLocalRef(oneDim);
        }

        vector<string> return_string = getOmrResults(mbgra, localArray, localArrayXrange, len1,
                                                     len_xrange,
                                                     0, 150, 200, 500, 0.6, 0.9, 0.9, 0.01, warpth);
        for (int i = 0; i < return_string.size(); i++) {
            env->SetObjectArrayElement(strArray, i, env->NewStringUTF(return_string[i].c_str()));

        }


        return strArray;

    } else {


        jclass matCls = env->FindClass("org/opencv/core/Mat");

        jmethodID matConstructor = env->GetMethodID(matCls, "<init>", "()V");

        jmethodID getPtrMethod = env->GetMethodID(matCls, "getNativeObjAddr", "()J");

        vector<cv::Mat> rois = getOuterBlobs(mbgra,
                                             localArray,
                                             len1, warpth);

        jobjectArray matArray = env->NewObjectArray(rois.size(), matCls, 0);


        if (!rois.empty()) {

            for (int count = 0; count < rois.size(); count++) {
                jobject jMat = env->NewObject(matCls, matConstructor);
                cv::Mat &native_image = *(cv::Mat *) env->CallLongMethod(jMat,
                                                                         getPtrMethod);

                native_image = rois[count];
                env->SetObjectArrayElement(matArray, count, jMat);
            }
        }

        return matArray;

    }

    env->ReleaseStringUTFChars(warpName_, warpth);

    AndroidBitmap_unlockPixels(env, bitmap);
    env->ReleaseStringUTFChars(typestr, type);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_paperflow_MainActivity_processaruco(JNIEnv *env, jclass type,
                                                          jobject bitmap) {

    // TODO



    jclass matCls = env->FindClass("org/opencv/core/Mat");


    jmethodID matConstructor = env->GetMethodID(matCls, "<init>", "()V");

    jmethodID getPtrMethod = env->GetMethodID(matCls, "getNativeObjAddr", "()J");

    jclass arucoPropClass = env->FindClass("com/example/paperflow/ArucoProps");

    jmethodID arucoPropCons = env->GetMethodID(arucoPropClass, "<init>", "()V");

    jmethodID setIdentifier = env->GetMethodID(arucoPropClass, "setIdentifier",
                                               "(Ljava/lang/String;)V");

    jmethodID setWarped = env->GetMethodID(arucoPropClass, "setWarped",
                                           "(Lorg/opencv/core/Mat;)V");

    int ret;
    AndroidBitmapInfo info;
    void *pixelsorig = 0;


    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        return 0;
    }

    if (info.format !=
        ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return 0;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixelsorig)) < 0) {
    }

    // init our output image
    cv::Mat mbgra(info.height, info.width, CV_8UC4, pixelsorig);


    arucoDetails arucoDetails1 = getArucoMarkers(mbgra);

    jobject jMat = env->NewObject(matCls, matConstructor);
    cv::Mat &native_image = *(cv::Mat *) env->CallLongMethod(jMat,
                                                             getPtrMethod);
    native_image = arucoDetails1.warped;


    jobject arucoProp = env->NewObject(arucoPropClass, arucoPropCons);


    env->CallVoidMethod(arucoProp, setWarped, jMat);
    env->CallVoidMethod(arucoProp, setIdentifier,
                        env->NewStringUTF(arucoDetails1.response.c_str()));
    return arucoProp;


}